package com.iw.android.prayerapp.ui.activities.onBoarding

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.databinding.ActivityOnBoardingBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon

class OnBoardingActivity : BaseActivity() {

    private var _binding: ActivityOnBoardingBinding? = null
    private val binding get() = _binding!!
    var data = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarWithBlackIcon(R.color.bg_color)

        initialize()
        setOnClickListener()
    }

    override fun initialize() {
        data = intent.getStringExtra("data").toString()
        setOnBackPressedListener()
    }

    override fun setOnClickListener() {

    }

    private fun setOnBackPressedListener() {
        onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if(!intent.getStringExtra("data").isNullOrEmpty()){
                        finish()
                    }else{

                    }

                }
            })
    }
}