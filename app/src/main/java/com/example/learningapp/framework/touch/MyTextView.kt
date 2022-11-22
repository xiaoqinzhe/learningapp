package com.example.learningapp.framework.touch

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView

class MyTextView: TextView {

    companion object {
        const val TAG = TouchEventTestActivity.TOUCH_TAG + "MyTextView"
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent $ev")
        val res = super.dispatchTouchEvent(ev)
        Log.d(TAG, "dispatchTouchEvent $res")
        return res
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent $event")
        val res = super.onTouchEvent(event)
        Log.d(TAG, "onTouchEvent $res")
        return res
    }

}