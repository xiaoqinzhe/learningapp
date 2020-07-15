package com.example.learningapp.utils;

public class Common {

    public static String stringToUnicode(String str){
        StringBuilder sb = new StringBuilder();
        String[] hex = str.split("\\\\u");
        for (int i=1; i<hex.length; ++i){
            L.d(hex[i]);
            sb.append((char)Integer.parseInt(hex[i], 16));
        }
        return sb.toString();
    }

    public static String unicodeToString(String unicodeStr){
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<unicodeStr.length(); ++i){
            char c = unicodeStr.charAt(i);
            sb.append("\\u").append(Integer.toHexString(c));
        }
        return sb.toString();
    }
}
