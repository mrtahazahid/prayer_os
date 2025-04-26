package com.iw.android.prayerapp.services.gps

import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.iw.android.prayerapp.notificationService.Notification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var notifications: Notification
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        notifications.stopPrayer()
        sendNotification(applicationContext)
    }

    private fun sendNotification(context: Context) {
        val intent = Intent("com.iw.android.prayerapp.NOTIFICATION")
        intent.putExtra("show_image", false)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}