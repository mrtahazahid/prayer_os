package com.iw.android.prayerapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel : ViewModel() {

    val getLatitude = MutableLiveData<Double?>()
    val getLongitude = MutableLiveData<Double?>()

    fun putLiveLatitude(lat: Double?){
        try {
            if (lat == null) {
                getLatitude.value = 0.0
            }
            else{
                getLatitude.value = lat
            }
        }
        catch (e:Exception){
            Log.d("LOG=>putLiveLatitude", "updateLiveData: $e")
        }
    }

    fun putLiveLongitude(lon: Double?){
        try {
            if (lon == null) {
                getLongitude.value = 0.0
            }
            else{
                getLongitude.value = lon
            }
        }
        catch (e:Exception){
            Log.d("LOG=>putLiveLongitude", "updateLiveData: $e")
        }
    }


}