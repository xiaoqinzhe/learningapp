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
    public static final ClassName LAYOUT_INFLATER = ClassName.get("android.view", "LayoutInflater");
    public static final ClassName VIEW = ClassName.get("android.view", "View");
    public static final ClassName VIEW_GROUP = ClassName.get("android.view", "ViewGroup");

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
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }
        sb.append(LAYOUT_NAME_SUFFIX);
        return sb.toString();
    }

    public static String getViewName(String id) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String word : id.split("_")) {
            if (word.length() == 0) {
                continue;
            }
            if (i++ == 0) {
                sb.append(word);
            } else {
                sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
            }
        }
        return sb.toString();
    }

    public static IViewParser getViewParser(String name) {
        IViewParser viewParser = viewParserMap.get(name);
        if (viewParser != null) {
            return viewParser;
        }
        if (name.contains(".")) {
            // custom view
            // find super: ele,  class 不行
            try {
                Class<?> clz = LayoutBindingConfig.class.getClassLoader().loadClass(name);
                Class<?> superClz = clz.getSuperclass();
                while (superClz != null) {
                    viewParser = viewParserMap.get(superClz.getSimpleName());
                    if (viewParser != null) {
                        return viewParser;
                    }
                    superClz = clz.getSuperclass();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
