package com.iw.android.prayerapp.services.gps

import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.notificationService.Notification
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var notifications: Notification
    lateinit var prefrence: DataPreference

    // ProcessLifecycleOwner provides lifecycle for the whole application process.
    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope

    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null

//    private var notificationManager: NotificationManager? = null

    private var location:Location?=null

    override fun onCreate() {
        super.onCreate()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).setIntervalMillis(500).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }
        prefrence = DataPreference(this)
        startPeriodicTask()
//        notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val notificationChannel = NotificationChannel(CHANNEL_ID, "locations", NotificationManager.IMPORTANCE_HIGH)
//            notificationManager?.createNotificationChannel(notificationChannel)
//        }
    }

    @Suppress("MissingPermission")
    fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }

    }
    fun startPeriodicTask() {

        applicationScope.launch {
            while (true) {
                // Your periodic task logic here
                checkAndTriggerNotification()

                // Delay for 10 seconds
                delay(10_000)
            }
        }
    }


    private fun checkAndTriggerNotification() {
        // Add your logic here to check if it's time to show a notification

        val specifiedTimes = listOf("05:35 AM", "12:44 PM", "05:32 PM", "06:30 PM", "07:12 PM")

        for (specifiedTime in specifiedTimes) {
            if (isTimeMatch(specifiedTime)) {
           notifications.notify("Isha")
                break
            }
        }
    }

    fun convertTimeToMillis(timeString: String): Long {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = dateFormat.parse(timeString)
        return date?.time ?: 0
    }
    fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }
    private fun isTimeMatch(specifiedTime: String): Boolean {
        return convertTimeToMillis(getCurrentTimeIn12HourFormat()).compareTo(convertTimeToMillis(specifiedTime)) == 0
    }


    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }

    fun getCurrentTimeIn12HourFormat(): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return LocalTime.now().format(formatter)
    }
    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        Log.d("CheckABC", "onNewLocation: ")
        EventBus.getDefault().post(LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude
        ))

//        startForeground(NOTIFICATION_ID,getNotification())
    }

//    fun getNotification():Notification{
//        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle("Location Updates")
//            .setContentText(
//                "Latitude--> ${location?.latitude}\nLongitude --> ${location?.longitude}"
//            )
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setOngoing(true)
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            notification.setChannelId(CHANNEL_ID)
//        }
//        return notification.build()
//    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createLocationRequest()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
    }
}