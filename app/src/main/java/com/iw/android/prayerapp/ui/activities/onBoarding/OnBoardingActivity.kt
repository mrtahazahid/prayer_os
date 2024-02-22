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

    val viewModel: OnBoardingViewModel by viewModels()

    private var gpsStatusListener: GpsStatusListener? = null

    private var turnOnGps: TurnOnGps? = null

    private var service: Intent? = null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }
                }

                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
            }
        }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarWithBlackIcon(R.color.bg_color)

        initialize()
        setOnClickListener()
    }


    override fun initialize() {

        //   tinyDB = TinyDB(this)

        service = Intent(this, LocationService::class.java)

        gpsStatusListener = GpsStatusListener(this)
        turnOnGps = TurnOnGps(this)

        checkPermissions()
    }

    override fun setOnClickListener() {

    }


//    override fun onResume() {
//        super.onResume()
//        checkPermissions()
//    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                enableGPSLocation()
            } else {
                startService(service)
                enableGPSLocation()
            }
        }
    }

    private fun enableGPSLocation() {

        var isGpsStatusChanged: Boolean? = null
        gpsStatusListener?.observe(this) { isGpsOn ->
            if (isGpsStatusChanged == null) {
                if (!isGpsOn) {
                    //Turn on GPS
                    turnOnGps?.startGPS(resultLauncher)
                }
                isGpsStatusChanged = isGpsOn
            } else {
                if (isGpsStatusChanged != isGpsOn) {
                    if (!isGpsOn) {
                        //Turn on GPS
                        turnOnGps?.startGPS(resultLauncher)
                    }
                    isGpsStatusChanged = isGpsOn
                }
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {

            } else if (activityResult.resultCode == RESULT_CANCELED) {

            }
        }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent) = lifecycleScope.launch {
        viewModel.saveUserLatLong(
            UserLatLong(
                locationEvent.latitude ?: 0.0,
                locationEvent.longitude ?: 0.0
            )
        )

        Log.d("CheckViewModel", "receiveLocationEvent: lat => ${locationEvent.latitude}")
        Log.d("CheckViewModel", "receiveLocationEvent: lon => ${locationEvent.longitude}")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        stopService(service)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }
}