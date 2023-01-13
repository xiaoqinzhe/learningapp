package com.example.libapt.layout.viewparser;

import com.example.libapt.layout.LayoutBindingMgr;
import com.squareup.javapoet.ClassName;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class ViewGroupParser extends ViewParser {

    @Override
    public ClassName getLPClassName() {
        return ClassName.get("android.view", "ViewGroup", "MarginLayoutParams");
    }

    @Override
    public void setLayoutAttr(LayoutBindingMgr.LayoutParseContext parseContext, String lpVarName, String attrName, String attrValue, Messager messager) {
        switch (attrName) {
            case "android:layout_width":
            case "android:layout_height":
                String format = "$N." + attrName.substring(15) + "=";
                if (attrValue.equals("match_parent") || attrValue.equals("fill_parent")) {
                    parseContext.methodSpecBuilder.addStatement(format + "$T.$L", lpVarName, getLPClassName(), "MATCH_PARENT");
                } else if (attrValue.equals("wrap_content")) {
                    parseContext.methodSpecBuilder.addStatement(format + "$T.$L", lpVarName, getLPClassName(), "WRAP_CONTENT");
                }
                break;
            case "android:layout_marginTop":
            case "android:layout_marginStart":
            case "android:layout_marginEnd":
            case "android:layout_marginBottom":
                String methodName = "";
                switch (attrName) {
                    case "android:layout_marginTop": methodName = "topMargin="; break;
                    case "android:layout_marginBottom": methodName = "bottomMargin="; break;
                    case "android:layout_marginStart": methodName = "setMarginStart"; break;
                    case "android:layout_marginEnd": methodName = "setMarginEnd"; break;
                }
                if (attrValue.startsWith("@dimen/")) {
                    parseContext.methodSpecBuilder.addStatement(
                            "$N." + methodName + "(context.getResources().getDimensionPixelSize($T.dimen.$L))",
                            lpVarName, parseContext.R_CLASS, attrValue.substring(7)
                    );
                } else {
                    int size = 0;
                    for (int i = 0; i < attrValue.length(); ++i) {
                        if (attrValue.charAt(i) <= '9' && attrValue.charAt(i) >= '0') {
                            size = size * 10 + attrValue.charAt(i) - '0';
                        } else {
                            break;
                        }
                    }
                    if (attrValue.endsWith("dp")) {
                        parseContext.methodSpecBuilder.addStatement(
                                "$N." + methodName + "((int) ($L * context.getResources().getDisplayMetrics().density + 0.5f))",
                                lpVarName, size
                        );
                    } else if (attrValue.endsWith("px")) {
                        parseContext.methodSpecBuilder.addStatement(
                                "$N." + methodName + "($L)",
                                lpVarName, size
                        );
                    } else {
                        parseContext.methodSpecBuilder.addStatement(
                                "$N." + methodName + "(context.getResources().getDimensionPixelSize($T.dimen.$L))",
                                lpVarName, parseContext.R_CLASS, attrValue.substring(7)
                        );
                    }
                }
                break;
        }
    }

}
