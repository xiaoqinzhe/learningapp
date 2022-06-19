package com.example.learningapp.kotlin.coroutine

import kotlinx.coroutines.*

class CoroutineTest {

    fun main() {
        // 协程
        GlobalScope.launch(Dispatchers.Main) {
            val i = getUserInfo() // 非阻塞，自动切换回主线程
            println("CoroutineTest $i")
        }
    }

    // 耗时函数，在IO线程执行
    suspend fun getUserInfo(): Int = withContext(Dispatchers.IO) {
        delay(2000)
        1
    }

}