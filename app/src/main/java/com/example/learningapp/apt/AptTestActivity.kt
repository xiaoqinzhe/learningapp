package com.example.learningapp.apt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.generated.layout.ActivityAptTestLayoutBinding
import com.example.learningapp.R
import com.example.libannotation.BindActivity
import com.example.libannotation.BindLayouts
import com.example.libannotation.BindView

@BindActivity
@BindLayouts(layouts = ["activity_apt_test"])
class AptTestActivity : AppCompatActivity() {

    @BindView(viewId = R.id.title)
    lateinit var titleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityAptTestLayoutBinding.createView(this))
        BindViewAptTestActivity.bindViews(this)
        titleTv.setText(getString(com.example.ndktool.R.string.test_string))
    }
}