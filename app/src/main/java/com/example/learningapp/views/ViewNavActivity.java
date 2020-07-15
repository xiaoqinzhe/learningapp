package com.example.learningapp.views;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.learningapp.R;
import com.example.learningapp.views.myviews.ZoomImageView;

public class ViewNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_nav);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setLogo(R.drawable.ic_launcher_foreground);
//            actionBar.setShowHideAnimationEnabled(true);
//            actionBar.setUp
        }
    }

    public void startRecyclerView(View view) {
        Intent intent = new Intent(this, RecyclerViewActivity.class);
        startActivity(intent);
    }

    public void startMenu(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void startPicInPic(View view) {
        Intent intent = new Intent(this, PicInPicActivity.class);
        startActivity(intent);
    }

    public void startZoomImageView(View view) {
        Intent intent = new Intent(this, ZoomImageViewActivity.class);
        startActivity(intent);
    }

    public void startTouchEventActivity(View view) {
        Intent intent = new Intent(this, TouchEventActivity.class);
        startActivity(intent);
    }

    public void startEmojiActivity(View view) {
        Intent intent = new Intent(this, EmojiActivity.class);
        startActivity(intent);
    }

    public void startKeyboardActivity(View view) {
        Intent intent = new Intent(this, KeyboardActivity.class);
        startActivity(intent);
    }
}
