package com.example.learningapp.aidl

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.learningapp.R

class AidlActivity : AppCompatActivity() {

    private val aidlConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val service = IAidlComputeService.Stub.asInterface(service)
            service.des
            service.add(1, 3)
            service.setCallback(object: IAidlComputeServiceCallback.Stub() {
                override fun onResponse(result: Int) {
                    Log.d("", "get result: $result");
                }

            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    private var serverMessenger: Messenger? = null
    private val clientMessenger: Messenger = Messenger(object: Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    })

    private val messengerConn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            serverMessenger = Messenger(service)
            val msg = Message.obtain()
            msg.what = 1
            msg.replyTo = clientMessenger
            serverMessenger!!.send(msg)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            TODO("Not yet implemented")
        }

    }

    val conn = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("AidlActivity", "onServiceConnected")
            val res = (service as ComputeService.MyBinder).add(1, 2)
            Log.d("AidlActivity", "$res")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("AidlActivity", "onServiceDisconnected")
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidl)
    }

    fun startService(view: View) {
        startService(Intent(this, ComputeService::class.java))
    }

    fun stopService(view: View) {
        stopService(Intent(this, ComputeService::class.java))
    }

    fun bindService(view: View) {
        bindService(Intent(this, AidlService.AidlComputeService::class.java), aidlConn, Service.BIND_AUTO_CREATE)
    }


    fun unbindService(view: View) {
        unbindService(aidlConn)
    }
}