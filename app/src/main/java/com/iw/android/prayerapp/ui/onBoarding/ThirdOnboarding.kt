package com.iw.android.prayerapp.ui.onBoarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.FragmentThirdOnboardingBinding
import com.iw.android.prayerapp.extension.openAppSettings
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.TurnOnGps
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import com.iw.android.prayerapp.utils.LocationPermissionTextProvider
import com.iw.android.prayerapp.utils.decodeImage.decodeSampledBitmap
import com.iw.android.prayerapp.utils.getCurrentLocationSuspend
import com.iw.android.prayerapp.utils.showPermissionDialog
import kotlinx.coroutines.launch

class ThirdOnboarding : BaseFragment(R.layout.fragment_third_onboarding) {

    private var _binding: FragmentThirdOnboardingBinding? = null
    private val binding get() = _binding!!

    val viewModel: OnBoardingViewModel by viewModels()
    private var isOpenSetting = false

    private var gpsStatusListener: GpsStatusListener? = null
    private var turnOnGps: TurnOnGps? = null
    private lateinit var fusedClient: FusedLocationProviderClient

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                enableGPSLocation()
            } else {
                showPermissionDialog(
                    permissionTextProvider = LocationPermissionTextProvider(),
                    isPermanentlyDeclined = permissions.entries.any { (permission, _) ->
                        (permission == Manifest.permission.ACCESS_FINE_LOCATION ||
                                permission == Manifest.permission.ACCESS_COARSE_LOCATION) &&
                                !shouldShowRequestPermissionRationale(permission)
                    },
                    onDismiss = {},
                    onOkClick = {
                        openAppSettings()
                        isOpenSetting = true
                    },
                    onGoToAppSettingsClick = ::openAppSettings,
                    context = requireContext()
                )
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
        val bitmap = decodeSampledBitmap(requireContext(), R.drawable.third_screen, 1024, 1024)

        binding.centerImage.setImageBitmap(bitmap)
        checkPermissions()
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
        setOnBackPressedListener()
        gpsStatusListener = GpsStatusListener(requireContext())
        turnOnGps = TurnOnGps(requireContext())

    }

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val binding = _binding ?: return@launch
                try {
                    binding.progress.show()
                    val location = fusedClient.getCurrentLocationSuspend()

                    location?.let {
                        val lat = it.latitude
                        val lng = it.longitude
                        Log.d("location", "lat $lat lng $lng")
                        // Navigate with lat/lng
                        viewModel.saveUserLatLong(
                            UserLatLong(
                                lat,
                                lng
                            )
                        )
                        binding.progress.hide()
                        val args = bundleOf("lat" to lat, "long" to lng)
                        findNavController().navigate(
                            R.id.action_thirdOnboarding_to_fourthOnboarding,
                            args
                        )
                    }?: run {
                        binding.progress.hide()
                        showToast("Could not fetch location")
                    }
                } catch (e: Exception) {
                    binding.progress.hide()
                    showToast("Location permission not granted")
                    showPermissionDialog(
                        permissionTextProvider = LocationPermissionTextProvider(),
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION),
                        onDismiss = {},
                        onOkClick = {
                            openAppSettings()
                        },
                        onGoToAppSettingsClick = ::openAppSettings,
                        context = requireContext()
                    )
                }
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
        _binding?.btnEnableNotification?.setOnClickListener(null)
        _binding?.notNow?.setOnClickListener(null)
        _binding?.skip?.setOnClickListener(null)
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        if (isOpenSetting) {
            checkPermissions()
            isOpenSetting = false
        }

    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissions.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            enableGPSLocation()
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
