package com.iw.android.prayerapp.services.gps

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.iw.android.prayerapp.notificationService.Notification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class NotificationListenerService : NotificationListenerService() {


    @Inject
    lateinit var notifications: Notification

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Return STICKY to prevent the automatic service termination
        return START_STICKY
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        Log.d("notif",sbn.toString())
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        Log.d("notif",sbn.toString())
        if (sbn?.packageName == "com.iw.android.prayerapp") {
            if (reason == REASON_APP_CANCEL || reason == REASON_APP_CANCEL_ALL) {
                notifications.stopPrayer()
            }
        }
    }

}