package com.iw.android.prayerapp.ui.main.qibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.batoulapps.adhan2.Qibla
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentQiblaBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.floor


class QiblaFragment : BaseFragment(R.layout.fragment_qibla) {

    private var _binding: FragmentQiblaBinding? = null
    private val binding get() = _binding!!
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private val viewModel: QiblaViewModel by viewModels()

    private lateinit var compass: Compass
    private var currentAzimuth: Float = 0f
    private lateinit var gps: GPSTracker

    lateinit var getQibla: Qibla


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
        gps = GPSTracker(requireContext())
        currentLatitude = viewModel.getUserLatLong?.latitude ?: 0.0
        currentLongitude = viewModel.getUserLatLong?.longitude ?: 0.0

        val location = GetAdhanDetails.getTimeZoneAndCity(
            requireContext(), currentLatitude,
            currentLongitude
        )
        binding.textViewTitle.text = location?.city ?: "City"

        getQibla = GetAdhanDetails.getQiblaDirection(currentLatitude, currentLongitude)
        binding.txtQiblaHeading.text =
            "Qibla direction is ${"%.2f".format(getQibla.direction)}\u00B0 from North"

        setupCompass()
        fetchGPS()


    }

    override fun setObserver() {

    }

    override fun setOnClickListener() {

    }


    override fun onStop() {
        super.onStop()
        compass.stop()
    }

    override fun onPause() {
        super.onPause()
        compass.stop()
    }

    override fun onResume() {
        super.onResume()
        compass.start()
    }

    fun saveBoolean(value: Boolean) = lifecycleScope.launch {
        viewModel.setBoolean(value)
    }

    suspend fun getBoolean(): Boolean {
        return viewModel.getBoolean()
    }

    fun saveFloat(value: Float) = lifecycleScope.launch {
        viewModel.saveFloat(value)
    }

    suspend fun getFloat(): Float {
        return viewModel.getFloat()
    }

    private fun setupCompass() = lifecycleScope.launch {
        val permissionGranted = getBoolean()
        requireActivity().runOnUiThread {
            if (permissionGranted) {
                getBearing()
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        1
                    )
                }
            }

            compass = Compass(requireActivity())
            val compassListener =
                Compass.CompassListener { azimuth ->
                    adjustCompassArrow(azimuth)
                }

            compass.setListener(compassListener)
        }

    }

    fun adjustCompassArrow(azimuth: Float) = lifecycleScope.launch {
        val qiblaDegree = getFloat()
        val minAzimuth = (floor(getFloat()) - 3).toInt()
        val maxAzimuth = (ceil(getFloat()) + 3).toInt()
        requireActivity().runOnUiThread {
            val animation = RotateAnimation(
                -(currentAzimuth) + qiblaDegree,
                -azimuth,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            currentAzimuth = azimuth




            binding.imageViewQiblaDirection.imageTintList =
                if (currentAzimuth >= minAzimuth && currentAzimuth <= maxAzimuth) {
                    ContextCompat.getColorStateList(requireContext(), R.color.app_green)
                } else {
                    ContextCompat.getColorStateList(requireContext(), R.color.white)
                }

            animation.duration = 500
            animation.repeatCount = 0
            animation.fillAfter = true
            binding.imageViewQiblaDirection.startAnimation(animation)
            val formattedNumber = DecimalFormat("#.#").format(currentAzimuth)
            binding.textViewCurrentDirection.text = formattedNumber
        }

    }

    @SuppressLint("MissingPermission")
    fun getBearing() = lifecycleScope.launch {
        val qiblaDegree = getFloat()
        if (qiblaDegree <= 0.0001) {
            fetchGPS()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveBoolean(true)
                    setupCompass()
                } else {
                    showToast("Location permission denied")
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun fetchGPS() {
        val result: Double
        gps = GPSTracker(requireContext())
        if (gps.canGetLocation()) {
            val myLat = gps.latitude
            val myLon = gps.longitude

            if (myLat >= 0.001 && myLon >= 0.001) {
                // posisi ka'bah
                val kabahLat = Math.toRadians(21.422487)
                val kabahLon = 39.826206
                val myRadiansLat = Math.toRadians(myLat)
                val lonDiff = Math.toRadians(kabahLon - myLon)
                val y = Math.sin(lonDiff) * Math.cos(kabahLat)
                val x =
                    Math.cos(myRadiansLat) * Math.sin(kabahLat) - Math.sin(myRadiansLat) * Math.cos(
                        kabahLat
                    ) * Math.cos(lonDiff)
                result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
                saveFloat(result.toFloat())
            }
        } else {
            gps.showSettingAlert()
        }
    }
}
