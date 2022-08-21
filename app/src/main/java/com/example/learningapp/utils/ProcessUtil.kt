package com.example.learningapp.utils

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import android.os.Process
import android.util.Log

object ProcessUtil {

    private const val TAG = "ProcessUtil"

    fun testApi(context: Context) {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        Log.d(TAG, "$memInfo")

        activityManager.runningAppProcesses
        val processMemInfo = activityManager.getProcessMemoryInfo(intArrayOf(Process.myPid()))
        processMemInfo[0].totalPss

        val debugMemInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(debugMemInfo)
    }

}