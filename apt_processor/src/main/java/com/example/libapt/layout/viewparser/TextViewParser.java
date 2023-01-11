package com.example.libapt.layout.viewparser;

import com.squareup.javapoet.ClassName;

public class TextViewParser extends ViewParser {

    @Override
    public ClassName getClassName() {
        return ClassName.get("android.widget", "TextView");
    }

}
