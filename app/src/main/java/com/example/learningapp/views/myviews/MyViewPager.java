package com.example.learningapp.views.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class MyViewPager extends ViewPager {
    public MyViewPager(@NonNull Context context) {
        super(context);
    }

    public MyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
//        String[] names = new String[10];
//        names[MotionEvent.ACTION_DOWN] = "down";
//        names[MotionEvent.ACTION_UP] = "up";
//        names[MotionEvent.ACTION_MOVE] = "move";

//        Log.e("error", "viewpager event "+names[ev.getAction()]);
        return super.onTouchEvent(ev);
    }
}
