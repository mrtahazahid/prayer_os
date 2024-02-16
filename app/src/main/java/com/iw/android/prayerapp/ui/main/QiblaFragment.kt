package com.iw.android.prayerapp.ui.main

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
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentQiblaBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.AppConstant
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.TinyDB
import kotlin.math.roundToLong


class QiblaFragment : BaseFragment(R.layout.fragment_qibla), SensorEventListener{

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
    ): View {
        _binding = FragmentQiblaBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        (requireActivity() as MainActivity).showBottomSheet()



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        tinyDB = TinyDB(context)

        sensorManager = (requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager)

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!

        currentLatitude = if (tinyDB.getDouble(AppConstant.CURRENT_LATITUDE).isNaN() || tinyDB.getDouble(AppConstant.CURRENT_LATITUDE)
                .toString().isEmpty()
        ) {
            0.0
        } else {
            tinyDB.getDouble(AppConstant.CURRENT_LATITUDE)
        }

        currentLongitude = if (tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE).isNaN() || tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE)
                .toString().isEmpty()
        ) {
            0.0
        } else {
            tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE)
        }

        val getQibla = GetAdhanDetails.getQiblaDirection(currentLatitude, currentLongitude)

        binding.txtQiblaHeading.text = "Qibla direction is ${"%.2f".format(getQibla.direction)}\u00B0 from North"
    }

    override fun setObserver() {

    }

    override fun setOnClickListener() {

    }

    override fun onResume() {
        super.onResume()
        val sensorLst = sensorManager.getSensorList(Sensor.TYPE_ALL)

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

        val degree = (event?.values?.get(0)!!).roundToLong()
        val rotateAnimation = RotateAnimation(currentDegree, (-degree).toFloat() /*-267.69f*/, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)

        rotateAnimation.duration = 210
        rotateAnimation.fillAfter = true
        binding.imageViewQiblaDirection.startAnimation(rotateAnimation)
        currentDegree = (degree).toFloat() /*-267.69f*/



    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }
}