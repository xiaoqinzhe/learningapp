package com.example.learningapp.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.learningapp.R;

public class PicInPicActivity extends AppCompatActivity {

    private Button btnStartPic;
    private ImageView ivShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_in_pic);
        btnStartPic = findViewById(R.id.btn_views_picinpic);
        ivShow = findViewById(R.id.iv_views_picinpic);


        btnStartPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    PicInPicActivity.this.enterPicInPic();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void enterPicInPic(){
        Rect showRect = new Rect(0, 0, 400,400);
//        ivShow.getHitRect(showRect);
        Rational aspectRatio = new Rational(10, 16);
        PictureInPictureParams params = new PictureInPictureParams.Builder()
                .setSourceRectHint(showRect)
                .setAspectRatio(aspectRatio)
                .build();
        enterPictureInPictureMode(params);
    }

    @Override
    protected void onUserLeaveHint() {
        // leave
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPicInPic();
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if (isInPictureInPictureMode){
            btnStartPic.setVisibility(View.INVISIBLE);
        }else{
            btnStartPic.setVisibility(View.VISIBLE);
        }

    }
}
