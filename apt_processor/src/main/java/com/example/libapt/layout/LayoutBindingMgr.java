package com.example.libapt.layout;

import com.example.libannotation.BindLayouts;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import jdk.nashorn.internal.ir.FunctionNode;

public class LayoutBindingMgr {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer; // 文件生成
    private Messager messager; // 日志

    public LayoutBindingMgr(Elements elements, Types types, Filer filer, Messager messager) {
        this.elementUtils = elements;
        this.typeUtils = types;
        this.filer = filer;
        this.messager = messager;
    }

    public void handleElement(Element element) {
        messager.printMessage(Diagnostic.Kind.NOTE, "handleElement " + element);
        // 1. get layout xml
        String[] layouts = element.getAnnotation(BindLayouts.class).layouts();
        for (String layoutName : layouts) {
            messager.printMessage(Diagnostic.Kind.NOTE, "layoutName=" + layoutName);
        }

    }

}
