package com.iw.android.prayerapp.ui.activities.onBoarding

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.ActivityOnBoardingBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.LocationEvent
import com.iw.android.prayerapp.services.gps.LocationService
import com.iw.android.prayerapp.services.gps.TurnOnGps
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class OnBoardingActivity : BaseActivity() {

    private var _binding: ActivityOnBoardingBinding? = null
    private val binding get() = _binding!!

    //  private lateinit var tinyDB: TinyDB





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