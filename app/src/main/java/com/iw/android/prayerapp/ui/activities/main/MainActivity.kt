package com.iw.android.prayerapp.ui.activities.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.activity.BaseActivity
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.ActivityMainBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.services.gps.GpsStatusListener
import com.iw.android.prayerapp.services.gps.NotificationListenerService
import com.iw.android.prayerapp.services.gps.NotificationService
import com.iw.android.prayerapp.services.gps.TurnOnGps
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import com.iw.android.prayerapp.utils.LocationPermissionTextProvider
import com.iw.android.prayerapp.utils.getCurrentLocationSuspend
import com.iw.android.prayerapp.utils.showPermissionDialog
import kotlinx.coroutines.launch


class MainActivity : BaseActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding


    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var fusedClient: FusedLocationProviderClient

    private var gpsStatusListener: GpsStatusListener? = null
    private var isOpenSetting= false

    val viewModel: OnBoardingViewModel by viewModels()
    private var turnOnGps: TurnOnGps? = null


    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ) {
                enableGPSLocation()
                lifecycleScope.launch {
                    val location = fusedClient.getCurrentLocationSuspend()
                    location?.let {
                        val lat = it.latitude
                        val lng = it.longitude
                        // Navigate with lat/lng
                        viewModel.saveUserLatLong(
                            UserLatLong(
                                lat,
                                lng
                            )
                        )
                    }
                }

            } else {
                showPermissionDialog(
                    permissionTextProvider = LocationPermissionTextProvider(),
                    isPermanentlyDeclined = permissions.entries.any {
                            (permission, _) ->
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
                    context = this
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            _binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding?.root)
            if (binding != null) {
                if (Build.VERSION.SDK_INT >= 35) {
                    WindowCompat.setDecorFitsSystemWindows(this.window, false)
                    ViewCompat.setOnApplyWindowInsetsListener(binding!!.mainView) { v: View, insets: WindowInsetsCompat ->
                        val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                        v.setPadding(0, systemBars.top, 0, 0)
                        insets
                    }
                }

                setStatusBarWithBlackIcon(R.color.black)
                initialize()
                setOnClickListener()
            } else {
                // binding is null, maybe show a toast or log an error
                showToast("Something went wrong while loading the screen.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("UI ERROR MAIN ACTIVITY","Unexpected error: ${e.localizedMessage}")
            finish() // Optional: close the activity to prevent half-loaded UI
        }
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
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        startNotificationListenerService()
        gpsStatusListener = GpsStatusListener(this)
        turnOnGps = TurnOnGps(this)
        startForegroundService()
        if (!checkNotificationPermission()) {
            showPermissionAlertDialog()
        }
        checkPermissions()
    }

    override fun setOnClickListener() {
        binding?.bottomNavigationView?.setOnItemSelectedListener {
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
        if (isOpenSetting){
            checkPermissions()
        }

    }
    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
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
        } else {
            enableGPSLocation()
            lifecycleScope.launch {
                val location = fusedClient.getCurrentLocationSuspend()
                location?.let {
                    val lat = it.latitude
                    val lng = it.longitude
                    // Navigate with lat/lng
                    viewModel.saveUserLatLong(
                        UserLatLong(
                            lat,
                            lng
                        )
                    )
                }  ?: showToast("Error while getting user location")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission(): Boolean {
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
            if (activityResult.resultCode == RESULT_CANCELED) {
                showToast("GPS is required for location services")
            }
        }

    fun hideBottomSheet() {
        if(_binding != null){
            _binding?.bottomNavigationView?.visibility = View.GONE
        }

    }

    fun showBottomSheet() {
        if(_binding != null) {
            _binding?.bottomNavigationView?.visibility = View.VISIBLE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        navController.removeOnDestinationChangedListener(destinationChangedListener)
    }

    private val destinationChangedListener =
        NavController.OnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.prayerFragment, R.id.qiblaFragment, R.id.timeFragment, R.id.moreFragment, R.id.settingFragment -> {
                    binding?.bottomNavigationView?.show()
                }
                R.id.iqamaFragment->{
                    binding?.bottomNavigationView?.gone()
                }
                else -> {
                    binding?.bottomNavigationView?.gone()
                }

            }
        }


    // Request notification permission
    private fun requestNotificationPermission(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        context.startActivity(intent)
    }


    private fun startForegroundService() {
        val notificationIntent = Intent(this, NotificationService::class.java)
        ContextCompat.startForegroundService(this,notificationIntent)
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

   private fun openAppSettings() {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        ).also(::startActivity)
    }


}