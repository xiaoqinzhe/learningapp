package com.example.learningapp.backworker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.learningapp.R;

import java.util.concurrent.TimeUnit;

public class WorkerActivity extends AppCompatActivity {
    private WorkRequest workRequest;
    private TextView tv_worker_state;
    private TextView tv_worker_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        tv_worker_state = findViewById(R.id.tv_worker_state);
        tv_worker_progress = findViewById(R.id.tv_worker_progress);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
//        getWindow().setNavigationBarColor(getTheme().obtainStyledAttributes(new int[]{android.R.attr.colorPrimary}).getColor(0,0xffff0000));
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        getWindow().setStatusBarColor(Color.TRANSPARENT);

    }

    public void startWorker(View v){
        Constraints constraints = new Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build();
        Data inputData = new Data.Builder().putString("input", "input").build();
        workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.SECONDS)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build();
        WorkManager.getInstance(this).enqueue(workRequest);
        LiveData<WorkInfo> workInfoLiveData = WorkManager.getInstance(this)
                .getWorkInfoByIdLiveData(workRequest.getId());
        workInfoLiveData.observe(this, new Observer<WorkInfo>() {
            @Override
            public void onChanged(WorkInfo workInfo) {
                Log.i("worker", "worker state change");
                // state
                WorkInfo.State state = workInfo.getState();
                Log.i("worker", "state: "+state.toString());
                tv_worker_state.setText(state.toString());

                // progress
                int progress = workInfo.getProgress().getInt(MyWorker.PROGRESS, 0);
                Log.i("worker", "progress: "+progress);
                tv_worker_progress.setText(String.valueOf(progress));

                // success
                if(state == WorkInfo.State.SUCCEEDED){
                    Data outputData = workInfo.getOutputData();
                    Log.i("worker", "output Data: "+outputData.getString("result"));
                    tv_worker_progress.setText(String.valueOf(100));
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("state", tv_worker_state.getText().toString());
        outState.putString("progress", tv_worker_progress.getText().toString());
        Log.i("restore", "state: "+tv_worker_state.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        Log.i("restore", "state: "+savedInstanceState.getString("state"));
        tv_worker_state.setText(savedInstanceState.getString("state"));
        tv_worker_progress.setText(savedInstanceState.getString("progress"));
        super.onRestoreInstanceState(savedInstanceState);
    }
}
