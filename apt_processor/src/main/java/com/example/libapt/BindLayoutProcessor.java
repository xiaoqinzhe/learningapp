package com.example.libapt;

import com.example.libannotation.BindActivity;
import com.example.libannotation.BindLayouts;
import com.example.libannotation.BindView;
import com.example.libapt.layout.LayoutBindingMgr;
import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
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
        return false;
    }

}
