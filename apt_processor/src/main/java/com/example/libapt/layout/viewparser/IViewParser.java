package com.example.libapt.layout.viewparser;

import com.example.libapt.layout.LayoutBindingMgr;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;

public interface IViewParser {

    ClassName getClassName();

    ClassName getLPClassName();

    void setAttr(LayoutBindingMgr.LayoutParseContext parseContext, String viewVarName, String attrName, String attrValue, Messager messager);

    void setLayoutAttr(LayoutBindingMgr.LayoutParseContext parseContext, String lpVarName, String attrName, String attrValue, Messager messager);
}
