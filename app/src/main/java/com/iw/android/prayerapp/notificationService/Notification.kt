package com.iw.android.prayerapp.notificationService


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import kotlin.random.Random


@Singleton
class Notification @Inject constructor(@ApplicationContext private val context: Context) {

    var player: MediaPlayer? = null

    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope

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
        isForSilent: Boolean
    ) {
        Log.d("Notify","called")
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
                setVibrate(longArrayOf(0, 100, 200, 300,400,500))
                priority = NotificationCompat.PRIORITY_HIGH
                setContentIntent(pendingIntent)
            }
        } else if (isForSilent) {
            NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.mipmap.app_icon)
                setContentTitle(currentNamazTitle)
                setContentText(description)
                setAutoCancel(true)
                setSilent(true)
                priority = NotificationCompat.PRIORITY_HIGH

                setContentIntent(pendingIntent)
            }
        } else {
            NotificationCompat.Builder(context, channelId).apply {
                setSmallIcon(R.mipmap.app_icon)
                setContentTitle(currentNamazTitle)
                setContentText(description)
                setAutoCancel(true)
                priority = NotificationCompat.PRIORITY_HIGH
                setSilent(false)
                setContentIntent(pendingIntent)

                try {
                    applicationScope.launch {
                        val uri =  Uri.parse("android.resource://" + context.packageName + "/" + sound)
                        player = MediaPlayer.create(context, uri)
                        player?.isLooping = false // This will play sound in repeatable mode.
                        player?.start()
//                        delay(10000)
//                        player?.release()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }





        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                NOTIFICATION_CHANNEL_NAME,
                NOTIFICATION_IMPORTANCE
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt() * Random.nextInt(
            NOTIFICATION_ID_MULTIPLIER
        )
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private val notificationIntent = Intent(context, MainActivity::class.java)
    fun startForegroundService() {
        notificationIntent.putExtra("fromNotification", true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(notificationIntent)
        } else {
            context.startService(notificationIntent)
        }
    }

    fun stopPrayer(){
        player?.release()
    }
}

