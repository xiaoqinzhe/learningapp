package com.example.libapt.layout.viewparser;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;

public class ViewParser implements IViewParser {
    @Override
    public ClassName getClassName() {
        return ClassName.get("android.view", "View");
    }

    @Override
    public ClassName getLPClassName() {
        return null;
    }

    @Override
    public void setLayoutAttr(MethodSpec.Builder methodSpecBuilder, String lpVarName, String attrName, String attrValue, Messager messager) {

    }

    @Override
    public void setAttr(MethodSpec.Builder methodSpecBuilder, String viewVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:id":
                break;
        }

    }
}
