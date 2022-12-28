package com.example.libapt.layout;

import com.example.libannotation.BindLayouts;
import com.example.libapt.utils.LayoutScanner;

import java.io.File;
import java.io.IOException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class LayoutBindingMgr {

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer; // 文件生成
    private Messager messager; // 日志

    private String modulePath;

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
            }
            note("handleElement path =" + path);

            // 2. parse xml
        }

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

    private void err(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

}
