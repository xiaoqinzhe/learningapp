package com.example.learningapp.apt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.learningapp.R

/**
 * https://github.com/JakeWharton/butterknife.git
 */
class ButterKnifeTestActivity : AppCompatActivity() {

    @BindView(R.id.title)
    lateinit var titleView: TextView

    @BindView(R.id.btn)
    lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_butter_knife_test)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.btn, R.id.title)
    fun onClick(view: View) {

    }

}