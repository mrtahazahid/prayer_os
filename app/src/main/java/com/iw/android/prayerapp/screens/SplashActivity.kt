package com.iw.android.prayerapp.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.ActivitySplashBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.utils.AppConstant.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.utils.TinyDB

class SplashActivity : AppCompatActivity() {
    private var _binding: ActivitySplashBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarWithBlackIcon(R.color.bg_color)

        tinyDB = TinyDB(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if(tinyDB.getBoolean(IS_ONBOARDING)){
                startActivity(Intent(this, MainActivity::class.java))
            }
            else{
                startActivity(Intent(this,OnBoardingActivity::class.java))
            }
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}