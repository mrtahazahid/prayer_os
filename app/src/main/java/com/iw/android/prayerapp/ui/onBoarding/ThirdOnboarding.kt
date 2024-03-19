package com.iw.android.prayerapp.ui.onBoarding

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import com.google.android.gms.location.LocationServices
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.FragmentThirdOnboardingBinding
import com.iw.android.prayerapp.extension.NotificationPermissionTextProvider
import com.iw.android.prayerapp.extension.showPermissionDialog
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.LocationEvent
import com.iw.android.prayerapp.services.gps.LocationService
import com.iw.android.prayerapp.services.gps.NotificationService
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

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true &&
                it[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                getLocation()
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        showPermissionDialog(
                            permissionTextProvider = NotificationPermissionTextProvider(),
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ),
                            onDismiss = {},
                            onOkClick = {
                                openNotificationSettings()


                            },
                            onGoToAppSettingsClick = ::openNotificationSettings,
                            context = requireContext()
                        )
                    }
                }
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

    }

    override fun setObserver() {
    }

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            if (isButtonForNext) {
                if(lat != 0.0 && long != 0.0){
                    val args = Bundle()
                    args.putString("lat", lat.toString())
                    args.putString("long", long.toString())
                    findNavController().navigate(R.id.action_thirdOnboarding_to_fourthOnboarding,args)

                }

            } else {
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

    }


    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    requireContext(),
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
                getLocation()
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"
            }
        }
    }

    fun openNotificationSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        startActivityForResult(intent, 120)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 120) {
            // Check if the user granted the camera permission after going to app settings
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                isButtonForNext = true
                binding.btnEnableNotification.text = "Next"
            } else {
                showPermissionDialog(
                    permissionTextProvider = NotificationPermissionTextProvider(),
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    onDismiss = {},
                    onOkClick = {
                        openNotificationSettings()
                    },
                    onGoToAppSettingsClick = ::openNotificationSettings, context = requireContext()
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    // Use the location data (latitude and longitude)
                    lat = location.latitude
                    long = location.longitude
                    // Now you can use latitude and longitude to perform location-related tasks
                } else {
                    // Handle the case where location is null (e.g., location services disabled)
                    showToast("Location not available")
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