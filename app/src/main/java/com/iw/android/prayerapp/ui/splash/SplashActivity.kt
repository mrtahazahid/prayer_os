package com.iw.android.prayerapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.databinding.ActivitySplashBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.screens.MainActivity
import com.iw.android.prayerapp.ui.activities.screens.OnBoardingActivity
import com.iw.android.prayerapp.utils.AppConstant.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.utils.TinyDB


class SplashActivity : BaseActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        setOnClickListener()
    }

    override fun initialize() {
        setStatusBarWithBlackIcon(R.color.bg_color)

        tinyDB = TinyDB(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if(tinyDB.getBoolean(IS_ONBOARDING)){
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                startActivity(Intent(this, OnBoardingActivity::class.java))
            }
        }, 3000)
    }

    override fun setOnClickListener() {}



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}