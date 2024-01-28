package com.iw.android.prayerapp.fragments

import android.annotation.SuppressLint
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentQiblaBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.utils.AppConstant
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.TinyDB


class QiblaFragment : Fragment(), SensorEventListener{

    private var _binding : FragmentQiblaBinding? = null
    private val binding get() = _binding!!

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private lateinit var tinyDB: TinyDB

    private lateinit var sensorManager: SensorManager

    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor

    private var currentDegree = 0f

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQiblaBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)

        tinyDB = TinyDB(context)

        sensorManager = (requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager)

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!

        if (tinyDB.getDouble(AppConstant.CURRENT_LATITUDE).isNaN() || tinyDB.getDouble(AppConstant.CURRENT_LATITUDE)
                .toString().isNullOrEmpty()
        ) {
            currentLatitude = 0.0
        } else {
            currentLatitude = tinyDB.getDouble(AppConstant.CURRENT_LATITUDE)
        }

        if (tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE).isNaN() || tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE)
                .toString().isNullOrEmpty()
        ) {
            currentLongitude = 0.0
        } else {
            currentLongitude = tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE)
        }

        val getQibla = GetAdhanDetails.getQiblaDirection(currentLatitude, currentLongitude)

        binding.txtQiblaHeading.text = "Qibla direction is ${"%.2f".format(getQibla.direction)}\u00B0 from North"


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        var sensorLst = sensorManager.getSensorList(Sensor.TYPE_ALL)

        sensorLst.iterator().forEach {
            Log.d("CheckMobSensor", "onResume: ${it.name}")
        }

//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {

        val degree = Math.round(event?.values?.get(0)!!)
        val rotateAnimation = RotateAnimation(currentDegree, (-degree).toFloat() /*-267.69f*/, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)

        rotateAnimation.duration = 210
        rotateAnimation.fillAfter = true
        binding.ivQiblaDirection.startAnimation(rotateAnimation)
        currentDegree = (-degree).toFloat() /*-267.69f*/

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}