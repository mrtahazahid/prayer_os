package com.iw.android.prayerapp.services.gps

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.NotificationPrayerTime
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.notificationService.Notification
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.method.getMethod
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class NotificationService : Service() {
    @Inject
    lateinit var notifications: Notification
   private  lateinit var preference: DataPreference

    private var prayerList = arrayListOf<NotificationPrayerTime>()
    private var method: CalculationParameters? = null
    private var madhab: Madhab? = null
    private var loopStarted = false
    private val applicationScope = ProcessLifecycleOwner.get().lifecycleScope

    override fun onCreate() {
        super.onCreate()
        loopStarted = false
        preference = DataPreference(this)
    }


    override fun onDestroy() {
        loopStarted = false
        super.onDestroy()
    }
    private var notificationJob: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(this, "113")
            .setContentTitle("Pray Watch is Running")
            .setContentText("Click to open")
            .setSmallIcon(R.mipmap.app_icon)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()

        startForeground(1, notification)

        if (intent?.action == "ACTION_STOP_ADHAN") {
            notifications.stopPrayer()
            notifications.removeNotification()
        } else {

            if (notificationJob == null || notificationJob?.isActive == false) {
                Log.d("notificationJob", "called $notificationJob")
                notificationJob = applicationScope.launch {
                    startPeriodicTask()
                    while (true) {
                        Log.d("while", "called")
                        checkAndTriggerNotification()
                        checkIqamaTime()
                        jummahTimeCheck()
                        delay(60000)
                    }
                }
            }
        }
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

    private suspend fun startPeriodicTask() {
        val selectedJurisprudence = preference.prayerJurisprudence.first()
        val selectedMethod = preference.prayerMethod.first()
        method = getMethod(selectedJurisprudence, selectedMethod)

        if (!selectedJurisprudence.isNullOrEmpty()) {
            madhab = if (selectedJurisprudence.toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }

        val userLatLong = preference.getUserLatLong()
        val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0, method!!
        )

        prayerList = arrayListOf(
            NotificationPrayerTime(
                "FJR",
                convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())
            ),
            NotificationPrayerTime(
                "DHR",
                convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())
            ),
            NotificationPrayerTime(
                "ASR",
                convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())
            ),
            NotificationPrayerTime(
                "MGB",
                convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())
            ),
            NotificationPrayerTime(
                "ISH",
                convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())
            )
        )
    }

    private suspend fun checkAndTriggerNotification() {
        preference.getFajrDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getSunriseDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getDuhrDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getAsrDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getMagribDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getIshaDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getMidnightDetail()?.let {
            checkNamazNotification(it)
        }
        preference.getLastThirdDetail()?.let {
            checkNamazNotification(it)
        }

    }

    private fun checkNamazNotification(specifiedTime: NotificationData) {

        if (specifiedTime.namazTime != "") {
            if (isTimeMatch(specifiedTime.namazTime)) {
                val sound =
                    if (specifiedTime.notificationSound?.isForAdhan == true) specifiedTime.notificationSound?.soundAdhan
                        ?: R.raw.adhan_abdul_basit_short else specifiedTime.notificationSound?.soundTone
                        ?: R.raw.adhan_abdul_basit_short

                if (specifiedTime.notificationSound?.isOff != true) {
                    notifications.notify(
                        specifiedTime.namazName,
                        "${specifiedTime.namazName} Prayer Time",
                        sound,
                        specifiedTime.notificationSound?.isVibrate ?: false,
                        specifiedTime.notificationSound?.isSilent ?: false,
                        specifiedTime.notificationSound?.isOff ?: false
                    )
                    sendNotification(applicationContext)
                }
            }

            if (specifiedTime.duaType != "off") {
                if (isTimeMatch(specifiedTime.duaTime)) {
                    val sound =
                        if (specifiedTime.notificationSound?.isForAdhan == true) specifiedTime.notificationSound?.soundAdhan
                            ?: R.raw.adhan_abdul_basit_short else specifiedTime.notificationSound?.soundTone
                            ?: R.raw.adhan_abdul_basit_short
                    notifications.notify(
                        specifiedTime.namazName,
                        "${specifiedTime.namazName} Dhua Time",
                        sound,
                        specifiedTime.reminderSound?.isVibrate ?: false,
                        specifiedTime.reminderSound?.isSilent ?: false,
                        specifiedTime.reminderSound?.isOff ?: false
                    )
                    sendNotification(applicationContext)

                }
            }

            if (specifiedTime.reminderTime != "") {
                if (isTimeMatch(specifiedTime.reminderTime)) {
                    Log.d("specifiedTime.reminderSound", specifiedTime.reminderSound.toString())
                    if (specifiedTime.reminderSound == null) {
                        notifications.notify(
                            specifiedTime.namazName,
                            "${specifiedTime.namazName} in ${specifiedTime.reminderTimeMinutes}",
                            0,
                            isForVibrate = true,
                            isForSilent = false,
                            isOff = false
                        )
                        sendNotification(applicationContext)
                    } else {
                        if (specifiedTime.reminderSound?.isOff != true) {
                            val sound =
                                if (specifiedTime.reminderSound?.isForAdhan == true) specifiedTime.reminderSound?.soundAdhan
                                    ?: R.raw.adhan_abdul_basit_short else specifiedTime.reminderSound?.soundTone
                                    ?: R.raw.adhan_abdul_basit_short
                            notifications.notify(
                                specifiedTime.namazName,
                                "${specifiedTime.namazName} in ${specifiedTime.reminderTimeMinutes}",
                                sound,
                                specifiedTime.reminderSound?.isVibrate ?: false,
                                specifiedTime.reminderSound?.isSilent ?: false,
                                specifiedTime.reminderSound?.isOff ?: false
                            )
                            sendNotification(applicationContext)

                        }
                    }

                }
            }

            if (specifiedTime.secondReminderTimeMinutes != "off") {
                if (isTimeMatch(specifiedTime.secondReminderTime)) {
                    val sound =
                        if (specifiedTime.reminderSound?.isForAdhan == true) specifiedTime.reminderSound?.soundAdhan
                            ?: R.raw.adhan_abdul_basit_short else specifiedTime.reminderSound?.soundTone
                            ?: R.raw.adhan_abdul_basit_short
                    notifications.notify(
                        specifiedTime.namazName,
                        "Second ${specifiedTime.namazName} namaz reminder",
                        sound,
                        specifiedTime.reminderSound?.isVibrate ?: false,
                        specifiedTime.reminderSound?.isSilent ?: false,
                        specifiedTime.reminderSound?.isOff ?: false
                    )
                    sendNotification(applicationContext)
                }
            }
        }
    }


    private fun convertTimeToMillis(timeString: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val date = dateFormat.parse(timeString)
            date?.time ?: 0
        } catch (e: ParseException) {
            Log.d("ParseException", e.message.toString())
            // Invalid format, return 0 to avoid crash
            0
        }
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


    private suspend fun checkIqamaTime() {
        when (preference.getIqamaFajrDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaFajrDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    preference.getIqamaFajrDetail()?.namazName ?: ""
                )
            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaFajrDetail()?.iqamaTime?.iqamaTime ?: "",
                    preference.getIqamaFajrDetail()?.namazName ?: ""
                )
            }
        }

        when (preference.getIqamaDhuhrDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaDhuhrDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    preference.getIqamaDhuhrDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaDhuhrDetail()?.iqamaTime?.iqamaTime ?: "",
                    preference.getIqamaDhuhrDetail()?.namazName ?: ""
                )
            }
        }

        when (preference.getIqamaAsrDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaAsrDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    preference.getIqamaAsrDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaAsrDetail()?.iqamaTime?.iqamaTime ?: "",
                    preference.getIqamaAsrDetail()?.namazName ?: ""
                )
            }
        }

        when (preference.getIqamaMaghribDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaMaghribDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    preference.getIqamaMaghribDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaMaghribDetail()?.iqamaTime?.iqamaTime ?: "",
                    preference.getIqamaMaghribDetail()?.namazName ?: ""
                )
            }
        }

        when (preference.getIqamaIshaDetail()?.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {}
            DuaTypeEnum.MINUTES.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaIshaDetail()?.iqamaTime?.iqamaMinutesTime ?: "",
                    preference.getIqamaIshaDetail()?.namazName ?: ""
                )

            }

            DuaTypeEnum.TIME.getValue() -> {
                checkIqamaTimeByTime(
                    preference.getIqamaIshaDetail()?.iqamaTime?.iqamaTime ?: "",
                    preference.getIqamaIshaDetail()?.namazName ?: ""
                )
            }
        }
    }

    private suspend fun checkIqamaTimeByTime(time: String, namazName: String) =
        if (isTimeMatch(time)) {
            Log.d("checkIqamaTimeByTime", "called")
            notifications.notify(
                namazName, "Iqama time",
                preference.getIqamaNotificationSetting()?.reminderSound ?: 0,
                isForVibrate = false,
                isForSilent = false,
                isOff = false
            )
            sendNotification(applicationContext)
        } else {
            null
        }

    private suspend fun jummahTimeCheck() {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayOfWeek == Calendar.FRIDAY) {
            if (!preference.getJummuahSetting()?.reminderTimeFormatted.isNullOrEmpty()) {
                if (isTimeMatch(preference.getJummuahSetting()!!.reminderTimeFormatted)) {
                    notifications.notify(
                        "Jummah", "Khutba reminder",
                        preference.getIqamaNotificationSetting()?.reminderSound ?: 0,
                        isForVibrate = false,
                        isForSilent = false,
                        isOff = false
                    )
                    sendNotification(applicationContext)
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