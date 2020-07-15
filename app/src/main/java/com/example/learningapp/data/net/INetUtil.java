package com.example.learningapp.data.net;

import java.io.File;
import java.util.Map;

public interface INetUtil {

    void get(String url, INetCallback callback, Object tag);

    void post(String url, Map data, INetCallback callback);

    void download(String url, File outFile, INetDownloadCallback callback, Object tag);

    void cancel(Object tag);

}
