package com.example.learningapp.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.learningapp.R;

public class TouchEventActivity extends AppCompatActivity {

    private TextView mTv;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_event);


//        mTv = findViewById(R.id.btn_hi);
//
//        mTv.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("debug", "touch tv");
//                return true;
//            }
//        });


    }


    public void clickTv(View view) {
        Log.d("debug", "click tv");
    }
}
