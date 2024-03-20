package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.FragmentThirdOnboardingBinding
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.LocationEvent
import com.iw.android.prayerapp.services.gps.LocationService
import com.iw.android.prayerapp.services.gps.TurnOnGps
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class ThirdOnboarding : BaseFragment(R.layout.fragment_third_onboarding) {

    private var _binding: FragmentThirdOnboardingBinding? = null
    private val binding get() = _binding!!

    val viewModel: OnBoardingViewModel by viewModels()

    private var isButtonForNext = false
    private var lat = 0.0
    private var long = 0.0

    private var gpsStatusListener: GpsStatusListener? = null

    private var turnOnGps: TurnOnGps? = null

    private var service: Intent? = null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"
            } else {
                showToast("Permission denied")
            }
        }

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                requireActivity(),
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    } else {
                        showToast("granted")
                    }
                }

                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                }
            }
        }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this@ThirdOnboarding)) {
            EventBus.getDefault().register(this@ThirdOnboarding)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        setOnBackPressedListener()
        service = Intent(requireContext(), LocationService::class.java)
        gpsStatusListener = GpsStatusListener(requireContext())
        turnOnGps = TurnOnGps(requireContext())

    }

    override fun setObserver() {
    }

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            if(isButtonForNext){
                    lifecycleScope.launch {
                        val args = Bundle()
                        args.putString("lat", viewModel.getUserLatLong()?.latitude.toString())
                        args.putString("long", viewModel.getUserLatLong()?.longitude.toString())
                        requireActivity().runOnUiThread {
                            findNavController().navigate(
                                R.id.action_thirdOnboarding_to_fourthOnboarding,
                                args
                            )
                        }
                    }
            }else{

                checkPermissions()
            }


        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_thirdOnboarding_to_fourthOnboarding)
        }

        binding.skip.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                ).putExtra("skip", "userSkipped")
            )
            requireActivity().finish()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireActivity().stopService(service)
        if (EventBus.getDefault().isRegistered(this@ThirdOnboarding)) {
            EventBus.getDefault().unregister(this@ThirdOnboarding)
        }

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    requireActivity(),
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
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"
                requireActivity().startService(service)
                enableGPSLocation()
            }
        }
    }

    private fun enableGPSLocation() {

        var isGpsStatusChanged: Boolean? = null
        gpsStatusListener?.observe(requireActivity()) { isGpsOn ->
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
            if (activityResult.resultCode == AppCompatActivity.RESULT_OK) {

            } else if (activityResult.resultCode == AppCompatActivity.RESULT_CANCELED) {

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
        isButtonForNext = true
        binding.btnEnableNotification.text = "Next"
        lat = locationEvent.latitude ?: 0.0
        long = locationEvent.longitude ?: 0.0

    }


    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })
    }

}

