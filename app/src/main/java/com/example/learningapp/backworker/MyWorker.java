package com.example.learningapp.backworker;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {
    public static final String PROGRESS = "progress";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        setProgressAsync(new Data.Builder().putInt(PROGRESS, 0).build());
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }

    @NonNull
    @Override
    public Result doWork() {
        if(isStopped()){
            return Result.failure();
        }
        try {
            Thread.sleep(3000);
            setProgressAsync(new Data.Builder().putInt(PROGRESS, 50).build());
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String input = getInputData().getString("input");
        Log.i("my worker", input);
        Data result_data = new Data.Builder().putString("result", "result from myworker").build();
        Result result = Result.success(result_data);
        setProgressAsync(new Data.Builder().putInt(PROGRESS, 100).build());
        return result;
    }
}
