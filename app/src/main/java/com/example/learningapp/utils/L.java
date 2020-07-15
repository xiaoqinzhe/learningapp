package com.example.learningapp.utils;

import android.util.Log;

public class L {

    private static final String TAG = "debug";

    private static boolean isDebug = true;

    public static void d(String msg, Object... args){
        if (!isDebug) return;
        Log.d(TAG, String.format(msg, args));
    }

}
