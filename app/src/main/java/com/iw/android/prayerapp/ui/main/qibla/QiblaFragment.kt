package com.iw.android.prayerapp.ui.main.qibla

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentQiblaBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.qibla.QiblaDegreeListener
import kotlin.math.roundToInt

class QiblaFragment : BaseFragment(R.layout.fragment_qibla) {

    private var _binding: FragmentQiblaBinding? = null
    private val binding get() = _binding!!
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private val viewModel: QiblaViewModel by viewModels()

    private lateinit var gps: GPSTracker
    private var getQibla: Double = 0.0

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
        setOnBackPressedListener()
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
        Log.d("lat,long","$currentLatitude $currentLongitude")
        getQibla = GetAdhanDetails.getQiblaDirection(currentLatitude, currentLongitude)
//        binding.imageViewQiblaDirection.degree = getQibla.toFloat()
//        binding.imageViewQiblaDirection.location =  Location(location?.city)

        // Pass Location to QiblaCompassViewV2
        val locations = Location("GPS")
        locations.latitude = currentLatitude
        locations.longitude = currentLongitude

        binding.imageViewQiblaDirection.location = locations
        binding.imageViewQiblaDirection.degree = getQibla.toFloat()



        binding.txtQiblaHeading.text =
            "Qibla direction is ${"%.2f".format(getQibla)}\u00B0 from North"

    }

    override fun setObserver() {}
    override fun setOnClickListener() {


        binding.imageViewQiblaDirection.degreeListener = object : QiblaDegreeListener {
            override fun onDegreeChange(degree: Float) {
                val qiblaDegree = getQibla.toFloat()
                val currentAzimuthInt = degree.toInt()
                val qiblaDegreeInt = qiblaDegree.toInt()
                val tolerance = 1
                val isAligned =
                    (currentAzimuthInt in (qiblaDegreeInt - tolerance)..(qiblaDegreeInt + tolerance))

                binding.textViewCurrentDirection.text = degree.roundToInt().toString()
                val color = if (isAligned) R.color.app_green else R.color.white
                binding.imageViewQiblaDirection.imageNeedle.imageTintList =
                    ContextCompat.getColorStateList(requireContext(), color)
                binding.textViewCurrentDirection.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        color
                    )
                )
            }
        }
    }


    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }


}
