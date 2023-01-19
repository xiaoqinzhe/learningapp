package com.example.libannotation.util;

public class LayoutBindingUtil {

    public static final String LAYOUT_NAME_SUFFIX = "LayoutBinding";

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

}
