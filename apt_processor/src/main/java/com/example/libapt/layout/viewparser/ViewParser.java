package com.example.libapt.layout.viewparser;

import com.example.libapt.layout.LayoutBindingMgr;
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
    public void setAttr(LayoutBindingMgr.LayoutParseContext parseContext, String viewVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:id":
                break;
        }

    }

    protected TextResult parseText(String attrValue) {
        TextResult result = new TextResult();
        if (attrValue.startsWith("@string/")) {
            result.isR = true;
            result.text = "context.getResources().getString($T.string." + attrValue.substring(8) + ")";
        } else {
            result.isR = false;
            result.text = attrValue;
        }
        return result;
    }

    protected class TextResult {
        boolean isR;
        String text;
    }

}
