package com.example.learningapp.ndk

import android.os.Build
import android.os.Bundle
import android.os.Process.THREAD_PRIORITY_URGENT_AUDIO
import android.os.Process.setThreadPriority
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.learningapp.R
import com.example.learningapp.utils.FileUtil
import com.example.ndktool.NDKTool
import java.io.File
import java.util.*

class NDKActivity : AppCompatActivity() {

    lateinit var viewModel: NDKViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ndkactivity)
        viewModel = ViewModelProvider(this).get(NDKViewModel::class.java)
        viewModel.jniStr.observe(this, {
            findViewById<TextView>(R.id.tv_str_jni).setText(it)
        })
        findViewById<View>(R.id.btn_get_str_jni)?.setOnClickListener {
            viewModel.getStringFromJni()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FileUtil.getAppSize(this)
        }

        findViewById<View>(R.id.btn_get_dir_size).setOnClickListener {
            var path = filesDir.parentFile.absolutePath+"/"
            path = "/sdcard/DCIM/"
            android.os.Process.setThreadPriority(THREAD_PRIORITY_URGENT_AUDIO)
            Thread {
                android.os.Process.setThreadPriority(THREAD_PRIORITY_URGENT_AUDIO)
                val st = System.currentTimeMillis()
                val size = NDKTool.getDirSizeByNative(path)
//                val size = getDirSizeByJava(path)
                Log.d("native", "fileCount = " + size + ", duration=" + (System.currentTimeMillis() - st))
            }.apply {
                priority = Thread.MAX_PRIORITY
                start()
            }
//        Thread {
//            val st = System.currentTimeMillis()
//            val size = dirSize(path)
////            val size = File(path).walkTopDown()
////                .map { it.length() }
////                .sum()
//            Log.d("native", "fileCount = " + size + ", duration=" + (System.currentTimeMillis() - st))
//        }.start()
        }
    }

    fun getDirSizeByJava(path: String): Long {
        val file = File(path)
        if (file.isFile) {
            return 1;
        }
        var fileCount = 0L
        file.listFiles()?.forEach {
//            Log.d("native", "getDirSizeByJava " + it.absolutePath)
            if (it.isDirectory) {
                if (!it.name.equals(".") && !it.name.equals("..")) {
                    fileCount += getDirSizeByJava(it.absolutePath)
                }
            } else {
                fileCount += file.length();
            }
        }
        return fileCount
    }

    /**
     * Try this one for better performance
     * Mehran
     * Return the size of a directory in bytes
     */
    private fun dirSize(path: String): Long {
        val dir = File(path)
        var result: Long = 0
        val dirlist = LinkedList<File>()
        dirlist.clear()
        dirlist.push(dir)
        while (!dirlist.isEmpty()) {
            val dirCurrent: File = dirlist.pollLast() as File
            val fileList = dirCurrent.listFiles()
            for (f in fileList) {
                if (f.isDirectory) {
                    if (!f.name.equals(".") && !f.name.equals("..")) {
                        dirlist.push(f)
                    }
                }
                else
                    result += f.length()
            }
        }
        return result
    }

}