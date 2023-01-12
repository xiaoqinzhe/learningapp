package com.example.libapt.layout.viewparser;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;

import javax.annotation.processing.Messager;

public class ViewGroupParser extends ViewParser {

    @Override
    public ClassName getLPClassName() {
        return ClassName.get("android.view", "ViewGroup", "MarginLayoutParams");
    }

    @Override
    public void setLayoutAttr(MethodSpec.Builder methodSpecBuilder, String lpVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:layout_width":
            case "android:layout_height":
                String format = "$N." + attrName.substring(15) + "=";
                if (attrValue.equals("match_parent") || attrValue.equals("fill_parent")) {
                    methodSpecBuilder.addStatement(format + "$T.$L", lpVarName, getLPClassName(), "MATCH_PARENT");
                } else if (attrValue.equals("wrap_content")) {
                    methodSpecBuilder.addStatement(format + "$T.$L", lpVarName, getLPClassName(), "WRAP_CONTENT");
                }
                break;
        }
    }

}
