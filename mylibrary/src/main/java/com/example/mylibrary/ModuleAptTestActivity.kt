package com.example.mylibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libannotation.BindLayouts

//@BindLayouts(layouts = ["activity_module_apt_test"])
class ModuleAptTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_module_apt_test)
    }
}