package com.example.libapt.layout;

import com.example.libapt.layout.viewparser.IViewParser;
import com.example.libapt.layout.viewparser.LinearLayoutParser;
import com.example.libapt.layout.viewparser.TextViewParser;
import com.example.libapt.layout.viewparser.ViewGroupParser;
import com.example.libapt.layout.viewparser.ViewParser;
import com.squareup.javapoet.ClassName;

import java.util.HashMap;
import java.util.Map;

public class LayoutBindingConfig {

    public static final String GENERATED_PACKAGE_NAME = "com.example.generated.layout";

    public static final String LAYOUT_NAME_SUFFIX = "LayoutBinding";

    public static final ClassName CONTEXT = ClassName.get("android.content", "Context");
    public static final ClassName VIEW = ClassName.get("android.view", "View");

    private static final Map<String, IViewParser> viewParserMap = new HashMap<>();

    static {
        viewParserMap.put("View", new ViewParser());
        viewParserMap.put("ViewGroup", new ViewGroupParser());
        viewParserMap.put("LinearLayout", new LinearLayoutParser());
        viewParserMap.put("TextView", new TextViewParser());
    }

    public static String getLayoutClassName(String layoutName) {
        StringBuilder sb = new StringBuilder();
        for (String word : layoutName.split("_")) {
            if (word.length() == 0) {
                continue;
            }
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1, word.length()));
        }
        sb.append(LAYOUT_NAME_SUFFIX);
        return sb.toString();
    }

    public static IViewParser getViewParser(String name) {
        return viewParserMap.get(name);
    }

}
