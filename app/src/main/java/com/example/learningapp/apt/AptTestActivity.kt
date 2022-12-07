package com.example.learningapp.apt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.learningapp.R
import com.example.libannotation.BindActivity
import com.example.libannotation.BindView

@BindActivity
class AptTestActivity : AppCompatActivity() {

    @BindView(viewId = R.id.title)
    lateinit var titleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apt_test)
        BindViewAptTestActivity.bindViews(this)
        titleTv.setText("from apt")
    }
}