package com.iw.android.prayerapp.notificationService


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Notification @Inject constructor(@ApplicationContext private val context: Context) {

    var player: MediaPlayer? = null
    private var screenOffReceiver: BroadcastReceiver? = null
    private var isReceiverRegistered = false

    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope

    init {
        createNotificationChannel()
    }
        private  val NOTIFICATION_ID = 12165  // Fixed notification ID


    companion object {
        private const val channelId = "110"
        private const val NOTIFICATION_ID_MULTIPLIER = 1000
        const val NOTIFICATION_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT
        private const val NOTIFICATION_CHANNEL_NAME = "Channel Name"

        @RequiresApi(Build.VERSION_CODES.N)
        private const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }

    fun notify(
        currentNamazTitle: String,
        description: String,
        sound: Int,
        isForVibrate: Boolean,
        isForSilent: Boolean, isOff: Boolean
    ) {
        Log.d("Notify", "called")
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or NOTIFICATION_FLAGS
        } else {
            NOTIFICATION_FLAGS
        }

        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingFlag)

        val notificationBuilder = if (isForVibrate) {
            NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.mipmap.app_icon)
                setContentTitle(currentNamazTitle)
                setContentText(description)
                setAutoCancel(true)
                setVibrate(longArrayOf(0, 100, 200, 300, 400, 500))
                priority = NotificationCompat.PRIORITY_HIGH
                setContentIntent(pendingIntent)
            }
        } else if (isForSilent) {
            NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.mipmap.app_icon)
                setContentTitle(currentNamazTitle)
                setContentText(description)
                setAutoCancel(true)
                priority = NotificationCompat.PRIORITY_HIGH
                setContentIntent(pendingIntent)
            }
        } else if (!isOff) {
            Log.d("isOff", "$isOff")
            NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.mipmap.app_icon)
                setContentTitle(currentNamazTitle)
                setContentText(description)
                setAutoCancel(true)
                priority = NotificationCompat.PRIORITY_HIGH
                setContentIntent(pendingIntent)


                try {
                    applicationScope.launch {
                        val uri =
                            Uri.parse("android.resource://" + context.packageName + "/" + sound)
                        player = MediaPlayer.create(context, uri)
                        player?.isLooping = false // This will play sound in repeatable mode.
                        player?.start()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (!isReceiverRegistered) {
                    screenOffReceiver = object : BroadcastReceiver() {
                        override fun onReceive(context: Context?, intent: Intent?) {
                            Log.d("ACTION_SCREEN_OFF", "called")
                            if (player?.isPlaying == true) {
                                stopPrayer()
                                removeNotification()
                            }
                        }
                    }
                    val screenOffFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
                    context.registerReceiver(screenOffReceiver, screenOffFilter)
                    isReceiverRegistered = true
                }
            }
        } else {
            null
        }

        if (notificationBuilder != null) {

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Channel Name"
            val channelDescription = "Channel Description"
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = channelDescription
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 100, 200, 300, 400, 500)
            }

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun stopPrayer() {
        player?.release()
        if (isReceiverRegistered && screenOffReceiver != null) {
            try {
                context.unregisterReceiver(screenOffReceiver)
                isReceiverRegistered = false
            } catch (e: IllegalArgumentException) {
                Log.e("Notification", "Receiver not registered: ${e.message}")
            }
        }
        screenOffReceiver = null
        player = null
    }

    fun removeNotification(){
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_ID)
    }



}


