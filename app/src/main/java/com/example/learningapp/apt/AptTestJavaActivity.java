package com.example.learningapp.apt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.example.learningapp.R;
import com.example.libannotation.BindLayouts;

@BindLayouts(layouts = {"activity_apt_test_java"})
public class AptTestJavaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_apt_test_java);
        View content = LayoutInflater.from(this).inflate(R.layout.activity_apt_test_java, null);
        setContentView(content);
    }
}