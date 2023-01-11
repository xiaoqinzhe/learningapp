package com.example.libapt.layout.viewparser;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;

public interface IViewParser {

    ClassName getClassName();

    ClassName getLPClassName();

    void setAttr(MethodSpec.Builder methodSpecBuilder, String viewVarName, String attrName, String attrValue, Messager messager);

    void setLayoutAttr(MethodSpec.Builder methodSpecBuilder, String lpVarName, String attrName, String attrValue, Messager messager);
}
