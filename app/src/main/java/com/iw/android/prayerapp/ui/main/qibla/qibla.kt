//package com.iw.android.prayerapp.ui.main.qibla
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.SharedPreferences
//import android.content.pm.PackageManager
//import android.location.Geocoder
//import android.os.Build
//import android.os.Bundle
//import android.view.animation.Animation
//import android.view.animation.RotateAnimation
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.app.ActivityCompat
//import java.io.IOException
//import java.util.*
//import kotlin.math.ceil
//import kotlin.math.floor
//import kotlin.math.round
//
//class MainActivity : AppCompatActivity() {
//AppCompatActivity    private lateinit var compass: Compass
//    private lateinit var compassOuter: ImageView
//    private lateinit var compassDegree: ImageView
//    private lateinit var compassArrow: ImageView
//    private lateinit var textDegree: TextView
//    private var currentAzimuth: Float = 0f
//    private lateinit var prefs: SharedPreferences
//    private lateinit var gps: GPSTracker
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        prefs = getSharedPreferences("", MODE_PRIVATE)
//        gps = GPSTracker(this)
//
//        compassOuter = findViewById(R.id.compassOuter)
//        compassDegree = findViewById(R.id.compassDegree)
//        compassArrow = findViewById(R.id.compassArrow)
//
//
//        setupCompass()
//        fetchGPS()
//
//        val qiblaDeg = "${round(getFloat("qibla_degree"))}°"
//        textDegree.text = qiblaDeg
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//        compass.start()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        compass.stop()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        compass.stop()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        compass.start()
//    }
//
//    fun saveBoolean(key: String, value: Boolean) {
//        val editor = prefs.edit()
//        editor.putBoolean(key, value)
//        editor.apply()
//    }
//
//    fun getBoolean(key: String): Boolean {
//        return prefs.getBoolean(key, false)
//    }
//
//    fun saveFloat(key: String, value: Float) {
//        val editor = prefs.edit()
//        editor.putFloat(key, value)
//        editor.apply()
//    }
//
//    fun getFloat(key: String): Float {
//        return prefs.getFloat(key, 0f)
//    }
//
//    private fun setupCompass() {
//        val permissionGranted = getBoolean("permission_granted")
//
//        if (permissionGranted) {
//            getBearing()
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                    1
//                )
//            }
//        }
//
//        compass = Compass(this)
//        val compassListener = object : Compass.CompassListener {
//            override fun onNewAzimuth(azimuth: Float) {
//                adjustCompassDegree(azimuth)
//                adjustCompassArrow(azimuth)
//            }
//        }
//
//        compass.setListener(compassListener)
//    }
//
//    fun adjustCompassDegree(azimuth: Float) {
//        val animation = RotateAnimation(-currentAzimuth, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//        currentAzimuth = azimuth
//        animation.duration = 500
//        animation.repeatCount = 0
//        animation.fillAfter = true
//        compassDegree.startAnimation(animation)
//    }
//
//    fun adjustCompassArrow(azimuth: Float) {
//        val qiblaDegree = getFloat("qibla_degree")
//        val animation = RotateAnimation(-(currentAzimuth) + qiblaDegree, -azimuth, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
//        currentAzimuth = azimuth
//
//        val minAzimuth = (floor(getFloat("qibla_degree")) - 3).toInt()
//        val maxAzimuth = (ceil(getFloat("qibla_degree")) + 3).toInt()
//
//        if (currentAzimuth >= minAzimuth && currentAzimuth <= maxAzimuth) {
//            compassOuter.setImageResource(R.drawable.compass_outer_green)
//        } else {
//            compassOuter.setImageResource(R.drawable.compass_outer_gray)
//        }
//
//        animation.duration = 500
//        animation.repeatCount = 0
//        animation.fillAfter = true
//        compassArrow.startAnimation(animation)
//    }
//
//    @SuppressLint("MissingPermission")
//    fun getBearing() {
//        val qiblaDegree = getFloat("qibla_degree")
//        if (qiblaDegree <= 0.0001) {
//            fetchGPS()
//        }
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//        when (requestCode) {
//            1 -> {
//                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    saveBoolean("permission_granted", true)
//                    setupCompass()
//                } else {
//                    Toast.makeText(applicationContext, "Permission tidak diizinkan!", Toast.LENGTH_LONG).show()
//                    finish()
//                }
//                return
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//    }
//
//    fun fetchGPS() {
//        val result: Double
//        gps = GPSTracker(this)
//        if (gps.canGetLocation()) {
//            val myLat = gps.latitude
//            val myLon = gps.longitude
//
//            if (myLat >= 0.001 && myLon >= 0.001) {
//                // posisi ka'bah
//                val kabahLat = Math.toRadians(21.422487)
//                val kabahLon = 39.826206
//                val myRadiansLat = Math.toRadians(myLat)
//                val lonDiff = Math.toRadians(kabahLon - myLon)
//                val y = Math.sin(lonDiff) * Math.cos(kabahLat)
//                val x = Math.cos(myRadiansLat) * Math.sin(kabahLat) - Math.sin(myRadiansLat) * Math.cos(kabahLat) * Math.cos(lonDiff)
//                result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
//                saveFloat("qibla_degree", result.toFloat())
//            }
//        } else {
//            gps.showSettingAlert()
//        }
//    }
//}
