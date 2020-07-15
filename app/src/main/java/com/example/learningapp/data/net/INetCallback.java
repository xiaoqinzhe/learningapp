package com.example.learningapp.data.net;

public interface INetCallback {

    void onResponse(String response);

    void onFailure(Throwable throwable);
}
