package com.iw.android.prayerapp.ui.main.prayerFragment

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.dateFormat.convertTimeToMillis
import com.iw.android.prayerapp.utils.method.getMadhab
import com.iw.android.prayerapp.utils.method.getMethod
import com.iw.android.prayerapp.utils.time.subtractMinutesFromTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    var userLatLong: UserLatLong? = null
    var selectedJurisprudenceFromDB = ""
    lateinit var method: CalculationParameters
    lateinit var madhab: Madhab

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            val selectedMethodFromDB = getPrayerMethod()
            selectedJurisprudenceFromDB = getPrayerJurisprudence()
            madhab = getMadhab(selectedJurisprudenceFromDB)
            method = getMethod(
                selectedMethod = selectedMethodFromDB,
                selectedJurisprudence = selectedJurisprudenceFromDB
            )

            Log.d("pray method",method.toString())
            saveDefaultNamaz()
        }
    }

    private suspend fun saveDefaultNamaz() {
        if (!repository.preferences.isFirstTime.first()) return

        val prayerTimes = GetAdhanDetails.getPrayTimeInLong(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0,
            method
        )

        val prayers = listOf(
            Triple("Fajr", prayerTimes.fajr.toEpochMilliseconds(), true),
            Triple("Dhuhr", prayerTimes.dhuhr.toEpochMilliseconds(), false),
            Triple("Asr", prayerTimes.asr.toEpochMilliseconds(), false),
            Triple("Maghrib", prayerTimes.maghrib.toEpochMilliseconds(), true),
            Triple("Isha", prayerTimes.isha.toEpochMilliseconds(), true),
            Triple("Sunrise", prayerTimes.sunrise.toEpochMilliseconds(), false),
            Triple("Midnight", prayerTimes.sunrise.toEpochMilliseconds(), false), // Maybe fix?
            Triple("Last Third", prayerTimes.sunrise.toEpochMilliseconds(), false) // Maybe fix?
        )

        prayers.forEach { (name, timeMillis, isAdhan) ->
            val adhanSound = if (isAdhan) R.raw.adhan_abdul_basit_short else null
            val isVibrate = !(name == "Sunrise" || name == "Midnight" || name == "Last Third")
            val isSilent = name == "Dhuhr" || name == "Asr"
            val notificationSound = CurrentNamazNotificationData(
                currentNamazName = name,
                soundName = "Adhan",
                soundToneName = "Tones",
                isSoundSelected = false,
                selectedSoundPosition = null,
                selectedSoundTonePosition = null,
                selectedSoundItemPosition = null,
                isForAdhan = true,
                isVibrate = false,
                isSilent = isSilent,
                isOff = false,
                soundAdhan = adhanSound,
                soundTone = null
            )

            val reminderSound = CurrentNamazNotificationData(
                currentNamazName = name,
                soundName = "Adhan",
                soundToneName = "Tones",
                isSoundSelected = false,
                selectedSoundPosition = null,
                selectedSoundTonePosition = null,
                selectedSoundItemPosition = null,
                isForAdhan = false,
                isVibrate = isVibrate,
                isSilent = false,
                isOff = false,
                soundAdhan = null,
                soundTone = null
            )

            val reminderTime = when (name) {
                "Fajr", "Dhuhr", "Asr", "Maghrib", "Isha" -> "20 min"
                else -> "0 min"
            }

            val reminderTimeValue = if (reminderTime == "0 min") {
                ""
            } else {
                subtractMinutesFromTime(
                    convertToFunTime(timeMillis),
                    20
                )
            }

            val data = NotificationData(
                namazName = name,
                namazTime = convertToFunTime(timeMillis),
                notificationSound = notificationSound,
                reminderSound = reminderSound,
                reminderTimeMinutes = reminderTime,
                reminderTime = reminderTimeValue,
                secondReminderTimeMinutes = "off",
                secondReminderTime = "",
                duaReminderMinutes = "off",
                duaTime = "",
                duaType = "off"
            )

            when (name) {
                "Fajr" -> saveFajrDetail(data)
                "Dhuhr" -> saveDuhrDetail(data)
                "Asr" -> saveAsrDetail(data)
                "Maghrib" -> saveMagribDetail(data)
                "Isha" -> saveIshaDetail(data)
                "Sunrise" -> saveSunriseDetail(data)
                "Midnight" -> saveMidNightDetail(data)
                "Last Third" -> saveLastNightDetail(data)
            }
        }

        repository.preferences.setBooleanData(DataPreference.IS_FIRST_TIME, false)
    }

    fun getTimeDifferenceToNextPrayer(): PrayerTime {

        val getPrayTimeInLong = GetAdhanDetails.getPrayTimeInLong(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0,
            method
        )
        val prayerTimes = listOf(
            PrayerTime("Fajr", getPrayTimeInLong.fajr.toEpochMilliseconds()),
            PrayerTime("Dhuhr", getPrayTimeInLong.dhuhr.toEpochMilliseconds()),
            PrayerTime("Asr", getPrayTimeInLong.asr.toEpochMilliseconds()),
            PrayerTime("Maghrib", getPrayTimeInLong.maghrib.toEpochMilliseconds()),
            PrayerTime("Isha", getPrayTimeInLong.isha.toEpochMilliseconds())
        )

        val currentTime = System.currentTimeMillis()

        // Find the next upcoming prayer
        val nextPrayer = prayerTimes.firstOrNull { it.currentNamazTime > currentTime }
        val currentTimeMillis1159 = convertTimeToMillis("11:59 PM")
        val currentTimeMillis12 = convertTimeToMillis("12:00 AM")

        return when {
            // If it's after Isha but before midnight
            currentTime >= prayerTimes.last().currentNamazTime && currentTime <= currentTimeMillis1159 -> {
                val ishaTime = prayerTimes.last().currentNamazTime
                val fajrTime =
                    prayerTimes.first().currentNamazTime + 24 * 60 * 60 * 1000 // next day

                val timeDifference = fajrTime - currentTime
                val totalTime = fajrTime - ishaTime

                PrayerTime(
                    "Fajr",
                    prayerTimes.first().currentNamazTime,
                    timeDifference,
                    totalTime
                )
            }

            // If it's after midnight but before Fajr
            currentTime in (currentTimeMillis12 until prayerTimes.first().currentNamazTime) -> {
                val timeDifference = prayerTimes.first().currentNamazTime - currentTime
                val totalTime = prayerTimes.first().currentNamazTime - currentTimeMillis12

                PrayerTime(
                    "Fajr",
                    prayerTimes.first().currentNamazTime,
                    timeDifference,
                    totalTime
                )
            }

            // Normal case: between two prayers
            nextPrayer != null -> {
                val nextIndex = prayerTimes.indexOf(nextPrayer)
                val prevIndex =
                    if (nextPrayer.currentNamazName == "Fajr") prayerTimes.lastIndex else nextIndex - 1

                val previousPrayerTime =
                    if (prevIndex >= 0) prayerTimes[prevIndex].currentNamazTime else 0L

                val timeDifference = nextPrayer.currentNamazTime - currentTime
                val totalTime = nextPrayer.currentNamazTime - previousPrayerTime

                nextPrayer.copy(
                    timeDifference = timeDifference,
                    totalTime = totalTime
                )
            }

            // Fallback: no next prayer found (shouldn't happen)
            else -> PrayerTime(
                "Fajr",
                prayerTimes.first().currentNamazTime,
                0L,
                0L
            )
        }
    }

    fun getPrayTimeInLong() = GetAdhanDetails.getPrayTimeInLong(
        userLatLong?.latitude ?: 0.0,
        userLatLong?.longitude ?: 0.0,
        method
    )
}