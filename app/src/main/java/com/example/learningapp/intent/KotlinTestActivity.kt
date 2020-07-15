package com.example.learningapp.intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.marginEnd
import com.example.learningapp.R

class KotlinTestActivity : AppCompatActivity() {

    var str: String? = "hhhh";
    var tv: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_test)
        str = "a"+"b";
        var add : (Int, Int) -> Int = {x, y -> x+y}
        tv?.setOnClickListener({ v -> println(v?.marginEnd)})
    }

    fun lll(s : String) : Int{
        return 0;
    }
}
