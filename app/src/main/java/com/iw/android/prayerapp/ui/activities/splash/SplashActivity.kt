package com.iw.android.prayerapp.ui.activities.splash

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.IS_FIRST_TIME_APP_LAUNCH
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.databinding.ActivitySplashBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : BaseActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!
    private var preference:DataPreference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        setOnClickListener()
    }

    override fun initialize() {
        setStatusBarWithBlackIcon(R.color.bg_color)
        preference = DataPreference(this)
        lifecycleScope.launch {
            if (preference?.getBooleanData(IS_FIRST_TIME_APP_LAUNCH) == true) {
                delay(2000)
                preference?.setBooleanData(IS_FIRST_TIME_APP_LAUNCH, false)
            }
            val activity = if (preference?.getBooleanData(IS_ONBOARDING) == true) {
                MainActivity::class.java
            } else {
                OnBoardingActivity::class.java
            }
            startActivity(Intent(this@SplashActivity, activity))
            finish()

        }
    }

    override fun setOnClickListener() {}


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}