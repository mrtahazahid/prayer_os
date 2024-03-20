package com.iw.android.prayerapp.ui.activities.onBoarding

import android.os.Bundle
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.databinding.ActivityOnBoardingBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon

class OnBoardingActivity : BaseActivity() {

    private var _binding: ActivityOnBoardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarWithBlackIcon(R.color.bg_color)

        initialize()
        setOnClickListener()
    }

    override fun initialize() {

    }

    override fun setOnClickListener() {

    }
}