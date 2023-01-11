package com.example.libapt.layout.viewparser;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;

public class LinearLayoutParser extends ViewGroupParser {

    @Override
    public ClassName getClassName() {
        return ClassName.get("android.widget", "LinearLayout");
    }

    @Override
    public void setAttr(MethodSpec.Builder methodSpecBuilder, String viewVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:orientation":
                methodSpecBuilder.addStatement("$N.setOrientation($L)", viewVarName,
                        attrValue.equals("vertical") ? "LinearLayout.VERTICAL" : "LinearLayout.HORIZONTAL");
                return;
        }
        super.setAttr(methodSpecBuilder, viewVarName, attrName, attrValue, messager);
    }

}
