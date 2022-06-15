package com.example.learningapp.aidl

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AidlService: Service() {

    inner class AidlComputeService: IAidlComputeService.Stub() {
        // 只能同步，异步需绑定客户端service
        override fun getDes(): String {
            return "des"
        }

        override fun add(a: Int, b: Int): Int {
            return a + b
        }

        // client ipc callback
        override fun setCallback(callback: IAidlComputeServiceCallback?) {

        }

    }

    val binder = AidlComputeService()

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}