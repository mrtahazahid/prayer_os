package com.iw.android.prayerapp

import android.app.Application
import android.content.Intent
import android.service.notification.NotificationListenerService
import com.iw.android.prayerapp.services.gps.NotificationService
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    override fun onCreate() {
        super.onCreate()
        // Start the service

    }
}

