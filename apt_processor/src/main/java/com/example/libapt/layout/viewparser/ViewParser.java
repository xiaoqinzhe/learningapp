package com.example.libapt.layout.viewparser;

import com.example.libapt.layout.LayoutBindingMgr;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

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
    public void setLayoutAttr(LayoutBindingMgr.LayoutParseContext parseContext, String lpVarName, String attrName, String attrValue, Messager messager) {

    }

    @Override
    public void setAttr(LayoutBindingMgr.LayoutParseContext parseContext, String viewVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:id":
                parseContext.methodSpecBuilder.addStatement("$N.setId($T.id.$L)", viewVarName, parseContext.R_CLASS, attrValue.substring(5));
                return;
        }
        messager.printMessage(Diagnostic.Kind.WARNING, "attr is not supported: " + attrName);
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

    // TypedValue.applyDimension
//    protected SizeResult parseSize(String attrValue) {
//
//    }

    protected class TextResult {
        boolean isR;
        String text;
    }

}
