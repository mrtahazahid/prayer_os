package com.iw.android.prayerapp.ui.main.timeFragment

import android.util.Log
import androidx.lifecycle.viewModelScope
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
import com.iw.android.prayerapp.utils.dateFormat.convertAndGetCurrentTimeMillis
import com.iw.android.prayerapp.utils.dateFormat.convertTimeToMillis
import com.iw.android.prayerapp.utils.dateFormat.formatDateWithCurrentTime
import com.iw.android.prayerapp.utils.method.getMadhab
import com.iw.android.prayerapp.utils.method.getMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {
    var userLatLong: UserLatLong? = null
    var selectedPrayerDate = Date()
    var selectedJurisprudenceFromDB =""
    var prayTimeArray = arrayListOf<PrayTime>()
    private lateinit var method: CalculationParameters
    private lateinit  var madhab: Madhab

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
            getPrayList(userLatLong?.latitude?:0.0,userLatLong?.longitude?:0.0)
        }

    }

    fun getPrayerTime(lat: Double, long: Double): ArrayList<String> {
        return GetAdhanDetails.getPrayTime(
            lat,
            long,
            method,
            selectedPrayerDate
        )

    }


   suspend fun getPrayList(lat: Double, long: Double) {
        val getPrayerTime =getPrayerTime(lat,long)

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
                R.drawable.ic_mike,
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
                getPrayerTime[6],
                formatDateWithCurrentTime(selectedPrayerDate),
                getMidNightDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Last Third",
                getPrayerTime[7],
                formatDateWithCurrentTime(selectedPrayerDate),
                getLastNightDetail() ?: NotificationData()
            )
        )


        // Get the upcoming namaz using getTimeDifferenceToNextPrayer function
        val upcomingNamaz = getTimeDifferenceToNextPrayer(lat, long)

        // Iterate through the prayTimeArray and set isCurrentNamaz accordingly
        for (prayTime in prayTimeArray) {
            prayTime.isCurrentNamaz = prayTime.title == upcomingNamaz.currentNamazName
        }
    }


    private fun getTimeDifferenceToNextPrayer(lat: Double, long: Double): PrayerTime {

        val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
            lat,
            long,
            method
        )

        val getPrayerTime1 = getPrayerTime(lat,long)

        Log.d("time midnight", getPrayerTime1[6])
        Log.d("time midnight static", "1712083320000")

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
                1712083320000
            ), PrayerTime(
                "LastThird",
                1712004000000
            )
        )

        val currentTimeMillis = convertAndGetCurrentTimeMillis()


        // Iterate through the array to find the next prayer time
        var nextPrayerTimeIndex = 0
        var currentPrayerTimeIndex = 0
        var previousPrayerTimeIndex = 0
        for (i in prayerTimeList.indices) {
            if (prayerTimeList[i].currentNamazTime > currentTimeMillis) {
                when (prayerTimeList[i].currentNamazName) {
                    "Fajr" -> {
                        previousPrayerTimeIndex = prayerTimeList.size - 1
                        currentPrayerTimeIndex = i
                        nextPrayerTimeIndex = i + 1
                    }
                    "Isha" -> {
                        previousPrayerTimeIndex = i - 1
                        currentPrayerTimeIndex = i
                        nextPrayerTimeIndex = 0
                    }
                    else -> {
                        previousPrayerTimeIndex = i - 1
                        currentPrayerTimeIndex = i
                        nextPrayerTimeIndex = i + 1
                    }
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
}