package com.example.learningapp.jetpack.databinding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.learningapp.R
import com.example.learningapp.databinding.ActivityDataBindingBinding

class DataBindingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDataBindingBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_binding)
        binding.lifecycleOwner = this
        val data = DataBindInfo()
        data.name.value = "name"
        data.content.value = "content"
        binding.databindData = data

        binding.vm = ViewModelProvider(this).get(DViewModel::class.java)
    }

    inner class DataBindInfo {
        val name = MutableLiveData<String>() // livedata 可以自动通知ui更新
        val content = MutableLiveData<String>()
    }

    class DViewModel: ViewModel() {

        private val number = MutableLiveData<Int>(3)

        fun getNumber(): MutableLiveData<Int> {
            return number
        }

        fun add() {
            number.value = number.value?.plus(1)
        }

    }

}