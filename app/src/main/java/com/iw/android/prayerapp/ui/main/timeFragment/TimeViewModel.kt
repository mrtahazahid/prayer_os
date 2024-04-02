package com.iw.android.prayerapp.ui.main.timeFragment


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {
    var userLatLong: UserLatLong? = null
    var getSavedPrayerJurisprudence = ""
    var getMethod = ""
    var selectedPrayerDate = Date()
    var prayTimeArray = arrayListOf<PrayTime>()
    private var method: CalculationParameters? = null
    private var madhab: Madhab? = null

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            getSavedPrayerJurisprudence = getPrayerJurisprudence()
            getMethod = getPrayerMethod()
            getMethod()
            getPrayList()

        }
    }

   suspend fun getPrayList()  {


        val getPrayerTime = GetAdhanDetails.getPrayTime(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0,
            method!!,
            selectedPrayerDate
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Fajr",
                getPrayerTime[0],
                formatDateWithCurrentTime(selectedPrayerDate),
                getFajrDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_speaker_zzz,
                "Sunrise",
                getPrayerTime[1],
                formatDateWithCurrentTime(selectedPrayerDate),
                getSunriseDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_gallery,
                "Dhuhr",
                getPrayerTime[2],
                formatDateWithCurrentTime(selectedPrayerDate),
                getDuhrDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Asr",
                getPrayerTime[3],
                formatDateWithCurrentTime(selectedPrayerDate),
                getAsrDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Maghrib",
                getPrayerTime[4],
                formatDateWithCurrentTime(selectedPrayerDate),
                getMagribDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Isha",
                getPrayerTime[5],
                formatDateWithCurrentTime(selectedPrayerDate),
                getIshaDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Midnight",
                "11:42PM",
                formatDateWithCurrentTime(selectedPrayerDate),
                getMidNightDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Last Third",
                "1:40AM",
                formatDateWithCurrentTime(selectedPrayerDate),
                getLastNightDetail() ?: NotificationData()
            )
        )
        // Get the upcoming namaz using getTimeDifferenceToNextPrayer function
        val upcomingNamaz = getTimeDifferenceToNextPrayer()

        // Iterate through the prayTimeArray and set isCurrentNamaz accordingly
        for (prayTime in prayTimeArray) {
            prayTime.isCurrentNamaz = prayTime.title == upcomingNamaz.currentNamazName
        }
    }

    private fun formatDateWithCurrentTime(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date

        // Get the current time components
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        // Set the current time to the date
        calendar.set(Calendar.HOUR_OF_DAY, currentHour)
        calendar.set(Calendar.MINUTE, currentMinute)

        // Format the date with time
        val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun getTimeDifferenceToNextPrayer(): PrayerTime {

            madhab = if (getSavedPrayerJurisprudence.toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        

        val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0,
            method!!
        )

        val prayerTimeList = listOf(
            PrayerTime(
                "Fajr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()))
            ), PrayerTime(
                "Sunrise",
                convertTimeToMillis(convertToFunTime(getPrayerTime.sunrise.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Dhuhr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Asr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.asr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Maghrib",
                convertTimeToMillis(convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Isha",
                convertTimeToMillis(convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()))
            ), PrayerTime(
                "Midnight",
                convertTimeToMillis("11:42 PM")
            ), PrayerTime(
                "LastThird",
                convertTimeToMillis("10:40 AM")
            )
        )

        val currentTimeMillis = convertAndGetCurrentTimeMillis()

        Log.d("fajr", getPrayerTime.fajr.toEpochMilliseconds().toString())
        Log.d("currentTimeMillis", currentTimeMillis.toString())

        // Iterate through the array to find the next prayer time
        var nextPrayerTimeIndex = 0
        var currentPrayerTimeIndex = 0
        var previousPrayerTimeIndex = 0
        for (i in prayerTimeList.indices) {
            if (prayerTimeList[i].currentNamazTime > currentTimeMillis) {
                if (prayerTimeList[i].currentNamazName == "Fajr") {
                    previousPrayerTimeIndex = prayerTimeList.size - 1
                    currentPrayerTimeIndex = i
                    nextPrayerTimeIndex = i + 1
                } else if (prayerTimeList[i].currentNamazName == "Isha") {
                    previousPrayerTimeIndex = i - 1
                    currentPrayerTimeIndex = i
                    nextPrayerTimeIndex = 0
                } else {
                    previousPrayerTimeIndex = i - 1
                    currentPrayerTimeIndex = i
                    nextPrayerTimeIndex = i + 1
                }
                break
            } else {
                continue
            }
        }

        // Calculate the time difference between the current time and the next prayer time
        val timeDifferenceMillis =
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime - currentTimeMillis


        // Calculate the total time difference between the previous and up-coming prayer
        val totalDifferenceMillis =
            if (prayerTimeList[currentPrayerTimeIndex].currentNamazName == "Fajr") {
                prayerTimeList[previousPrayerTimeIndex].currentNamazTime - prayerTimeList[nextPrayerTimeIndex].currentNamazTime
            } else {
                prayerTimeList[nextPrayerTimeIndex].currentNamazTime - prayerTimeList[previousPrayerTimeIndex].currentNamazTime
            }
        // Return the PrayerTime object with time differences
        return PrayerTime(
            prayerTimeList[currentPrayerTimeIndex].currentNamazName,
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime,
            timeDifferenceMillis,
            totalDifferenceMillis
        )
    }

    private fun convertTimeToMillis(timeString: String): Long {
        val currentDate = Date()
        try {
            // Parse the time string by combining it with the current date
            val combinedDateTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
                .parse(
                    "${
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(currentDate)
                    } $timeString"
                )

            return combinedDateTime?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

    private fun convertAndGetCurrentTimeMillis(): Long {
        return LocalDateTime.now()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    fun convertTimeToEpochMilliseconds(timeString: String): Long {
        // Define a formatter for the time pattern
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

        try {
            // Parse the time string to obtain a LocalTime object
            val localTime = LocalTime.parse(timeString, timeFormatter)

            // Get the current date and set the obtained LocalTime
            val dateTime = localTime.atDate(LocalDate.now())

            // Convert the LocalDateTime to epoch milliseconds
            return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

    private suspend fun getMethod()  {
        if (!getSavedPrayerJurisprudence.isNullOrEmpty()) {
            madhab = if (getSavedPrayerJurisprudence.toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }

        if (!getMethod.isNullOrEmpty()) {
            method = when (getMethod.toInt()) {
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
                    CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }
            }
        }
    }

}