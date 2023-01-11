package com.example.libapt.layout.viewparser;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;

public class ViewGroupParser extends ViewParser {

    @Override
    public ClassName getLPClassName() {
        return ClassName.get("android.view", "ViewGroup", "LayoutParams");
    }

    @Override
    public void setLayoutAttr(MethodSpec.Builder methodSpecBuilder, String lpVarName, String attrName, String attrValue, Messager messager) {
        super.setLayoutAttr(methodSpecBuilder, lpVarName, attrName, attrValue, messager);
    }
}
