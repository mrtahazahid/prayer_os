package com.iw.android.prayerapp.services.gps

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority

class TurnOnGps(private val context: Context) {

    fun startGPS(resultLauncher: ActivityResultLauncher<IntentSenderRequest>){
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000)
            .setMinUpdateDistanceMeters(5f)
            .build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val task = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())

        task.addOnFailureListener {
            if(it is ResolvableApiException){
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(it.resolution).build()
                    resultLauncher.launch(intentSenderRequest)
                }catch (e: IntentSender.SendIntentException){
                    e.printStackTrace()
                }
            }
        }
    }
}