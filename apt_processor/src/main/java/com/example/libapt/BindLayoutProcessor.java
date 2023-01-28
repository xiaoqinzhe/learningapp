package com.example.libapt;

import com.example.libannotation.BindActivity;
import com.example.libannotation.BindLayouts;
import com.example.libannotation.BindView;
import com.example.libapt.layout.LayoutBindingMgr;
import com.google.auto.service.AutoService;
//import com.sun.tools.javac.code.Type;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import jdk.nashorn.internal.ir.FunctionNode;

@AutoService(Processor.class)
public class BindLayoutProcessor extends AbstractProcessor {

    private Elements mElementUtils;
    private Types mTypeUtils;
    private Filer mFiler; // 文件生成
    private Messager mMessager; // 日志
    private LayoutBindingMgr layoutBindingMgr;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        // some utils
        mElementUtils = processingEnv.getElementUtils();
        mTypeUtils = processingEnv.getTypeUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        layoutBindingMgr = new LayoutBindingMgr(mElementUtils, mTypeUtils, mFiler, mMessager);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new HashSet<>();
        types.add(BindLayouts.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(BindLayouts.class)) {
            layoutBindingMgr.handleElement(element);
        }
        String findClsName = "com.example.learningapp.views.myviews.MySimpleViewGroup";
        Set<? extends Element> elementSet = roundEnv.getRootElements();
        for (Element element : elementSet) {
            if (element.toString().equals(findClsName)) {
//                String superName = ((Type.ClassType) element.asType()).supertype_field.toString();
//                System.out.println("superName = " + superName);
                findPlatformParent0(findClsName, element.asType());
                break;
            }
        }

        return false;
    }

    private String findPlatformParent0(String fullTagName, Object typeMirror) {
        Class<?> clazz = typeMirror.getClass();
        try {
            Field field = clazz.getField("supertype_field");
            Object obj = field.get(typeMirror);

            String parentName = obj.toString();
            System.err.println("findPlatformParent0 : [parentName=" + parentName + ", fullTagName=" + fullTagName + "]");
//            if (TranslatorCache.getTranslator(parentName) == null) {
//                return findPlatformParent0(fullTagName, obj);
//            }

            return parentName;
        } catch (Exception e) {
            System.err.println("findPlatformParent0 err fullTagName : " + fullTagName);
        }

        return null;
    }

}
