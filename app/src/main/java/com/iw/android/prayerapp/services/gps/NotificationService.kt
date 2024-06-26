package com.iw.android.prayerapp.services.gps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.NotificationPrayerTime
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.notificationService.Notification
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {
    @Inject
    lateinit var notifications: Notification
    private lateinit var prefrence: DataPreference

    private var prayerList = arrayListOf<NotificationPrayerTime>()
    private var method: CalculationParameters? = null
    private var madhab: Madhab? = null

    // ProcessLifecycleOwner provides lifecycle for the whole application process.
    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope


    override fun onCreate() {
        super.onCreate()
        prefrence = DataPreference(this)
        startPeriodicTask()
    }

    private fun startPeriodicTask() {
        applicationScope.launch {
            getMethod()
            if (!prefrence.prayerJurisprudence.first().isNullOrEmpty()) {
                madhab = if (prefrence.prayerJurisprudence.first().toInt() == 1) {
                    Madhab.HANAFI
                } else {
                    Madhab.SHAFI
                }
            }

            val userLatLong = prefrence.getUserLatLong()
            var selectedPrayerDate = Date()
            val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                userLatLong?.latitude ?: 0.0,
                userLatLong?.longitude ?: 0.0, method!!
            )

            val getPrayerTime1 = GetAdhanDetails.getPrayTime(
                userLatLong?.latitude ?: 0.0,
                userLatLong?.longitude ?: 0.0,
                method ?: CalculationMethod.NORTH_AMERICA.parameters.copy(
                    madhab = madhab ?: Madhab.HANAFI
                ),
                selectedPrayerDate
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
                NotificationPrayerTime("Mid night", getPrayerTime1[6]),
                NotificationPrayerTime("Last third", getPrayerTime1[7])
            )

            while (true) {
                checkAndTriggerNotification()
                checkDailyNamazTime()
                checkIqamaTime()
                jummahTimeCheck()
                val intent = Intent(applicationContext, NotificationListenerService::class.java)
                startService(intent)
                if (prefrence.automaticLocation.first()) {
                    startService(
                        Intent(
                            applicationContext,
                            LocationService::class.java
                        )
                    )
                } else {
                    stopService(
                        Intent(
                            applicationContext,
                            LocationService::class.java
                        )
                    )
                }
                delay(60000)
            }
        }
    }

    private suspend fun checkAndTriggerNotification() {
        val specifiedTimes = prefrence.getNotificationData()
        if (specifiedTimes.isNotEmpty()) {
            for ((index, specifiedTime) in specifiedTimes.withIndex()) {
                if (specifiedTime.namazTime != "") {
                    if (specifiedTime.duaTime != "off") {
                        if (isTimeMatch(specifiedTime.duaTime)) {
                            notifications.notify(
                                specifiedTime.namazName, "${specifiedTime.namazName} Dhua Time",
                                specifiedTime.notificationSound?.sound ?: R.raw.adhan_abdul_basit,
                                specifiedTime.reminderSound?.isVibrate ?: false,
                                specifiedTime.reminderSound?.isSilent ?: false
                            )
                            sendNotification(applicationContext)
                            prefrence.removeNotificationData(index)
                            delay(1500)
                            break
                        }
                    }

                    if (isTimeMatch(specifiedTime.reminderTime)) {
                        if (specifiedTime.reminderSound?.isOff != true) {
                            notifications.notify(
                                specifiedTime.namazName,
                                "${specifiedTime.namazName} at ${specifiedTime.namazTime}",
                                specifiedTime.reminderSound?.sound ?: R.raw.adhan_abdul_basit,
                                specifiedTime.reminderSound?.isVibrate ?: false,
                                specifiedTime.reminderSound?.isSilent ?: false
                            )
                            sendNotification(applicationContext)
                            prefrence.removeNotificationData(index)
                            delay(1500)
                            break
                        }
                    }
                    if (specifiedTime.secondReminderTimeMinutes != "off") {
                        if (isTimeMatch(specifiedTime.secondReminderTime)) {
                            notifications.notify(
                                specifiedTime.namazName, "Second fajr namaz reminder",
                                specifiedTime.reminderSound?.sound ?: R.raw.adhan_abdul_basit,
                                specifiedTime.reminderSound?.isVibrate ?: false,
                                specifiedTime.reminderSound?.isSilent ?: false
                            )
                            sendNotification(applicationContext)
                            prefrence.removeNotificationData(index)
                            delay(1500)
                            break
                        }
                    }

                } else {
                    continue
                }
            }
        }

    }


    private fun convertTimeToMillis(timeString: String): Long {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = dateFormat.parse(timeString)
        return date?.time ?: 0
    }

    private fun millisToTimeFormat(millis: Long): String {
        val dateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val date = Date(millis)
        return dateFormat.format(date)
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

    private fun getCurrentTimeIn12HourFormat(): String {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
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
            .setContentTitle("Prayer App is Running in background")
            .setContentText("Click to open")
            .setSmallIcon(R.mipmap.app_icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? = null
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            "113",
            "Prayer is running in background",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
    }

    private suspend fun checkDailyNamazTime() {

        val currentTime = millisToTimeFormat(System.currentTimeMillis())

        for (time in prayerList) {
            val notificationDetail: CurrentNamazNotificationData? = when (time.currentNamazName) {
                "Fajr" -> {
                    if (prefrence.getFajrCurrentNamazNotificationData() != null) {
                        prefrence.getFajrCurrentNamazNotificationData()!!
                    } else {
                        CurrentNamazNotificationData(
                            "Fajr",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                "Dhuhr" -> {
                    if (prefrence.getDhuhrCurrentNamazNotificationData() != null) {
                        prefrence.getDhuhrCurrentNamazNotificationData()!!
                    } else {
                        CurrentNamazNotificationData(
                            "Dhuhr",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                "Asr" -> {
                    if (prefrence.getAsrCurrentNamazNotificationData() != null) {
                        prefrence.getAsrCurrentNamazNotificationData()!!
                    } else {
                        CurrentNamazNotificationData(
                            "Asr",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                "Maghrib" -> {
                    if (prefrence.getMaghribCurrentNamazNotificationData() != null) {
                        prefrence.getMaghribCurrentNamazNotificationData()!!
                    } else {
                        CurrentNamazNotificationData(
                            "Maghrib",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                "Isha" -> {
                    if (prefrence.getIshaCurrentNamazNotificationData() != null) {
                        prefrence.getIshaCurrentNamazNotificationData()!!

                    } else {
                        CurrentNamazNotificationData(
                            "Isha",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                "Mid night" -> {
                    if (prefrence.getMidnightCurrentNamazNotificationData() != null) {
                        prefrence.getMidnightCurrentNamazNotificationData()!!

                    } else {
                        CurrentNamazNotificationData(
                            "Mid night",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                "Last third" -> {
                    if (prefrence.getLastNightCurrentNamazNotificationData() != null) {
                        prefrence.getLastNightCurrentNamazNotificationData()!!

                    } else {
                        CurrentNamazNotificationData(
                            "Last third",
                            "Adhan",
                            "Tones",
                            0,
                            isSoundSelected = false,
                            isForAdhan = true,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            sound = R.raw.adhan_abdul_basit
                        )
                    }
                }

                else -> {
                    CurrentNamazNotificationData(
                        "",
                        "Adhan",
                        "Tones",
                        0,
                        isSoundSelected = false,
                        isForAdhan = true,
                        isVibrate = false,
                        isSilent = false,
                        isOff = false,
                        sound = R.raw.adhan_abdul_basit
                    )
                }
            }
            val specifiedTimes = prefrence.getNotificationData()
            Log.d("notificationDetail",notificationDetail.toString())
            if (specifiedTimes.isNotEmpty()) {
                for ((index, specifiedTime) in specifiedTimes.withIndex()) {
Log.d("s",(currentTime == time.currentNamazTime).toString())
                        if (currentTime == time.currentNamazTime) {
                            if (notificationDetail?.isOff !=true) {
                                notifications.notify(
                                    time.currentNamazName, "Namaz Time",
                                    notificationDetail?.sound ?: R.raw.adhan_abdul_basit,
                                    notificationDetail?.isVibrate?:false,
                                    notificationDetail?.isSilent?:false
                                )
                                sendNotification(applicationContext)
                                prefrence.removeNotificationData(index)
                                delay(1500)
                                break

                            } else if (specifiedTime.namazTime != "") {
                                if (isTimeMatch(specifiedTime.namazTime)) {
                                    if (specifiedTime.notificationSound?.isOff != true) {
                                        notifications.notify(
                                            specifiedTime.namazName, "Namaz Time",
                                            specifiedTime.notificationSound?.sound
                                                ?: R.raw.adhan_abdul_basit,
                                            specifiedTime.notificationSound?.isVibrate ?: false,
                                            specifiedTime.notificationSound?.isSilent
                                                ?: false
                                        )
                                        sendNotification(applicationContext)
                                        prefrence.removeNotificationData(index)
                                        delay(1500)
                                        break
                                    }
                                }
                            }
                        } else {
                            continue
                        }

                }
            }

        }
    }


    private suspend fun checkIqamaTime() {
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

    private suspend fun checkIqamaTimeByTime(time: String, namazName: String) =

        if (isTimeMatch(time)) {
            notifications.notify(
                namazName, "Iqama time",
                prefrence.getIqamaNotificationSetting()?.reminderSound ?: 0,
                isForVibrate = false,
                isForSilent = false
            )
            sendNotification(applicationContext)
        } else {
            null
        }


    private suspend fun jummahTimeCheck() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.FRIDAY) {
            if (!prefrence.getJummuahSetting()?.reminderTimeFormatted.isNullOrEmpty()) {
                if (isTimeMatch(prefrence.getJummuahSetting()!!.reminderTimeFormatted)) {
                    notifications.notify(
                        "Jummah", "Khutba reminder",
                        prefrence.getIqamaNotificationSetting()?.reminderSound ?: 0,
                        isForVibrate = false,
                        isForSilent = false
                    )
                    sendNotification(applicationContext)
                }
            }
        }
    }

    private suspend fun getMethod() {
        if (prefrence.prayerJurisprudence.first().isNullOrEmpty()) {
            madhab = if (prefrence.prayerJurisprudence.first().toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }

        if (!prefrence.prayerMethod.first().isNullOrEmpty()) {
            method = when (prefrence.prayerMethod.first().toInt()) {
                0 -> {
                    CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                1 -> {
                    CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                2 -> {
                    CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                3 -> {
                    CalculationMethod.EGYPTIAN.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                4 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                5 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                6 -> {
                    CalculationMethod.UMM_AL_QURA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                8 -> {
                    CalculationMethod.UMM_AL_QURA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                9 -> {
                    CalculationMethod.DUBAI.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                10 -> {
                    CalculationMethod.KUWAIT.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                11 -> {
                    CalculationMethod.SINGAPORE.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                12 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                13 -> {
                    CalculationMethod.QATAR.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }


                14 -> {
                    CalculationMethod.KARACHI.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                else -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }
            }
        }
    }

    private fun sendNotification(context: Context) {
        val intent = Intent("com.iw.android.prayerapp.NOTIFICATION")
        intent.putExtra("show_image", true)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}