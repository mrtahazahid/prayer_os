package com.iw.android.prayerapp.ui.activities.onBoarding

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
        if (Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            ViewCompat.setOnApplyWindowInsetsListener(binding.mainView) { v: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, systemBars.top, 0, systemBars.bottom)
                insets
            }
        }

        setStatusBarWithBlackIcon(R.color.black)
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
                    if (!intent.getStringExtra("data").isNullOrEmpty()) {
                        finish()
                    }
                }
            })
    }
}