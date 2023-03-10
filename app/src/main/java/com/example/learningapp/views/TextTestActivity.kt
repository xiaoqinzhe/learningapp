package com.example.learningapp.views

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.example.learningapp.R
import kotlinx.android.synthetic.main.activity_text_test.*


class TextTestActivity : AppCompatActivity() {

    private lateinit var colorSpan: ForegroundColorSpan
    private lateinit var ssb: SpannableStringBuilder

    @BindView(R.id.text_span)
    lateinit var textSpan: TextView

    @BindView(R.id.btn_change_span)
    lateinit var btnChangeSpan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_test)
        ButterKnife.bind(this)
        spannableTest()
        btnChangeSpan.setOnClickListener {
            changeSpan()
        }
    }

    private fun spannableTest() {
        ssb = SpannableStringBuilder()
        ssb.append("text").append("user").append("other")
        colorSpan = ForegroundColorSpan(Color.parseColor("#FF00FF"))
        ssb.setSpan(colorSpan, 4, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(ForegroundColorSpan(Color.parseColor("#00FF00")), 8, 13, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textSpan.text = ssb
    }

    private fun changeSpan() {
//        ssb.removeSpan(colorSpan)
        ssb.replace(4, 8, "newUser")
        textSpan.text = ssb
    }

}