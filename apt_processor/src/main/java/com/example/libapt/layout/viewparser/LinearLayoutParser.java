package com.example.libapt.layout.viewparser;

import com.example.libapt.layout.LayoutBindingMgr;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;

public class LinearLayoutParser extends ViewGroupParser {

    @Override
    public ClassName getClassName() {
        return ClassName.get("android.widget", "LinearLayout");
    }

    @Override
    public void setAttr(LayoutBindingMgr.LayoutParseContext parseContext, String viewVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:orientation":
                parseContext.methodSpecBuilder.addStatement("$N.setOrientation($L)", viewVarName,
                        attrValue.equals("vertical") ? "LinearLayout.VERTICAL" : "LinearLayout.HORIZONTAL");
                return;
        }
        super.setAttr(parseContext, viewVarName, attrName, attrValue, messager);
    }

}
