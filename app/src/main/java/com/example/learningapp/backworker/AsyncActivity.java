package com.example.learningapp.backworker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.learningapp.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class AsyncActivity extends AppCompatActivity {
    private MyTask myTask;
    private MyHandler myhandler;
    private Integer[] strs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async);
        strs = new Integer[10000000];
        // wrong！！！
//        new AsyncTask<String, Integer, String>(){
//
//            @Override
//            protected String doInBackground(String... strings) {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.i("AsyncActivity", "new: task is done");
//                return null;
//            }
//
//            @Override
//            protected void onCancelled() {
//                super.onCancelled();
//                Log.i("AsyncActivity", "new: task is cancelled!");
//            }
//        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        myTask = new MyTask(this);
        myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        myhandler = new MyHandler(this);
//        myhandler.sendEmptyMessageDelayed(233, 120000);
        new Timer().schedule(new TimerTask(){  // not killed!!!
            @Override
            public void run() {
                Log.i("AsyncActivity", "my thread is still running...");
                while(true){
                    try {
                        Thread.sleep(1000);
                        Log.i("AsyncActivity", "my thread is still running...");
                        myhandler.sendEmptyMessage(233);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTask.cancel(true);
        myhandler.removeCallbacksAndMessages(null);
        // stop thread!!!
    }

    static class MyHandler extends Handler{
        private WeakReference<Activity> activityWeakReference;

        public MyHandler(Activity activity){
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Activity activity = activityWeakReference.get();
            Log.i("AsyncActivity", "static: handling message is done");
        }
    }

    static class MyTask extends AsyncTask<String, Integer, String>{

        WeakReference<Activity> activityWeakReference;

        public MyTask(Activity activity){
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.i("AsyncActivity", "static: task is done");
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String string) {
            Activity activity = activityWeakReference.get();
            if(activity != null){
                Log.i("AsyncActivity", "MyTask: post execute...");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.i("AsyncActivity", "static: mytask is cancelled!");
        }
    }

}
