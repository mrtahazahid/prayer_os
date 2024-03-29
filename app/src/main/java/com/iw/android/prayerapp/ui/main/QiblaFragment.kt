package com.iw.android.prayerapp.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.batoulapps.adhan2.Qibla
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentQiblaBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.settingFragment.SettingViewModel
import com.iw.android.prayerapp.utils.GetAdhanDetails
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan


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
    val handler: Handler = Handler()
    private val ALPHA = 0.08f


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

    @SuppressLint("SetTextI18n")
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
            val latitude = currentLatitude * Math.PI / 180
            val longitude = currentLongitude * Math.PI / 180
            val qiblaRadians = calculateQiblaDirection(latitude, longitude)
//            val qiblaRadians = getQibla.direction


            qiblaDegree = ((qiblaRadians - radian) * 180 / Math.PI).toFloat()
//            binding.textViewCurrentDirection.text = "%.2f".format(qiblaDegree)
            binding.textViewCurrentDirection.text = "${"%.2f".format((getQibla.direction - radian) - 1.35)}°"

            Log.d("qiblaDegree","mainDegree -->>> ${getQibla.direction}")
            Log.d("qiblaDegree","SecondDegree -->>> ${binding.textViewCurrentDirection.text}")


            val tolerance = 0.01 // Set your tolerance level as needed


            if (abs(getQibla.direction - ("${"%.2f".format((getQibla.direction - radian) - 1.35)}").toDouble()) < tolerance) {
                binding.imageViewQiblaDirection.imageTintList =  ContextCompat.getColorStateList(requireContext(),R.color.app_green)
            } else {
                binding.imageViewQiblaDirection.imageTintList =   ContextCompat.getColorStateList(requireContext(),R.color.white)
            }


            if (binding.textViewCurrentDirection.text == "%.2f".format((getQibla.direction - radian) - 1.35)) {
                Log.d("qiblaDegree","matched -->>> ")
            }

//            binding.textViewCurrentDirection.text = "${"%.2f".format((qiblaRadians - radian) - 2.38)}°"
            if (qiblaDegree < 0) {
                qiblaDegree += 360f
            }
        }
    }

    private fun calculateQiblaDirection(latitude: Double, longitude: Double): Double {
        val makkahLatitude = 21.422510 * Math.PI / 180.0
        val makkahLongitude = 39.826168 * Math.PI / 180.0
        val valueA = latitude
        val valueB = longitude
        val result = atan2(
            sin(makkahLatitude - valueA),
            cos(valueA) * tan(makkahLatitude) - sin(valueA) * cos(makkahLongitude - valueB)
        )
        return (result + Math.PI * 2) % (Math.PI * 2)
    }
    fun vibrateForOneSecond() {
        val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Vibrate for 1 second using VibrationEffect API
                vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // Deprecated in API 26, but still used for older devices
                @Suppress("DEPRECATION")
                vibrator.vibrate(1000)
            }
        }
    }
}