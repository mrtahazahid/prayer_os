package com.iw.android.prayerapp.notificationService


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random



 var player: MediaPlayer? = null
@Singleton
class Notification @Inject constructor(@ApplicationContext private val context: Context) {

    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope
    companion object {
        private const val channelId = "110"
        private const val NOTIFICATION_ID_MULTIPLIER = 1000
        private const val NOTIFICATION_FLAGS = PendingIntent.FLAG_UPDATE_CURRENT
        private const val NOTIFICATION_CHANNEL_NAME = "Channel Name"

        @RequiresApi(Build.VERSION_CODES.N)
        private const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH
    }

    fun notify(currentNamazTitle: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            // Get the Uri of the sound from the raw resource folder


        }

        val pendingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or NOTIFICATION_FLAGS
        } else {
            NOTIFICATION_FLAGS
        }



        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingFlag)


      //  val customSoundUri =Uri.parse("android.resource://${context.packageName}/${R.raw.adhan_abdul_basit}" )
      //  RingtoneManager.setActualDefaultRingtoneUri(context,RingtoneManager.TYPE_ALARM,customSoundUri)

        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.mipmap.app_icon)
            setContentTitle(currentNamazTitle)
            setContentText("Namaz Time")
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_HIGH
            setSilent(true)
            setContentIntent(pendingIntent)
        }
        try {
            applicationScope.launch {
                val uri = Uri.parse("android.resource://" + context.packageName + "/" + R.raw.adhan_abdul_basit)
                player = MediaPlayer.create(context, uri)
                player?.isLooping = false // This will play sound in repeatable mode.
                player?.start()
                delay(20000)
                player?.stop()
            }


//     mBuilder.setSound(uri);
        } catch (e: Exception) {
            e.printStackTrace()
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

