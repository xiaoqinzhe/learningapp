package com.example.learningapp.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.marginEnd
import com.example.learningapp.R
import com.example.learningapp.kotlin.HelloKotlin.Companion.SB
import com.example.learningapp.kotlin.coroutine.CoroutineTest
import kotlinx.coroutines.CoroutineScope

class KotlinTestActivity : AppCompatActivity() {

    var str: String? = "hhhh";
    var tv: TextView? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_test)
        str = "a"+"b";
        var add : (Int, Int) -> Int = {x, y -> x+y}
        tv?.setOnClickListener({ v -> println(v?.marginEnd)})

        CoroutineTest.testScope()
        CoroutineTest.testFlow()

    }

    fun lll(s : String) : Int{
        return 0;
    }
}
