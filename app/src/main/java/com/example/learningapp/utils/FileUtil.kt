package com.example.learningapp.utils

import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.storage.StorageManager
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File

object FileUtil {

    private const val TAG = "FileUtil"

    @RequiresApi(Build.VERSION_CODES.O)
    @JvmStatic
    fun getAppSize(context: Context): Long {
        try {
            val storageStatsManager = context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val file = File(context.dataDir.absolutePath)
            val uuid = storageManager.getUuidForPath(file)
            val uid = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA).uid
            val storageStat = storageStatsManager.queryStatsForUid(uuid, uid)
            val totalSize = storageStat.appBytes + storageStat.dataBytes + storageStat.cacheBytes
            Log.d(TAG, "getAppSize totalSize= $totalSize, appBytes=${storageStat.appBytes}, " +
                    "dataBytes=${storageStat.dataBytes}, cacheBytes=${storageStat.cacheBytes}")
            return totalSize
        } catch (e: Exception) {
            Log.d(TAG, "getAppSize error ${e.message}")
        }
        return 0
    }

}