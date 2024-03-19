package com.iw.android.prayerapp.services.gps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.notificationService.Notification
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
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
                NotificationPrayerTime("Midnight", "11:42 PM"),
                NotificationPrayerTime("LastThird", "01:40 AM")
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
                checkIqamaTime()
                jummahTimeCheck()
                // Delay for 10 seconds
                delay(60000)
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
            Log.d("checkAndTriggerNotification", "checkAndTriggerNotification: ${specifiedTimes.toString()}")
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
                                notifications.notify(
                                    specifiedTime.namazName,
                                    specifiedTime.sound ?: 0,
                                    false,
                                    false
                                )
                                prefrence.updateNotificationData(index, specifiedTime)
                                delay(1500)
                                break
                            }
                        } else {
                            if (!specifiedTime.isNotificationCall) {
                                if (isTimeMatch(specifiedTime.namazTime)) {
                                    notifications.notify(
                                        specifiedTime.namazName,
                                        specifiedTime.sound ?: 0,
                                        false,
                                        false
                                    )
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
        val currentTime = millisToTimeFormat(System.currentTimeMillis())
        for (time in prayerList) {
            if (currentTime == time.currentNamazTime && !time.isCalled && notificationDetail != null) {
                notifications.notify(
                    time.currentNamazName,
                    notificationDetail.sound ?: 0,
                    notificationDetail.isVibrate,
                    notificationDetail.isSilent
                )
                break
            } else {
                continue
            }
        }
    }

    private fun checkIqamaTime() = applicationScope.launch {


        Log.d("time", "${prefrence.getIqamaAsrDetail()?.namazTime.toString()}")
        Log.d("time", "${prefrence.getIqamaAsrDetail()?.iqamaTime?.iqamaMinutesTime.toString()}  ")
        when (prefrence.getIqamaFajrDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaFajrDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    prefrence.getIqamaFajrDetail()?.namazName ?: ""
                )
            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaFajrDetail()?.iqamaTime?.iqamaTime ?: "",
                    prefrence.getIqamaFajrDetail()?.namazName ?: ""
                )
            }
        }

        when (prefrence.getIqamaDhuhrDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaDhuhrDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    prefrence.getIqamaDhuhrDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaDhuhrDetail()?.iqamaTime?.iqamaTime ?: "",
                    prefrence.getIqamaDhuhrDetail()?.namazName ?: ""
                )
            }
        }

        when (prefrence.getIqamaAsrDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaAsrDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    prefrence.getIqamaAsrDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaAsrDetail()?.iqamaTime?.iqamaTime ?: "",
                    prefrence.getIqamaAsrDetail()?.namazName ?: ""
                )
            }
        }

        when (prefrence.getIqamaMaghribDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaMaghribDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    prefrence.getIqamaMaghribDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaMaghribDetail()?.iqamaTime?.iqamaTime ?: "",
                    prefrence.getIqamaMaghribDetail()?.namazName ?: ""
                )
            }
        }

        when (prefrence.getIqamaIshaDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaIshaDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    prefrence.getIqamaIshaDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    prefrence.getIqamaIshaDetail()?.iqamaTime?.iqamaTime ?: "",
                    prefrence.getIqamaIshaDetail()?.namazName ?: ""
                )
            }
        }
    }


    private fun checkIqamaTimeByMinutes(minutes: String) = applicationScope.launch {
//        val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
//            prefrence.getUserLatLong()?.latitude ?: 0.0,
//            prefrence.getUserLatLong()?.longitude ?: 0.0
//        )
//        val prayerTimeList = listOf(
//            IqamaTimeList(
//                "Fajr",
//                convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()),
//                convertTimeToMillis(convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()))
//            ),
//            IqamaTimeList(
//                "Dhuhr",
//                convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()),
//                convertTimeToMillis(convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()))
//            ),
//            IqamaTimeList(
//                "Asr",
//                convertToFunTime(getPrayerTime.asr.toEpochMilliseconds()),
//                convertTimeToMillis(convertToFunTime(getPrayerTime.asr.toEpochMilliseconds()))
//            ),
//            IqamaTimeList(
//                "Maghrib",
//                convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds()),
//                convertTimeToMillis(convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds()))
//            ),
//            IqamaTimeList(
//                "Isha",
//                convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()),
//                convertTimeToMillis(convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()))
//            )
//        )
//
//        val currentTimeMillis = convertTimeToMillis(convertToFunTime(Instant.now().toEpochMilli()))
//
//        var currentPrayerTimeIndex = 0
//        for ((index, _) in prayerTimeList.withIndex()) {
//            if (prayerTimeList[index].nameTimeLong <= currentTimeMillis) {
//                currentPrayerTimeIndex = index
//                break
//            } else {
//                continue
//            }
//        }
//
//        val currentNamazName = prayerTimeList[currentPrayerTimeIndex].namazName
//        val currentNamazTime = prayerTimeList[currentPrayerTimeIndex].nameTimeString
//
//        Log.d("iqama",currentNamazTime)
//        Log.d("iqama",extractMinutes(minutes).toString())
//        Log.d("iqama",addMinutesToTime(currentNamazTime, extractMinutes(minutes)))
//        if (isTimeMatch(addMinutesToTime(currentNamazTime, extractMinutes(minutes)))) {
//            notifications.notify(
//                "$currentNamazName Iqama Time",
//                prefrence.getIqamaNotificationSetting()?.reminderSound ?: 0,
//                false,
//                false
//            )
//        }


    }

    private fun checkIqamaTimeByTime(time: String, namazName: String) =
        applicationScope.launch {
            if (isTimeMatch(time)) {
                notifications.notify(
                    "$namazName Iqama Time",
                    prefrence.getIqamaNotificationSetting()?.reminderSound ?: 0,
                    false,
                    false
                )
            }
        }

    private fun jummahTimeCheck() = applicationScope.launch {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.FRIDAY) {
            if (!prefrence.getJummuahSetting()?.reminderTimeFormatted.isNullOrEmpty()) {
                if (isTimeMatch(prefrence.getJummuahSetting()!!.reminderTimeFormatted)) {
                    notifications.notify(
                        "Khutba reminder",
                        prefrence.getIqamaNotificationSetting()?.reminderSound ?: 0,
                        false,
                        false
                    )
                }

            }

        }
    }

    fun addMinutesToTime(currentTime: String, minutesToAdd: Int): String {
        // Parse the current time string
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val parsedTime = LocalTime.parse(currentTime, formatter)

        // Add minutes to the parsed time
        val resultTime = parsedTime.plusMinutes(minutesToAdd.toLong())

        // Format the result time back to "hh:mm a" format
        return resultTime.format(formatter)
    }

    fun subtractMinutesFromTime(currentTime: String, minutesToSubtract: Int): String {
        // Parse the current time string
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val parsedTime = LocalTime.parse(currentTime, formatter)

        // Subtract minutes from the parsed time
        val resultTime = parsedTime.minusMinutes(minutesToSubtract.toLong())

        // Format the result time back to "hh:mm a" format
        return resultTime.format(formatter)
    }

    fun extractMinutes(input: String): Int {
        val regex = """(\d+)\s+min""".toRegex()
        val matchResult = regex.find(input)
        return matchResult?.groupValues?.get(1)?.toInt() ?: 0
    }
}