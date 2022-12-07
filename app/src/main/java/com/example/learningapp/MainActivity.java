package com.example.learningapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.learningapp.aidl.AidlActivity;
import com.example.learningapp.apt.AptTestActivity;
import com.example.learningapp.backworker.AsyncActivity;
import com.example.learningapp.backworker.WorkerActivity;
import com.example.learningapp.fragments.DummyActivity;
import com.example.learningapp.intent.StartActivity;
import com.example.learningapp.jetpack.databinding.DataBindingActivity;
import com.example.learningapp.kotlin.HelloKotlin;
import com.example.learningapp.kotlin.KotlinTestActivity;
import com.example.learningapp.kotlin.coroutine.CoroutineTest;
import com.example.learningapp.livedata.UserProfileActivity;
import com.example.learningapp.media.MediaNavActivity;
import com.example.learningapp.ndk.NDKActivity;
import com.example.learningapp.views.RecyclerViewActivity;
import com.example.learningapp.notification.NotificationActivity;
import com.example.learningapp.views.ViewNavActivity;
import com.example.libannotation.BindActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@BindActivity
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> list = new ArrayList<>();

        Log.d("getDir()", getDir("getDir", MODE_PRIVATE).getAbsolutePath());
        Log.d("getDataDir()", getDataDir().getAbsolutePath());
        Log.d("getFilesDir()", getFilesDir().getAbsolutePath());
        Log.d("getCacheDir()", getCacheDir().getAbsolutePath());

        Log.d("getExternalCacheDir()", getExternalCacheDir().getAbsolutePath());
        Log.d("getExternalFilesDir()", getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        Log.d("getExternalFilesDir()", String.valueOf(getExternalFilesDirs(Environment.DIRECTORY_MUSIC).length));

        Log.d("Environment.getDataDirectory()", Environment.getDataDirectory().getAbsolutePath());
        Log.d("Environment.getDownloadCacheDirectory()", Environment.getDownloadCacheDirectory().getAbsolutePath());
        Log.d("Environment.getRootDirectory()", Environment.getRootDirectory().getAbsolutePath());
        Log.d("Environment.getExternalStorageState()", Environment.getExternalStorageState());


        // onResume / onWindowFocusChanged
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
//                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        Button btn = findViewById(R.id.btn_main_intent);
        btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Button button = (Button)v;
                v.startDragAndDrop(null, new View.DragShadowBuilder(v), button.getText(), 0);
                return true;
            }
        });

        final Button btnFragment = findViewById(R.id.btn_main_fragment);
        btnFragment.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()){
                    case DragEvent.ACTION_DROP:
                        CharSequence sequence = (CharSequence) event.getLocalState();
                        btnFragment.setText(sequence);
                        break;

                }
                return true;
            }
        });

        // preference
        SharedPreferences sharedPreferences = getSharedPreferences("mySetting", Context.MODE_PRIVATE);
        int textSize = sharedPreferences.getInt("textSize", 2);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("textSize", textSize);
        editor.apply();

        // 响应系统ui可见性改变
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                Log.d("debug", "system ui "+visibility);
            }
        });

        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);


        // 全屏1：点击弹出 看视频
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        findViewById(R.id.sv_main).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("debug", "main click");
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            }
//        });

        // 沉浸式：自己划出  看图
//        getSupportActionBar().hide();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
//                |View.SYSTEM_UI_FLAG_FULLSCREEN
//                |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
////                |View.SYSTEM_UI_FLAG_LAYOUT_STABLE    // 3个，防止布局变化？
////                |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
////                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//        );
//        findViewById(R.id.sv_main).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("debug", "main click");
//                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
//            }
//        });

        // 完全沉浸式：自己划出，自动隐藏  绘画，游戏     ！！布局位置不变
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        findViewById(R.id.sv_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("debug", "main click");
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }



    @Override
    protected void onResume() {
        super.onResume();
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        Log.i("state", "onResume");
    }

    public void startIntent(View view) {
        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }

    public void startWorker(View view) {
        Intent intent = new Intent(this, WorkerActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i("main", "save instance state");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        // does not work...
        Log.i("main", "save instance state");
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState) {
        Log.i("main", "restore instance state");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("state", "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("state", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("state", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("state", "onDestroy");
    }

    public void startNav(View view) {
        Intent intent = new Intent(this, ViewNavActivity.class);
        startActivity(intent);
    }

    public void startLivedata(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    public void startAsynctask(View view) {
        Intent intent = new Intent(this, AsyncActivity.class);
        startActivity(intent);
    }

    public void startNotification(View view) {
        Intent intent = new Intent(this, NotificationActivity.class);
        startActivity(intent);
    }

    public void startFragment(View view) {
        Intent intent = new Intent(this, DummyActivity.class);
        startActivity(intent);
    }

    public void startMedia(View view) {
        Intent intent = new Intent(this, MediaNavActivity.class);
        startActivity(intent);
    }

    public synchronized void startAIDL(View view) {
        Intent intent = new Intent(this, AidlActivity.class);
        startActivity(intent);
        AtomicInteger a = new AtomicInteger(0);
        a.getAndAdd(1);
    }

    public synchronized void startDataBinding(View view) {
        Intent intent = new Intent(this, DataBindingActivity.class);
        startActivity(intent);
    }

    public synchronized void startNDK(View view) {
        Intent intent = new Intent(this, NDKActivity.class);
        startActivity(intent);
    }

    public synchronized void startKotlinTest(View view) {
        Intent intent = new Intent(this, KotlinTestActivity.class);
        startActivity(intent);
    }

    public void startAptTest(View view) {
        Intent intent = new Intent(this, AptTestActivity.class);
        startActivity(intent);
    }
}
