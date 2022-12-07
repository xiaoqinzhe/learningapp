package com.example.learningapp.framework.touch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import com.example.learningapp.R

/**
 * 拦截事件可参考 [AIOContentLeftSwipeHelper]
 */
class TouchEventTestActivity : AppCompatActivity() {

    companion object {
        const val TOUCH_TAG = "TOUCH_"
        const val TAG = TOUCH_TAG + "TouchEventTestActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touch_event_test)
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