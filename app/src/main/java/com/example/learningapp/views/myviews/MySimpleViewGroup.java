package com.example.learningapp.views.myviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MySimpleViewGroup extends ViewGroup {
    public MySimpleViewGroup(Context context) {
        this(context, null);
    }

    public MySimpleViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySimpleViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public MySimpleViewGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("debug", "onLayout "+l+" "+t+" "+r+" "+b);
        int mLeft = 0, mTop=0;
        int width = getWidth();
        int height = getHeight();
        Log.d("debug", "width "+width+" height "+height);
        for (int i = 0; i < getChildCount(); ++i){
            View child = getChildAt(i);
            mTop = (height-child.getMeasuredHeight())/2;
            child.layout(mLeft, mTop, mLeft+child.getMeasuredWidth(), mTop+child.getMeasuredHeight());
            mLeft += child.getMeasuredWidth();
        }
    }
}
