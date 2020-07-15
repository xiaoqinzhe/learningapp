package com.example.learningapp.data.net;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpNetUtil implements INetUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void get(String url, final INetCallback callback, Object tag) {

//        try {
//            JSONObject jsonObject = new JSONObject("");
//            jsonObject.getJSONArray("").get
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        OkHttpClient client = OkHttpClientFactory.getOkHttpClient();
        Request request = new Request.Builder().url(url).get().tag(tag).build();
        Call call = client.newCall(request);
//        call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try{
                    final String str = response.body().string();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResponse(str);
                        }
                    });
                }catch (IOException e){
                    e.printStackTrace();
                    callback.onFailure(e);
                }
            }
        });
    }

    @Override
    public void post(String url, Map data, INetCallback callback) {
        FormBody formBody = new FormBody.Builder()
                .add("hh", "dd")
                .build();
        Request request = new Request.Builder().url(url).post(formBody).build();
    }

    @Override
    public void cancel(Object tag) {
        OkHttpClient client = OkHttpClientFactory.getOkHttpClient();
        for (Call call: client.dispatcher().queuedCalls()){
            if (call.request().tag().equals(tag)){
                call.cancel();
            }
        }
        for (Call call: client.dispatcher().runningCalls()){
            if (call.request().tag().equals(tag)){
                call.cancel();
            }
        }
    }

    @Override
    public void download(String url, final File outFile, final INetDownloadCallback callback, Object tag) {
        if (!outFile.exists()){
            outFile.mkdirs();
        }
//        outFile.setReadable(true, false);
        Request request = new Request.Builder().url(url).get().tag(tag).build();
        Call call = OkHttpClientFactory.getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = response.body().byteStream();
                    os = new FileOutputStream(outFile);
                    byte[] buffer = new byte[1024*4];
                    int bufferLen;
                    long curLen=0;
                    final long totalLen = response.body().contentLength();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProgress(0);
                        }
                    });
                    while((bufferLen = is.read(buffer)) > 0){
                        os.write(buffer, 0, bufferLen);
                        os.flush();
                        curLen += bufferLen;
                        final long finalCurLen = curLen;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onProgress((float)finalCurLen /totalLen);
                            }
                        });
                    }
                    callback.onFinish();
                }catch (Exception e){
                    e.printStackTrace();
                    callback.onFailure(e);
                }finally {
                    if (is != null)
                        is.close();
                    if (os != null)
                        os.close();
                }
            }
        });
    }
}
