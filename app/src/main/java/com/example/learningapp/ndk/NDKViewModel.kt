package com.example.learningapp.ndk

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ndktool.NDKTool

class NDKViewModel: ViewModel() {

    val jniStr = MutableLiveData<String>()

    fun getStringFromJni() {
        jniStr.value = NDKTool.getStringFromJni()
    }

}