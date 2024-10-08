package com.iw.android.prayerapp

import android.app.Application
import com.iw.android.prayerapp.utils.GetAdhanSound
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start the service

    }
}

