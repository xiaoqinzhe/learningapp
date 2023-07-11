package com.example.learningapp.kotlin.coroutine

import android.util.Log
import com.example.learningapp.thread.ThreadManager

object TestApi {
    const val TAG = "TestApi"

    var listeners: ArrayList<() -> Unit> = arrayListOf()

    fun start() {
        ThreadManager.postNormal {
            while (true) {
                Thread.sleep(2000)
                Log.d(TAG, "TestApi tick")
                listeners.forEach {
                    it.invoke()
                }
            }
        }
    }
}