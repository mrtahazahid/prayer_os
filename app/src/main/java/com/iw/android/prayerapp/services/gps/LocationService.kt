package com.iw.android.prayerapp.services.gps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus

@AndroidEntryPoint
class LocationService : Service() {


    private lateinit var prefrence: DataPreference



    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null


    private var location:Location?=null

    override fun onCreate() {
        super.onCreate()
        prefrence = DataPreference(this)
    }

    @Suppress("MissingPermission")
    private  fun createLocationRequest(){
        try {
            fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest!!,locationCallback!!,null
            )
        }catch (e:Exception){
            e.printStackTrace()
        }

    }

    private fun removeLocationUpdates(){
        locationCallback?.let {
            fusedLocationProviderClient?.removeLocationUpdates(it)
        }
        stopForeground(true)
        stopSelf()
    }


    private fun onNewLocation(locationResult: LocationResult) {
        location = locationResult.lastLocation
        Log.d("CheckABC", "onNewLocation:${location?.latitude} ")
        EventBus.getDefault().post(LocationEvent(
            latitude = location?.latitude,
            longitude = location?.longitude
        ))
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 60000).setIntervalMillis(60000).build()
        locationCallback = object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability) {
                super.onLocationAvailability(p0)
            }

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult)
            }
        }
        createLocationRequest()

        createNotificationChannel()
        val notification: android.app.Notification = NotificationCompat.Builder(this, "114")
            .setContentTitle("Pray Watch is running for location")
            .setSmallIcon(R.mipmap.app_icon)
            .build()
        startForeground(2, notification)

        return START_STICKY
    }


    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "114",
            "Prayer Location Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        removeLocationUpdates()
        stopSelf()
    }


}