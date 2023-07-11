package com.example.learningapp.kotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.core.view.marginEnd
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.learningapp.R
import com.example.learningapp.kotlin.coroutine.CoroutineTest
import com.example.learningapp.kotlin.coroutine.TestApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class KotlinTestActivity : AppCompatActivity() {

    val TAG = "KotlinTestActivity"

    var str: String? = "hhhh";
    var tv: TextView? = null;

    lateinit var vm: KotlinTestVM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_test)
        str = "a"+"b";
        var add : (Int, Int) -> Int = {x, y -> x+y}
        tv?.setOnClickListener({ v -> println(v?.marginEnd)})

        vm = ViewModelProvider(this).get(KotlinTestVM::class.java)
        vm.test()

        lifecycleScope.launch {

        }

        //        CoroutineTest.testScope()
//        CoroutineTest.testFlow()
        CoroutineTest.getCallbackFlow()
            .onEach {
                Log.d(TAG, "callbackFlow onEach $it")
            }
            .flowOn(Dispatchers.Default)
            .launchIn(lifecycleScope)

        lifecycleScope.launch(Dispatchers.IO) {
            Log.d(TAG, "suspendCancellableCoroutineTest start")
            val result = CoroutineTest.suspendCancellableCoroutineTest()
            Log.d(TAG, "suspendCancellableCoroutineTest end result=$result")
        }

    }

    fun lll(s : String) : Int{
        return 0;
    }
}
