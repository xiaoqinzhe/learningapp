package com.example.ndktool;

public class NDKTool {

    static {
        System.loadLibrary("ndktool");
    }

    public static native String getStringFromJni();

}
