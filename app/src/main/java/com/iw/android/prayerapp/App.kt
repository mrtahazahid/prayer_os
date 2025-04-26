package com.iw.android.prayerapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application(){
    var isAppInForeground = false

    override fun onCreate() {
        super.onCreate()

        val activityLifecycleCallbacks = object : ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                isAppInForeground = true
            }

            override fun onActivityPaused(activity: Activity) {
                isAppInForeground = false
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivityDestroyed(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}
        }

        registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
    }
}