package com.iw.android.prayerapp.ui.activities.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.ActivityMainBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.LocationEvent
import com.iw.android.prayerapp.services.gps.LocationService
import com.iw.android.prayerapp.services.gps.NotificationListenerService
import com.iw.android.prayerapp.services.gps.NotificationService
import com.iw.android.prayerapp.services.gps.TurnOnGps
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!


    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph


    private var gpsStatusListener: GpsStatusListener? = null

    val viewModel: OnBoardingViewModel by viewModels()
    private var turnOnGps: TurnOnGps? = null

    private var service: Intent? = null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {

        }

    private val pushNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            showPermissionAlertDialog()
        } else {
            startForegroundService()
        }
    }
    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }
                }

                it.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
            }
        }


    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setStatusBarWithBlackIcon(R.color.bg_color)
        initialize()
        setOnClickListener()
    }


    @SuppressLint("InlinedApi")
    override fun initialize() {

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        val inflater = navHostFragment.navController.navInflater
        navGraph = inflater.inflate(R.navigation.nav_graph_dashboard)
        navHostFragment.navController.graph = navGraph
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(destinationChangedListener)

        service = Intent(this, LocationService::class.java)
        startNotificationListenerService()
        gpsStatusListener = GpsStatusListener(this)
        turnOnGps = TurnOnGps(this)
        startForegroundService()
        if (!checkPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                showPermissionAlertDialog()
            } else {
                pushNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }

    override fun setOnClickListener() {
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.prayer_screen -> navController.navigate(R.id.prayerFragment)
                R.id.time_screen -> navController.navigate(R.id.timeFragment)
                R.id.qibla_screen -> navController.navigate(R.id.qiblaFragment)
                R.id.setting_screen -> navController.navigate(R.id.settingFragment)
                R.id.more_screen -> navController.navigate(R.id.moreFragment)
            }
            true
        }
    }


    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
                enableGPSLocation()
            } else {
                lifecycleScope.launch {
                    if (viewModel.repository.preferences.automaticLocation.first()) {
                        startService(
                            service
                        )
                    } else {
                        stopService(service)

                    }
                }

                enableGPSLocation()
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        )
        return permission == PackageManager.PERMISSION_GRANTED
    }

    private fun enableGPSLocation() {

        var isGpsStatusChanged: Boolean? = null
        gpsStatusListener?.observe(this) { isGpsOn ->
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
            if (activityResult.resultCode == RESULT_OK) {

            } else if (activityResult.resultCode == RESULT_CANCELED) {

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

        Log.d("CheckViewModel", "receiveLocationEvent: lat => ${locationEvent.latitude}")
        Log.d("CheckViewModel", "receiveLocationEvent: lon => ${locationEvent.longitude}")
    }

    fun hideBottomSheet() {
        binding.bottomNavigationView.visibility = View.GONE
    }

    fun showBottomSheet() {
        binding.bottomNavigationView.visibility = View.VISIBLE
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        stopService(service)
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.prayerFragment, R.id.qiblaFragment, R.id.timeFragment, R.id.moreFragment, R.id.settingFragment -> {
                    binding.bottomNavigationView.show()
                }
                R.id.iqamaFragment->{
                    binding.bottomNavigationView.gone()
                }
                else -> {
                    binding.bottomNavigationView.gone()
                }

            }
        }


    // Request notification permission
    fun requestNotificationPermission(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)
    }


    fun startForegroundService() {
        val notificationIntent = Intent(this, NotificationService::class.java)
        startForegroundService(notificationIntent)
    }

    private fun showPermissionAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Permission Required")
        alertDialogBuilder.setMessage("Notification permission is necessary to receive updates.")
        alertDialogBuilder.setPositiveButton("Open Settings") { _, _ ->
            requestNotificationPermission(this)
        }
        alertDialogBuilder.setNegativeButton("Cancel") { _, _ ->
            // Handle cancellation, if needed
        }
        alertDialogBuilder.setCancelable(false)

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun startNotificationListenerService() {
        val componentName = ComponentName(this, NotificationListenerService::class.java)
        val intent = Intent()
        intent.component = componentName

        if (!isNotificationServiceEnabled()) {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        } else {
            startService(intent)
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
        val cn = ComponentName(this, NotificationListenerService::class.java)
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        )
        return flat != null && flat.contains(cn.flattenToString())
    }

}