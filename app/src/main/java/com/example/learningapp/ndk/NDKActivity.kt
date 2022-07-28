package com.example.learningapp.ndk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.example.learningapp.R
import com.example.ndktool.NDKTool

class NDKActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ndkactivity)
        findViewById<View>(R.id.btn_get_str_jni)?.setOnClickListener {
            findViewById<TextView>(R.id.tv_str_jni)?.text = NDKTool.getStringFromJni()
        }
    }
}