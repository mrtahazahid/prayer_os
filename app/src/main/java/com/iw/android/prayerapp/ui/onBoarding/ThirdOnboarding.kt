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
import androidx.core.content.ContextCompat
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
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
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

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"
                requireActivity().startService(service)
                enableGPSLocation()
            } else {
                showToast("Location permission denied")
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

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            if (isButtonForNext) {
                lifecycleScope.launch {
                    val args = Bundle()
                    val userLatLong = viewModel.getUserLatLong()
                    if (userLatLong?.latitude != null && userLatLong.longitude != null) {
                        args.putDouble("lat", userLatLong.latitude)
                        args.putDouble("long", userLatLong.longitude)
                        findNavController().navigate(
                            R.id.action_thirdOnboarding_to_fourthOnboarding,
                            args
                        )
                    } else {
                        binding.btnEnableNotification.text = "Fetching Location"
                        binding.progress.show()
                        service?.let { it1 ->
                            ContextCompat.startForegroundService(requireContext(),
                                it1
                            )
                        }
                        enableGPSLocation()
                    }
                }
            } else {
                checkPermissions()
            }
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_thirdOnboarding_to_fourthOnboarding)
        }

        binding.skip.setOnClickListener {
            requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
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
                ) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
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
            } else {
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"
                service?.let { it1 ->
                    ContextCompat.startForegroundService(requireContext(),
                        it1
                    )
                }
                enableGPSLocation()
            }
        }
    }

    private fun enableGPSLocation() {
        var isGpsStatusChanged: Boolean? = null
        gpsStatusListener?.observe(requireActivity()) { isGpsOn ->
            if (isGpsStatusChanged == null || isGpsStatusChanged != isGpsOn) {
                if (!isGpsOn) {
                    turnOnGps?.startGPS(resultLauncher)
                }
                isGpsStatusChanged = isGpsOn
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
            if (activityResult.resultCode == AppCompatActivity.RESULT_CANCELED) {
                showToast("GPS is required for location services")
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
        binding.progress.hide()
        lat = locationEvent.latitude ?: 0.0
        long = locationEvent.longitude ?: 0.0
    }

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!(requireActivity() as OnBoardingActivity).data.isNullOrEmpty() &&
                        (requireActivity() as OnBoardingActivity).data != "null"
                    ) {
                        requireActivity().finish()
                    }
                }
            })
    }
}
