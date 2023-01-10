package com.example.libapt.layout;

import com.example.libannotation.BindLayouts;
import com.example.libapt.utils.LayoutScanner;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

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

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer; // 文件生成
    private Messager messager; // 日志

    private String modulePath;

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
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(layoutClassName)
                    .addModifiers(Modifier.PUBLIC);
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(path);
                org.w3c.dom.Element documentElement = document.getDocumentElement();
                MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("createView");
                LayoutParseContext parseContext = new LayoutParseContext(typeSpecBuilder, methodSpecBuilder);
                transversalNode(documentElement, parseContext, null);
                typeSpecBuilder.addMethod(methodSpecBuilder.build());
            } catch (ParserConfigurationException | SAXException | IOException e) {
                err(e.getMessage());
            }

            // 3. write file
            TypeSpec typeSpec = typeSpecBuilder.build();
            JavaFile javaFile = JavaFile.builder(LayoutBindingConfig.GENERATED_PACKAGE_NAME, typeSpec).build();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                err(e.getMessage());
            }
        }

    }

    private void transversalNode(Node node, LayoutParseContext parseContext, NodeParseInfo parentInfo) {
        // parse node and attrs
        NodeList nodeList = node.getChildNodes();
        NodeParseInfo nodeParseInfo = parseNode(node, parseContext, parentInfo);
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                transversalNode(childNode, parseContext, nodeParseInfo);
            }
        }
    }

    private NodeParseInfo parseNode(Node node, LayoutParseContext parseContext, NodeParseInfo parentInfo) {
        note("parseNode Node type = " + node.getNodeType() +
                ", name = " + node.getNodeName() +
                ", value = " + node.getNodeValue());
        // new View()
        // setAttr, setLayoutParams
        // parent.addView
        NamedNodeMap map = node.getAttributes();
        if (map == null) return parentInfo;
        for (int i = 0; i < map.getLength(); ++i) {
            Node attr = map.item(i);
            note("parseNode attr type = " + attr.getNodeType() +
                    ", name = " + attr.getNodeName() +
                    ", value = " + attr.getNodeValue());
        }
        return new NodeParseInfo();
    }

    private void initModulePath(Filer filer) {
        try {
            JavaFileObject javaFileObject = filer.createSourceFile("Temp");
            String path = javaFileObject.toUri().getPath();
            note("path=" + path);
            int index = path.indexOf("build/generated/");
            if (index < 0) return;
            modulePath = path.substring(0, index);
            note("modulePath=" + modulePath);
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

    private static class LayoutParseContext {
        TypeSpec.Builder typeSpecBuilder;
        MethodSpec.Builder methodSpecBuilder;

        public LayoutParseContext(TypeSpec.Builder typeSpecBuilder, MethodSpec.Builder methodSpecBuilder) {
            this.typeSpecBuilder = typeSpecBuilder;
            this.methodSpecBuilder = methodSpecBuilder;
        }
    }

    private static class NodeParseInfo {

    }

}
