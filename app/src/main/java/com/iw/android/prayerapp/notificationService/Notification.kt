package com.iw.android.prayerapp.notificationService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class Notification @Inject constructor(@ApplicationContext private val context: Context) {


    companion object {
        private const val channelId = "110"
        private const val NOTIFICATION_ID_MULTIPLIER = 1000
        private const val NOTIFICATION_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT
        private const val NOTIFICATION_CHANNEL_NAME = "Channel Name"

        @RequiresApi(Build.VERSION_CODES.N)
        private const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }

    fun notify(user: String, title: String, message: String, app: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("user", user)
            putExtra("app", app)
            putExtra("notificationDeleted", true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Get the Uri of the sound from the raw resource folder


        }

        val pendingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or NOTIFICATION_FLAGS
        } else {
            NOTIFICATION_FLAGS
        }



        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingFlag)

        val actionButton = NotificationCompat.Action.Builder(
            R.mipmap.app_icon,
            "Stop Reminder",
            pendingIntent
        ).build()
      //  val customSoundUri =Uri.parse("android.resource://${context.packageName}/${R.raw.adhan_abdul_basit}" )
      //  RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_ALARM,customSoundUri)

        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.mipmap.app_icon)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(true)
            addAction(actionButton)
            priority = NotificationCompat.PRIORITY_HIGH
            setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.adhan_abdul_basit}"))
            setContentIntent(pendingIntent)
        }

//        val mMediaPlayer = MediaPlayer.create(context, R.raw.adhan_abdul_basit);
//        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//        mMediaPlayer.setLooping(true);
//        mMediaPlayer.start();

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
}