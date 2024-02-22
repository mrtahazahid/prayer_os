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
import androidx.fragment.app.viewModels
import com.batoulapps.adhan2.Qibla
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentQiblaBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.settingFragment.SettingViewModel
import com.iw.android.prayerapp.utils.GetAdhanDetails


class QiblaFragment : BaseFragment(R.layout.fragment_qibla), SensorEventListener {

    private var _binding: FragmentQiblaBinding? = null
    private val binding get() = _binding!!
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private val viewModel: SettingViewModel by viewModels()

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor
    private var currentDegree = 0f
    private var qiblaDegree = 0f
    private var currentOrientation = FloatArray(3)
    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    lateinit var getQibla: Qibla

    private val ALPHA = 0.2f


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
        currentLatitude = viewModel.getUserLatLong?.latitude ?: 0.0
        currentLongitude = viewModel.getUserLatLong?.longitude ?: 0.0

        Log.d("currentLatitude",(viewModel.getUserLatLong?.latitude ?: 0.0).toString())
        Log.d("currentLongitude",(viewModel.getUserLatLong?.longitude ?: 0.0).toString())

        sensorManager = (requireActivity().getSystemService(SENSOR_SERVICE) as SensorManager)
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)!!

        getQibla = GetAdhanDetails.getQiblaDirection(currentLatitude, currentLongitude)
        binding.txtQiblaHeading.text =
            "Qibla direction is ${"%.2f".format(getQibla.direction)}\u00B0 from North"
    }

    override fun setObserver() {

    }

    override fun setOnClickListener() {

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
            this,
            accelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager.registerListener(
            this,
            magnetometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                lowPassFilter(event.values, accelerometerReading)
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                lowPassFilter(event.values, magnetometerReading)
            }
        }

        updateOrientationAngles()
        val rotateAnimation = RotateAnimation(
            currentDegree,
            -qiblaDegree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        rotateAnimation.duration = 20000
        rotateAnimation.fillAfter = true
        binding.imageViewQiblaDirection.startAnimation(rotateAnimation)
        currentDegree = -qiblaDegree

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    private fun lowPassFilter(input: FloatArray, output: FloatArray) {
        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
    }

    private fun updateOrientationAngles() {
        val rotationMatrix = FloatArray(9)
        if (SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )
        ) {
            SensorManager.getOrientation(rotationMatrix, currentOrientation)
            val radian = currentOrientation[0].toDouble()
            val qiblaRadians = getQibla.direction
            qiblaDegree = ((qiblaRadians - radian) * 180 / Math.PI).toFloat()
            if (qiblaDegree < 0) {
                qiblaDegree += 360f
            }
        }
    }
}