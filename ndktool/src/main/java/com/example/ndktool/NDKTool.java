package com.example.ndktool;

public class NDKTool {

    static {
        System.loadLibrary("ndktool");
    }

    public static native String getStringFromJni();

    public static native NDKCls testObj(int i, boolean b, String str, NDKCls obj);

    public static native int getDirSizeByNative(String path);

}
