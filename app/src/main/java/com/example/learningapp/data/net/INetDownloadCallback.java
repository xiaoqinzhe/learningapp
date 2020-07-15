package com.example.learningapp.data.net;

public interface INetDownloadCallback {

    void onFailure(Exception e);

    void onProgress(float progress);

    void onFinish();

}
