package com.example.learningapp.ndk

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.learningapp.R
import com.example.ndktool.NDKTool
import java.io.File
import java.util.*

class NDKActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ndkactivity)
        findViewById<View>(R.id.btn_get_str_jni)?.setOnClickListener {
            findViewById<TextView>(R.id.tv_str_jni)?.text = NDKTool.getStringFromJni()
        }
//        var path = filesDir.parentFile.absolutePath+"/"
//        path = "/sdcard/DCIM/"
//        Thread {
//            val size = NDKTool.getDirSizeByNative(path)
//            Log.d("NDKActivity", "size = " + size)
//        }.start()
//        Thread {
//            val st = System.currentTimeMillis()
//            val size = dirSize(path)
////            val size = File(path).walkTopDown()
////                .map { it.length() }
////                .sum()
//            Log.d("native", "fileCount = " + size + ", duration=" + (System.currentTimeMillis() - st))
//        }.start()
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