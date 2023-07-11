package com.example.learningapp.thread

import android.os.Handler
import android.os.Looper
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

object ThreadManager {

    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    private val normalThreadPool by lazy {
        ThreadPoolExecutor(4, 4, 10, TimeUnit.SECONDS, LinkedBlockingQueue(24))
    }

    fun postMain(block: () -> Unit, delay: Long = 0) {
        mainHandler.postDelayed(block, delay)
    }

    fun postNormal(block: () -> Unit) {
        normalThreadPool.execute(block)
    }

}