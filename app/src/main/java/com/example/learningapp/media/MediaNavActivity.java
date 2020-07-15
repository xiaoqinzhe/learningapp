package com.example.learningapp.media;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.example.learningapp.R;

public class MediaNavActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_nav);
    }

    public void startAudio(View view) {
        Intent intent = new Intent(this, AudioActivity.class);
        startActivity(intent);
    }

    public void startVideoView(View view) {
        Intent intent = new Intent(this, VideoViewActivity.class);
        startActivity(intent);
    }
}
