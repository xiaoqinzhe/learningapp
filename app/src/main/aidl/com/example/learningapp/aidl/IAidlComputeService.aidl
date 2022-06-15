// IAidlComputeService.aidl
package com.example.learningapp.aidl;

import com.example.learningapp.aidl.IAidlComputeServiceCallback;

// Declare any non-default types here with import statements

interface IAidlComputeService {

    String getDes();

    int add(int a, int b);

    void setCallback(in IAidlComputeServiceCallback callback);

}