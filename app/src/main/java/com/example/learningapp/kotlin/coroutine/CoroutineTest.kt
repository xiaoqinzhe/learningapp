package com.example.learningapp.kotlin.coroutine

import android.util.Log
import com.example.learningapp.thread.ThreadManager
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis

/**
 * kotlin协程：
 * 线程框架，thread executor 等封装，工具api
 * 优点：同个代码块里多次切换线程
 * suspend关键字： 提醒，作为一个耗时函数，会挂起切换到后台线程执行，需要在协程里调用
 * 挂起函数：切换线程后切回来  withContext，delay
 * 非阻塞式挂起  不卡线程，挂起协程
 * suspend fun getUserInfo(): Int = withContext(Dispatchers.IO) {
 *     delay(2000)
 *     1
 * }
 * https://www.bilibili.com/video/BV164411C7FK
 *
 * - runBlocking 阻塞协程创建
 * Runs a new coroutine and blocks the current thread interruptibly until its completion. This function should not be used from a coroutine. It is designed to bridge regular blocking code to libraries that are written in suspending style, to be used in main functions and in tests.
 * - launch async 非阻塞协程创建
 *
 * 1. 协程作用域  跟踪所有由其启动的协程，可统一取消  https://juejin.cn/post/6926695962354122765
 * GlobalScope，MainScope，viewModelScope
 *
 * 2. 调度器  指定运行线程
 * Dispatchers.Main  Default  IO
 *
 * 3. 启动模式
 * CoroutineStart.DEFAULT 立即调度  LAZY 主动start
 *
 * 4. Job  launch 返回的
 * isActive isCancel   cancel()   withTimeout
 * async{} 异步返回值 Deferred<T>
 *
 * 5. 上下文 CoroutineContext
 * 由四个元素组成：Job、CoroutineDispatcher、CoroutineName、CoroutineExceptionHandler，各个元素可以用 + 号连接
 * 子协程可以继承 or 覆盖
 *
 */
object CoroutineTest {

    const val TAG = "CoroutineTest"

    // 协程作用域  跟踪所有由其启动的协程，
    private val scope = MainScope() // ui component, need cancel

    fun cancel() {
        scope.cancel()  // cancel job and its children!!
    }

    fun testScope() {
        scope.launch(User("hhh") + Job() + Dispatchers.Main + CoroutineName("test")
                + CoroutineExceptionHandler {context, e ->
            println("catch exception $e")
        },
                CoroutineStart.DEFAULT) {
            val res = withContext(Dispatchers.IO) {
                // IO thread
                delay(100)
                1
            }
            // main thread
            // ***
            throw Exception("my exception")
        }
        scope.launch {
            // 并发 有返回值
            val de = scope.async {
                "result"
            }
            val de2 = scope.async {
                "result"
            }
            val res1 = de.await()
            val res2 = de2.await()
        }
    }

    suspend fun main() {
        // 启动新的协程，不阻塞
        // GlobalScope不推荐用，测试代码用
        val job = GlobalScope.launch(Dispatchers.Main) {
            this.coroutineContext
            val i = getUserInfo() // 不阻塞线程，但会挂起协程，等执行完自动切换回主线程Main
            println("coroutine end user=$i")
        }
        println("launch coroutine $job")
        job.join() // wait

        // 启动协程，会阻塞
        println("runBlocking coroutine")
        val a = runBlocking {
            delay(1000)
            println("runBlocking delay 1000 end")
            launch {
                println("$this")
                delay(1000)
                launch {
                    println("$this")
                }
            }
            // 自动等上面的结束
        }
        println("runBlocking end")

        // cancel
        runBlocking {
            val job = launch {
                repeat(100) {
                    delay(100)
                    if (!isActive) {
                        return@repeat
                    }
                }
            }
            delay(2000)
            job.cancelAndJoin()
        }

        // timeout
        val result = withTimeoutOrNull(3000) {
            repeat(100) {
                delay(100)
            }
            "done"
        }
        println("timeout result=$result")

        // as
        runBlocking {
            val time = measureTimeMillis {
                val job1 = async { delay(1000) }
                val job2 = async { delay(1000) }
                job1.await()
                job2.await()
            }
            println("async time=$time")
        }

        // thread
        runBlocking {
            launch {  }  // main
            launch(Dispatchers.Main) {

            }
            launch(newSingleThreadContext("thread1")) {

            }
            runBlocking {

            }
        }

    }

