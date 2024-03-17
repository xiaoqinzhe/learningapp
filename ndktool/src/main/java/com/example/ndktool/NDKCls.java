package com.example.ndktool;

import android.util.Log;

public class NDKCls {

    public int a = 0;

    public NDKCls(int p) {
        a = p;
    }

    public void print(int param) {
        Log.d("NDKCls", "print a=" + a + ", param=" + param);
    }

}
