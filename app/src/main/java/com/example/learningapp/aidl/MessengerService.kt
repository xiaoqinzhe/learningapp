package com.example.learningapp.aidl

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger

class MessengerService: Service() {

    val messenger = Messenger(object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    })

    override fun onBind(intent: Intent?): IBinder? {
        return messenger.binder
    }
}