    // 耗时函数，在IO线程执行。  非阻塞挂起
    suspend fun getUserInfo(): Int = withContext(Dispatchers.IO) {
        delay(2000)
        1
    }

    // Flow
    fun testFlow() {
        simple().forEach {
            println("simple sequence $it ${Thread.currentThread()}") // Main
        }

        GlobalScope.launch(Dispatchers.Main) {   // 不阻塞  post
            println("getInt start")
            getInt().collect {
                println("getInt $it ${Thread.currentThread()}") // Default 跟随flowOn线程
            }
            println("getInt end") // 不阻塞main，但挂起协程   等上面执行完成
        }

        println("runBlocking")

        runBlocking {
            // Main
            (1..5).asFlow()
                    .map { it * it }
                    .transform {
                        emit("string $it ${Thread.currentThread()}") // 耗时
                    }
                    .collect { println(it) }
        }
    }

    // 默认suspend
    fun getInt(): Flow<Int> = flow<Int> {
        // Default thread
        for (i in 1..3) {
            delay(200)
            println("emit $i ${Thread.currentThread()}")
            emit(i)
        }
    }.map { it+1 }.flowOn(Dispatchers.Default)

    fun simple(): Sequence<Int> = sequence { // 序列构建器
        for (i in 1..3) {
            Thread.sleep(100) // 假装我们正在计算
            yield(i) // 产生下一个值
        }
    }

    fun getCallbackFlow(): Flow<String> = callbackFlow {
//        ThreadManager.postNormal {
//            Log.d(TAG, "getCallbackFlow start")
//            Thread.sleep(3000)
//            Log.d(TAG, "getCallbackFlow trySend")
//            trySend("done")
//            Log.d(TAG, "getCallbackFlow trySend end")
//            if (isActive) {
//                Log.d(TAG, "getCallbackFlow isActive call close")
//                close()
//            }
//        }
//        awaitClose { // need call to cancel listener
//            Log.d(TAG, "getCallbackFlow awaitClose")
//        }

        val listener: () -> Unit = {
            Log.d(TAG, "getCallbackFlow listener invoke")
            trySend("111")
        }
        TestApi.listeners.add(listener)
        awaitClose {
            Log.d(TAG, "getCallbackFlow awaitClose")
            TestApi.listeners.remove(listener)
        }

//        launch {
//            Log.d(TAG, "getCallbackFlow start")
//            delay(1000)
//            Log.d(TAG, "getCallbackFlow send done")
//            send("done")
//            Log.d(TAG, "getCallbackFlow send done end")
//            // can close
//            if (isActive) {
//                Log.d(TAG, "getCallbackFlow isActive call close")
//                close()
//            }
//        }
//        awaitClose { // need call to cancel listener
//            Log.d(TAG, "getCallbackFlow awaitClose")
//        }
    }

    /**
     * 异步变同步
     */
    suspend fun suspendCancellableCoroutineTest(): String = suspendCancellableCoroutine {
        it.invokeOnCancellation {
            Log.d(TAG, "suspendCancellableCoroutineTest invokeOnCancellation")
        }
        ThreadManager.postNormal {
            Thread.sleep(2000)
            if (it.isActive) { // 不能多次调用resume
                it.resume("done") {
                    Log.d(TAG, "suspendCancellableCoroutineTest cancel")
                }
            }
        }
    }

    class User(val name: String) : AbstractCoroutineContextElement(User) {
        companion object Key : CoroutineContext.Key<User>
    }

}