package com.example.libapt.layout;

import com.example.libannotation.BindLayouts;
import com.example.libapt.layout.viewparser.IViewParser;
import com.example.libapt.layout.viewparser.ViewParser;
import com.example.libapt.utils.LayoutScanner;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LayoutBindingMgr {

    private static List<String> modulePaths = new ArrayList<>();

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer; // 文件生成
    private Messager messager; // 日志

    private String modulePath;
    private String valueFilePath;

    private DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

    public LayoutBindingMgr(Elements elements, Types types, Filer filer, Messager messager) {
        this.elementUtils = elements;
        this.typeUtils = types;
        this.filer = filer;
        this.messager = messager;
        initModulePath(filer);
    }

    public void handleElement(Element element) {
        if (checkIllegalState()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "handleElement illegalState");
            return;
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "handleElement " + element);

        String[] layouts = element.getAnnotation(BindLayouts.class).layouts();
        for (String layoutName : layouts) {
            note("handleElement layoutName=" + layoutName);
            // 1. get layout xml
            String path = LayoutScanner.findLayout(modulePath, layoutName, messager);
            if (path == null) {
                err("handleElement path is null");
                return;
            }
            note("handleElement path =" + path);

            // 2. parse xml
            note("parse xml...");
            String layoutClassName = LayoutBindingConfig.getLayoutClassName(layoutName);
            ClassName layoutClz = ClassName.get(LayoutBindingConfig.GENERATED_PACKAGE_NAME, layoutClassName);
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(layoutClassName)
                    .addModifiers(Modifier.PUBLIC);
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(path);
                org.w3c.dom.Element documentElement = document.getDocumentElement();

                // method createView
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("createBinding")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(layoutClz)
                        .addParameter(LayoutBindingConfig.CONTEXT, "context");

                // new layoutClz()
                methodSpecBuilder.addStatement("$T binding = new $T()", layoutClz, layoutClz);

                LayoutParseContext parseContext = new LayoutParseContext(typeSpecBuilder, methodSpecBuilder);
                parseContext.bindClsName = "binding";
                transversalNode(documentElement, parseContext, null);
                typeSpecBuilder.addMethod(methodSpecBuilder.build());

                // method createView
                MethodSpec.Builder createViewMethod = MethodSpec.methodBuilder("createView")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(LayoutBindingConfig.VIEW)
                        .addParameter(LayoutBindingConfig.CONTEXT, "context")
                        .addStatement("return createBinding(context).rootView");
                typeSpecBuilder.addMethod(createViewMethod.build());

                // method inflate
                buildInflateMethod(typeSpecBuilder, layoutClassName, layoutClz);

            } catch (ParserConfigurationException | SAXException | IOException e) {
                err(e.getMessage());
            }

            // 3. write file
            TypeSpec typeSpec = typeSpecBuilder.build();
            JavaFile javaFile = JavaFile.builder(LayoutBindingConfig.GENERATED_PACKAGE_NAME, typeSpec)
                    .indent("    ")
                    .build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                err(e.getMessage());
            }
        }

    }

    private void buildInflateMethod(TypeSpec.Builder typeSpecBuilder, String layoutClassName, ClassName layoutClz) {
        /**
         * public static View inflate(LayoutInflater inflater, int layoutId, ViewGroup parent, boolean attach) {
         *         return createView(inflater, layoutId, parent, attach);
         *     }
         */
        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("inflate")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(layoutClz)
                .addParameter(LayoutBindingConfig.LAYOUT_INFLATER, "inflater")
                .addParameter(ClassName.INT, "layoutId")
                .addParameter(LayoutBindingConfig.VIEW_GROUP, "root")
                .addParameter(ClassName.BOOLEAN, "attachToRoot")
                .addStatement(layoutClassName + " binding = createBinding(inflater.getContext())") // todo add parent
                .beginControlFlow("if (root != null)")
                .beginControlFlow("if (attachToRoot)")
                .addStatement("root.addView(binding.rootView)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return binding");
        typeSpecBuilder.addMethod(methodSpecBuilder.build());

        methodSpecBuilder = MethodSpec.methodBuilder("inflateView")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(LayoutBindingConfig.VIEW)
                .addParameter(LayoutBindingConfig.LAYOUT_INFLATER, "inflater")
                .addParameter(ClassName.INT, "layoutId")
                .addParameter(LayoutBindingConfig.VIEW_GROUP, "root")
                .addParameter(ClassName.BOOLEAN, "attachToRoot")
                .addStatement("return inflate(inflater, layoutId, root, attachToRoot).rootView");
        typeSpecBuilder.addMethod(methodSpecBuilder.build());
    }

    private void transversalNode(Node node, LayoutParseContext parseContext, NodeParseInfo parentInfo) {
        // parse node and attrs
        NodeList nodeList = node.getChildNodes();
        NodeParseInfo nodeParseInfo = parseNode(node, parseContext, parentInfo);
        if (nodeParseInfo == null) {
            return;
        }

        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                transversalNode(childNode, parseContext, nodeParseInfo);
            }
        }

        if (parentInfo == null) {
            parseContext.methodSpecBuilder.addStatement("return $L", parseContext.bindClsName);
        }
    }

    private NodeParseInfo parseNode(Node node, LayoutParseContext parseContext, NodeParseInfo parentInfo) {
        note("parseNode Node type = " + node.getNodeType() +
                ", name = " + node.getNodeName() +
                ", value = " + node.getNodeValue());
        /**
         * view = new View()
         * view.setAttr
         * lp = new LayoutParams(); view.setLP(lp)
         * parent.addView(view)
         *
         * style -> find file: 1. android/sdk/res  2. app/src/main/res
         * custom view -> get supper class, parse, set custom method by invoking method initAttr() if find
         * merge/include -> get xml, transversal
         *
         */
        String nodeName = node.getNodeName();
        IViewParser viewParser = LayoutBindingConfig.getViewParser(nodeName);
        if (viewParser == null) {
            note("viewParser is null. node=" + nodeName);
            return null;
        }
        String viewVarName = "view" + parseContext.viewNameIndex;
        ClassName viewClass = null;
        if (nodeName.contains(".")) {
            // todo
        } else {
            viewClass = viewParser.getClassName();
        }
        parseContext.methodSpecBuilder.addStatement("$T $N = new $T(context)", viewClass, viewVarName, viewClass);

        if (parentInfo == null) {
            parseContext.typeSpecBuilder.addField(viewClass, "rootView", Modifier.PUBLIC);
            parseContext.methodSpecBuilder.addStatement(parseContext.bindClsName + "." + "rootView=" + viewVarName);
        }

        String lpVarName = "lp" + parseContext.viewNameIndex++;

        IViewParser parentViewParser;
        if (parentInfo == null) {
            parentViewParser = LayoutBindingConfig.getViewParser("ViewGroup");
        } else {
            parentViewParser = parentInfo.viewParser;
        }
        ClassName lpClsName = parentViewParser.getLPClassName();
        parseContext.methodSpecBuilder.addStatement("$T $N = new $T($T.WRAP_CONTENT, $T.WRAP_CONTENT)",
                lpClsName, lpVarName, lpClsName, lpClsName, lpClsName);

        NamedNodeMap map = node.getAttributes();
        if (map == null) return parentInfo;
        for (int i = 0; i < map.getLength(); ++i) {
            Node attr = map.item(i);
            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();

            note("parseNode attr type = " + attr.getNodeType() +
                    ", name = " + attrName +
                    ", value = " + attrValue);

            if (attrName.startsWith("android:layout_")) {
                // lp
                parentViewParser.setLayoutAttr(parseContext, lpVarName, attrName, attrValue, messager);
            } else {
                // attr
                if (attrName.equals("android:id")) {
                    // add view field
                    String viewId = attrValue.substring(5);
                    String viewFieldName = LayoutBindingConfig.getViewName(viewId);
                    parseContext.typeSpecBuilder.addField(viewClass, viewFieldName, Modifier.PUBLIC);
                    parseContext.methodSpecBuilder.addStatement(parseContext.bindClsName + "." + viewFieldName + " = " + viewVarName);
                }
                viewParser.setAttr(parseContext, viewVarName, attrName, attrValue, messager);
            }
        }

        parseContext.methodSpecBuilder.addStatement("$L.setLayoutParams($L)", viewVarName, lpVarName);
        if (parentInfo != null) {
            parseContext.methodSpecBuilder.addStatement("$L.addView($L)", parentInfo.viewVarName, viewVarName);
        }

        parseContext.methodSpecBuilder.addCode("\n");

        return new NodeParseInfo(viewVarName, viewParser, viewClass);
    }

    private void initModulePath(Filer filer) {
        try {
            JavaFileObject javaFileObject = filer.createSourceFile("Temp");
            String path = javaFileObject.toUri().getPath();
            note("path=" + path);
            int index = path.indexOf("build/generated/");
            if (index < 0) return;
            modulePath = path.substring(0, index);
            modulePaths.add(modulePath);
            note("modulePath=" + modulePath + ", path size=" + modulePaths.size());

//            valueFilePath = modulePath + "build/intermediates/incremental/mergeXxxResource/merge.dir/values/values.xml"
//                    note("path=" + path);

            javaFileObject.openWriter().close();
            javaFileObject.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIllegalState() {
        return modulePath == null || modulePath.length() == 0;
    }

    private void note(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void warn(String msg) {
        messager.printMessage(Diagnostic.Kind.WARNING, msg);
    }

    private void err(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    public static class LayoutParseContext {
        public TypeSpec.Builder typeSpecBuilder;
        public MethodSpec.Builder methodSpecBuilder;
        public String bindClsName;
        // todo pkg search
        public ClassName R_CLASS = ClassName.get("com.example.learningapp", "R");
        int viewNameIndex;

        public LayoutParseContext(TypeSpec.Builder typeSpecBuilder, MethodSpec.Builder methodSpecBuilder) {
            this.typeSpecBuilder = typeSpecBuilder;
            this.methodSpecBuilder = methodSpecBuilder;
        }
    }

    private static class NodeParseInfo {
        String viewVarName;
        IViewParser viewParser;
        ClassName viewClass;

        public NodeParseInfo(String viewVarName, IViewParser viewParser, ClassName viewClass) {
            this.viewVarName = viewVarName;
            this.viewParser = viewParser;
            this.viewClass = viewClass;
        }

    }

}
