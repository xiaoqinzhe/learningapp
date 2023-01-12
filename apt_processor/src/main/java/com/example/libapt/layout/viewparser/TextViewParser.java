package com.example.libapt.layout.viewparser;

import com.example.libapt.layout.LayoutBindingMgr;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;

public class TextViewParser extends ViewParser {

    @Override
    public ClassName getClassName() {
        return ClassName.get("android.widget", "TextView");
    }

    @Override
    public void setAttr(LayoutBindingMgr.LayoutParseContext parseContext, String viewVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:text":
                TextResult result = parseText(attrValue);
                if (result.isR) {
                    parseContext.methodSpecBuilder.addStatement("$N.setText(" + result.text + ")", viewVarName, parseContext.R_CLASS);
                } else {
                    parseContext.methodSpecBuilder.addStatement("$N.setText($L)", result.text);
                }
                return;
        }
        super.setAttr(parseContext, viewVarName, attrName, attrValue, messager);
    }
}
