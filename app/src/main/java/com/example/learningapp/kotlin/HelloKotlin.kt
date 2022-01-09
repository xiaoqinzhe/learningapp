package com.example.learningapp.kotlin

import android.content.Context
import android.view.View
import android.widget.Button
import com.example.learningapp.AppApplication
import java.io.File

class HelloKotlin() {

    companion object {
        val SB = "SB" // private
        const val SA = "SA" // public

        fun SMethod() = 1
    }

    private var str : String
    var a : Int = 0
        get() = field
        set(value) {
            field = value + 1
        }

    init {
        a = 1
        str = "${a + a}"
    }

    fun add(a: Int, b: Int) {
        val c = a + b
        println(c)
    }

    fun ab(a: Int, b: Int) = if (a > b) a else b

    fun ifTest() {
        var a = if (4 > 2) {
            print("a")
            7
        } else {
            6
        }
    }

    fun forTest() {
        val items = listOf("apple", "banana", "kiwifruit")
        for (index in items.indices) {
            println("item at $index is ${items[index]}")
        }

        for (i in 1..10 step 1) {}
        for (i in 10 downTo 0) {
            if (i == 4) break
        }

        var array = arrayOf(1,2,3)
        for ((index, value) in array.withIndex()) {
            println("the element at $index is $value")
        }

        array.forEach {
            print(it)
        }

    }

    fun whenTest() {
        when {
            a == 1 -> println("$a")
            else -> print("aaa")
        }

        when(a) {
            1,4 -> {

            }
            6 -> print(1)
            !in 1..3 -> {}
            else -> {}
        }
    }

    fun nullableTest(a: Int?): Int? {
        var b = a
        if (b != null) {
            b = b + 1
            return b
        }

        var cc = 1
        cc?.and(2)

        val files = File("Test").listFiles()
        println(files?.size ?: "empty") // if files is null, this prints "empty"

        files?.let {  }

        return null
    }

    fun arraysTest() {
        var arr = arrayOf(1, 2, 3)
        var arr2 = IntArray(3)
        arr[0] = 1
        arr.forEach { print(it) }
        arr.map { a -> a+1 }.filter { i -> i ==0 }
    }

    fun letApply() {
        var arr: Int? = null
        arr?.let {
            it.and(1)
            1
        }.let { print(1) }
        arr?.apply { and(1) }.toString()
    }

    fun funcTest() {
        fun t(a: Int) = true
        var f1: (Int) -> Boolean = ::t
        f1(1)

        f1 = { it > 0 }
        f1 = { a -> a > 0}
        f1 = fun (a: Int): Boolean {
            return a > 0
        }
    }
}

fun testHelloKotlin() {
    val helloKotlin = HelloKotlin()
    helloKotlin.a
    HelloKotlin.SB
    HelloKotlin.SMethod()
    HelloKotlin::ab
}

object AllStatic {
    val SSS = 1
    fun test() = print(1)
}

open class ParentCls {
    open fun hhh() {}
}

class Child1Cls(var height: Int): ParentCls() {
    override fun hhh() {}
}

abstract class MyAbstractClass {
    abstract fun doSomething()
}

val ww: Int = 1

fun main() {
    // 匿名内部类
    val myObject = object : MyAbstractClass() {
        override fun doSomething() {
            TODO("Not yet implemented")
        }
    }
    myObject.doSomething()

    //
    val btn = Button(AppApplication.getApp())
    btn.setOnClickListener { TODO("Not yet implemented") }

}