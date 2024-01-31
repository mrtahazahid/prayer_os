package com.iw.android.prayerapp.screens

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.ActivityMainBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.fragments.moreFragment.MoreFragment
import com.iw.android.prayerapp.fragments.PrayerFragment
import com.iw.android.prayerapp.fragments.QiblaFragment
import com.iw.android.prayerapp.fragments.SettingFragment
import com.iw.android.prayerapp.fragments.timeFragment.TimeFragment
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.LocationEvent
import com.iw.android.prayerapp.services.gps.LocationService
import com.iw.android.prayerapp.services.gps.TurnOnGps
import com.iw.android.prayerapp.utils.AppConstant.Companion.CURRENT_LATITUDE
import com.iw.android.prayerapp.utils.AppConstant.Companion.CURRENT_LONGITUDE
import com.iw.android.prayerapp.utils.TinyDB
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    private var _binding : ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    private var gpsStatusListener : GpsStatusListener? = null

    private var turnOnGps : TurnOnGps? = null

    private var service: Intent?=null

    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) { if (it) { } }

    private val locationPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
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
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarWithBlackIcon(R.color.bg_color)


        tinyDB = TinyDB(this)

        service = Intent(this, LocationService::class.java)

        gpsStatusListener = GpsStatusListener(this)
        turnOnGps = TurnOnGps(this)

        replaceFragment(PrayerFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.prayer_screen -> replaceFragment(PrayerFragment())
                R.id.time_screen -> replaceFragment(TimeFragment())
                R.id.qibla_screen -> replaceFragment(QiblaFragment())
                R.id.setting_screen -> replaceFragment(SettingFragment())
                R.id.more_screen -> replaceFragment(MoreFragment())
            }
            true
        }

    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

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
            }else{
                startService(service)
                enableGPSLocation()
            }
        }
    }

    private fun enableGPSLocation() {

        var isGpsStatusChanged : Boolean? = null
        gpsStatusListener?.observe(this){ isGpsOn ->
            if(isGpsStatusChanged == null){
                if(!isGpsOn){
                    //Turn on GPS
                    turnOnGps?.startGPS(resultLauncher)
                }
                isGpsStatusChanged = isGpsOn
            } else{
                if(isGpsStatusChanged != isGpsOn){
                    if(!isGpsOn){
                        //Turn on GPS
                        turnOnGps?.startGPS(resultLauncher)
                    }
                    isGpsStatusChanged = isGpsOn
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){activityResult ->
        if(activityResult.resultCode == AppCompatActivity.RESULT_OK){

        }
        else if(activityResult.resultCode == AppCompatActivity.RESULT_CANCELED){

        }
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
        locationEvent.latitude?.let { tinyDB.putDouble(CURRENT_LATITUDE, it) }
        locationEvent.longitude?.let { tinyDB.putDouble(CURRENT_LONGITUDE, it) }

        Log.d("CheckViewModel", "receiveLocationEvent: lat => ${locationEvent.latitude}")
        Log.d("CheckViewModel", "receiveLocationEvent: lon => ${locationEvent.longitude}")
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        stopService(service)
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
    }
}