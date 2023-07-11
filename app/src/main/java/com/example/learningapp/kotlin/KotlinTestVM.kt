package com.example.learningapp.kotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class KotlinTestVM: ViewModel() {

    fun test() {
        viewModelScope.launch {

        }
    }

}