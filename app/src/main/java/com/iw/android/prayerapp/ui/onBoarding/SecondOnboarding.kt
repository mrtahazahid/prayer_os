package com.iw.android.prayerapp.ui.onBoarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentSecondOnboardingBinding
import com.iw.android.prayerapp.extension.NotificationPermissionTextProvider
import com.iw.android.prayerapp.extension.showPermissionDialog
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity

class SecondOnboarding : BaseFragment(R.layout.fragment_second_onboarding) {

    private var _binding: FragmentSecondOnboardingBinding? = null
    private val binding get() = _binding!!

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                findNavController().navigate(R.id.action_secondOnboarding_to_thirdOnboarding)
            } else {
                showPermissionDialog(
                    permissionTextProvider = NotificationPermissionTextProvider(),
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        Manifest.permission.POST_NOTIFICATIONS
                    ),
                    onDismiss = {},
                    onOkClick = {
                        openNotificationSettings()


                    },
                    onGoToAppSettingsClick = ::openNotificationSettings, context = requireContext()
                )
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondOnboardingBinding.inflate(inflater, container, false)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if(!checkNotificationPermission()){
                    requestNotificationPermission()
                }else{
                    findNavController().navigate(R.id.action_secondOnboarding_to_thirdOnboarding)
                }
            } else {
                findNavController().navigate(R.id.action_secondOnboarding_to_thirdOnboarding)
            }
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_secondOnboarding_to_thirdOnboarding)

        }

        binding.skip.setOnClickListener {
           requireActivity().startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()

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
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            } else {
                showPermissionDialog(
                    permissionTextProvider = NotificationPermissionTextProvider(),
                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                        Manifest.permission.POST_NOTIFICATIONS
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            showPermissionDialog(
                permissionTextProvider = NotificationPermissionTextProvider(),
                isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                onDismiss = {},
                onOkClick = {
                    openNotificationSettings()
                },
                onGoToAppSettingsClick = ::openNotificationSettings, context = requireContext()
            )
        } else {
            // Request camera permission using the launcher
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.POST_NOTIFICATIONS
        )
        return permission == PackageManager.PERMISSION_GRANTED
    }


    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!(requireActivity() as OnBoardingActivity).data.isNullOrEmpty() && (requireActivity() as OnBoardingActivity).data != "null") {
                        requireActivity().finish()
                    }
                }
            })
    }
}