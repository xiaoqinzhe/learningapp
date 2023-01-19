package com.example.learningapp.apt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.apt_api.LayoutBinding
import com.example.generated.layout.ActivityAptTestLayoutBinding
import com.example.learningapp.R
import com.example.libannotation.BindActivity
import com.example.libannotation.BindLayouts
import com.example.libannotation.BindView

@BindActivity
@BindLayouts(layouts = ["activity_apt_test"])
class AptTestActivity : AppCompatActivity() {

    @BindView(viewId = R.id.titleTv)
    lateinit var titleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. use apt class: ActivityAptTestLayoutBinding
//        val binding = ActivityAptTestLayoutBinding()
//        setContentView(binding.createView(this))
//        titleTv = binding.titleTv

        // 2. use LayoutBinding class
//        LayoutBinding.setContentView(this, R.layout.activity_apt_test)

        // 3. auto replace method setContentView or inflate
        setContentView(R.layout.activity_apt_test)
//        setContentView(layoutInflater.inflate(R.layout.activity_apt_test, null))

//        BindViewAptTestActivity.bindViews(this)
//         titleTv.setText("title not null")

    }
}