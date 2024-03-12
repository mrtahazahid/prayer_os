package com.iw.android.prayerapp.services.gps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.data.response.NotificationPrayerTime
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.notificationService.Notification
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : NotificationListenerService() {
    @Inject
    lateinit var notifications: Notification
    lateinit var prefrence: DataPreference

    private var prayerList = arrayListOf<NotificationPrayerTime>()

    // ProcessLifecycleOwner provides lifecycle for the whole application process.
    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope


    override fun onCreate() {
        super.onCreate()
        prefrence = DataPreference(this)
        applicationScope.launch {
            val userLatLong = prefrence.getUserLatLong()
            val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                userLatLong?.latitude ?: 0.0,
                userLatLong?.longitude ?: 0.0
            )


            prayerList = arrayListOf(
                NotificationPrayerTime(
                    "Fajr",
                    convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())
                ),
                NotificationPrayerTime(
                    "Sunrise",
                    convertToFunTime(getPrayerTime.sunrise.toEpochMilliseconds())
                ),
                NotificationPrayerTime(
                    "Dhuhr",
                    convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())
                ),
                NotificationPrayerTime(
                    "Asr",
                    convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())
                ),
                NotificationPrayerTime(
                    "Maghrib",
                    convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())
                ),
                NotificationPrayerTime(
                    "Isha",
                    convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())
                ),
                NotificationPrayerTime("Midnight", "11:42PM"),
                NotificationPrayerTime("LastThird", "01:40AM")
            )
        }

        startPeriodicTask()

    }

    fun startPeriodicTask() {
        val inputString = "30 min"
        val extractedNumber = extractNumberFromString(inputString)
        Log.d("extractedNumber", "startPeriodicTask: $extractedNumber")
        applicationScope.launch {
            while (true) {
                // Your periodic task logic here
                checkAndTriggerNotification()
                checkDailyNamazTime()
                Log.d("snooze", prefrence.getSettingNotificationData().toString())
                // Delay for 10 seconds
                delay(30000)
            }
        }
    }

    override fun onNotificationRemoved(
        sbn: StatusBarNotification?,
        rankingMap: RankingMap?,
        reason: Int
    ) {
        Log.d("notification Removed", sbn.toString())
        if (sbn?.packageName == application.packageName) {
            if (reason == REASON_APP_CANCEL || reason == REASON_APP_CANCEL_ALL || reason == REASON_CLICK) {
                notifications.player?.stop()
            }
        }
    }


    private fun checkAndTriggerNotification() = applicationScope.launch {
        Log.d("service", "Called")
        // Add your logic here to check if it's time to show a notification
        val specifiedTimes = prefrence.getNotificationData()
        if (specifiedTimes.isNotEmpty()) {
            for ((index, specifiedTime) in specifiedTimes.withIndex()) {
                if (specifiedTime.namazTime != "") {
                    if (!specifiedTime.isReminderNotificationCall) {

                        if (specifiedTime.duaTime != "off") {
                            Log.d("namazTime", specifiedTime.namazTime)
                            val reminderTime =
                                convertTimeToMillis(specifiedTime.namazTime) - minutesToMillis(
                                    extractNumberFromString(specifiedTime.reminderTime)
                                )
                            val formattedTime = millisToTimeFormat(reminderTime)
                            Log.d("formattedTime", formattedTime)
                            if (isTimeMatch(
                                    formattedTime
                                )
                            ) {
                                notifications.notify(specifiedTime.namazName,specifiedTime.sound?:0,false,false)
                                specifiedTime.isReminderNotificationCall = true
                                prefrence.updateNotificationData(index, specifiedTime)
                                delay(1500)
                                break
                            }
                        } else {
                            if (!specifiedTime.isNotificationCall) {
                                if (isTimeMatch(specifiedTime.namazTime)) {
                                    notifications.notify(specifiedTime.namazName,specifiedTime.sound?:0,false,false)
                                    specifiedTime.isNotificationCall = true
                                    prefrence.updateNotificationData(index, specifiedTime)
                                    delay(1500)
                                    break
                                }
                            }
                        }
                    }
                } else {
                    continue
                }
            }
        }

    }


    fun convertTimeToMillis(timeString: String): Long {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = dateFormat.parse(timeString)
        return date?.time ?: 0
    }

    fun minutesToMillis(minutes: Int): Long {
        return minutes.toLong() * 60 * 1000
    }

    fun millisToTimeFormat(millis: Long): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun extractNumberFromString(input: String): Int {
        // Split the input string by spaces
        val parts = input.split(" ")

        // Find the first part that is a number
        val numberString = parts.find { it.toIntOrNull() != null }

        // Convert the found number string to an integer or return 0 if not found
        return numberString?.toInt() ?: 0
    }

    fun getCurrentTimeFormatted(): String {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val currentTime = Date()
        return dateFormat.format(currentTime)
    }

    private fun isTimeMatch(specifiedTime: String): Boolean {
        if (specifiedTime == "") {
            return false
        }
        return (convertTimeToMillis(getCurrentTimeIn12HourFormat()).compareTo(
            convertTimeToMillis(
                specifiedTime
            )
        ) == 0)

    }

    fun getCurrentTimeIn12HourFormat(): String {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        return LocalTime.now().format(formatter)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createNotificationChannel()
        val pendingFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE or Notification.NOTIFICATION_FLAGS
        } else {
            Notification.NOTIFICATION_FLAGS
        }

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, pendingFlag)
        val notification: android.app.Notification = NotificationCompat.Builder(this, "113")
            .setContentTitle("Prayer App is Running")
            .setContentText("Click to open")
            .setSmallIcon(R.mipmap.app_icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    fun subtractDurationFromTime(time: String, duration: String): String {
        val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Parse the time string
        val parsedTime = formatter.parse("05:35 AM")

        // Parse the duration string and convert it to seconds
        val durationInSeconds = duration.trim().split(" ")[0].toInt() * 60

        // Calculate the difference in milliseconds
        val differenceInMillis = parsedTime.time - durationInSeconds * 1000

        // Convert the difference back to a LocalTime object
        val localTimeDifference = LocalTime.ofSecondOfDay(differenceInMillis / 1000)

        // Format the result back to "hh:mm a" format
        val resultFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        return localTimeDifference.format(resultFormatter)
    }


    override fun onBind(intent: Intent): IBinder? = null
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "113",
                "Prayer is running in background",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private suspend fun checkDailyNamazTime() {
        val notificationDetail = prefrence.getCurrentNamazNotificationData()
        Log.d("notificationDetail",notificationDetail.toString())
        val currentTime = millisToTimeFormat(System.currentTimeMillis())
        Log.d("currentTime",currentTime)
        for (time in prayerList) {
            if (currentTime == time.currentNamazTime && !time.isCalled && notificationDetail != null) {
                if(!notificationDetail.isOff){
                    notifications.notify(time.currentNamazName, notificationDetail.sound ?: 0,notificationDetail.isVibrate,notificationDetail.isSilent)
                    time.isCalled = true
                    break
                }
            } else {
                continue
            }
        }
    }

}