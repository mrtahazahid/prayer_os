package com.iw.android.prayerapp.data.repositories


import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.PrayerTimes
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.repo.BaseRepository
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.dateFormat.convertAndGetCurrentTimeMillis
import com.iw.android.prayerapp.utils.dateFormat.convertTimeToMillis
import com.iw.android.prayerapp.utils.dateFormat.formatDateWithCurrentTime
import com.iw.android.prayerapp.utils.method.getMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeRepository @Inject constructor(preferences: DataPreference) :
    BaseRepository(preferences) {


    private var selectedPrayerDate = Date()
    var lat = 0.0
    var lng = 0.0
    var selectedJurisprudence = ""
    private lateinit var method: CalculationParameters
    private var prayTimeArray = arrayListOf<PrayTime>()
    private var prayTimeWithDateArray = arrayListOf<PrayTime>()

    init {
        CoroutineScope(Dispatchers.Main).launch {
            getValuesFromDB()
            getPrayerTimeList()
        }
    }

    fun setPrayDate(data: Date) {
        CoroutineScope(Dispatchers.IO).launch {
            selectedPrayerDate = data
            getPrayListWithCustomLocation()
            getPrayerTimeWithDateList()
        }
    }

    fun setLatLng(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            lat = latitude
            lng = longitude
            getPrayListWithCustomLocation()
            getPrayerTimeWithDateList()
        }
    }

    fun getPrayDate() = selectedPrayerDate
    fun getTimeList() = prayTimeArray
    fun getTimeListWithCustomLocation() = prayTimeWithDateArray
    private suspend fun getPrayerTimeList() = withContext(Dispatchers.IO) {
        val prayList = getPrayList()
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Fajr",
                prayList[0],
                formatDateWithCurrentTime(selectedPrayerDate),
                getFajrDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_speaker_zzz,
                "Sunrise",
                prayList[1],
                formatDateWithCurrentTime(selectedPrayerDate),
                getSunriseDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Dhuhr",
                prayList[2],
                formatDateWithCurrentTime(selectedPrayerDate),
                getDuhrDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Asr",
                prayList[3],
                formatDateWithCurrentTime(selectedPrayerDate),
                getAsrDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Maghrib",
                prayList[4],
                formatDateWithCurrentTime(selectedPrayerDate),
                getMagribDetail() ?: NotificationData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Isha",
                prayList[5],
                formatDateWithCurrentTime(selectedPrayerDate),
                getIshaDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Midnight",
                prayList[6],
                formatDateWithCurrentTime(selectedPrayerDate),
                getMidnightDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Last Third",
                prayList[7],
                formatDateWithCurrentTime(selectedPrayerDate),
                getLastThirdDetail() ?: NotificationData()
            )
        )


        // Get the upcoming namaz using getTimeDifferenceToNextPrayer function
        val upcomingNamaz = getTimeDifferenceToNextPrayer()

        // Iterate through the prayTimeArray and set isCurrentNamaz accordingly
        for (prayTime in prayTimeArray) {
            prayTime.isCurrentNamaz = prayTime.title == upcomingNamaz.currentNamazName
        }
    }

    suspend fun getPrayerTimeWithDateList() = withContext(Dispatchers.IO) {
        val prayList = getPrayListWithCustomLocation()
        prayTimeWithDateArray.clear()
        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Fajr",
                prayList[0],
                formatDateWithCurrentTime(selectedPrayerDate),
                getFajrDetail() ?: NotificationData()
            )
        )

        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_speaker_zzz,
                "Sunrise",
                prayList[1],
                formatDateWithCurrentTime(selectedPrayerDate),
                getSunriseDetail() ?: NotificationData()
            )
        )
        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Dhuhr",
                prayList[2],
                formatDateWithCurrentTime(selectedPrayerDate),
                getDuhrDetail() ?: NotificationData()
            )
        )
        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Asr",
                prayList[3],
                formatDateWithCurrentTime(selectedPrayerDate),
                getAsrDetail() ?: NotificationData()
            )
        )
        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Maghrib",
                prayList[4],
                formatDateWithCurrentTime(selectedPrayerDate),
                getMagribDetail() ?: NotificationData()
            )
        )
        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Isha",
                prayList[5],
                formatDateWithCurrentTime(selectedPrayerDate),
                getIshaDetail() ?: NotificationData()
            )
        )

        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Midnight",
                prayList[6],
                formatDateWithCurrentTime(selectedPrayerDate),
                getMidnightDetail() ?: NotificationData()
            )
        )

        prayTimeWithDateArray.add(
            PrayTime(
                R.drawable.ic_notification_mute,
                "Last Third",
                prayList[7],
                formatDateWithCurrentTime(selectedPrayerDate),
                getLastThirdDetail() ?: NotificationData()
            )
        )


        // Get the upcoming namaz using getTimeDifferenceToNextPrayer function
        val upcomingNamaz = getTimeDifferenceToNextPrayer()

        // Iterate through the prayTimeArray and set isCurrentNamaz accordingly
        for (prayTime in prayTimeWithDateArray) {
            prayTime.isCurrentNamaz = prayTime.title == upcomingNamaz.currentNamazName
        }
    }

    private suspend fun getTimeDifferenceToNextPrayer(): PrayerTime {
        var timeDifferenceMillis: Long
        var totalDifferenceMillis: Long
        var currentPrayerTimeIndex: Int
        withContext(Dispatchers.IO) {
            val currentTimeMillis = convertAndGetCurrentTimeMillis()


            // Iterate through the array to find the next prayer time
            var nextPrayerTimeIndex = 0
            currentPrayerTimeIndex = 0
            var previousPrayerTimeIndex = 0
            for (i in getPrayerList().indices) {
                if (getPrayerList()[i].currentNamazTime > currentTimeMillis) {
                    when (getPrayerList()[i].currentNamazName) {
                        "Fajr" -> {
                            previousPrayerTimeIndex = getPrayerList().size - 1
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
            timeDifferenceMillis =
                getPrayerList()[currentPrayerTimeIndex].currentNamazTime - currentTimeMillis

            // Calculate the total time difference between the previous and up-coming prayer
            totalDifferenceMillis =
                if (getPrayerList()[currentPrayerTimeIndex].currentNamazName == "Fajr") {
                    getPrayerList()[previousPrayerTimeIndex].currentNamazTime - getPrayerList()[nextPrayerTimeIndex].currentNamazTime
                } else {
                    getPrayerList()[nextPrayerTimeIndex].currentNamazTime - getPrayerList()[previousPrayerTimeIndex].currentNamazTime
                }
            // Return the PrayerTime object with time differences

        }
        return PrayerTime(
            getPrayerList()[currentPrayerTimeIndex].currentNamazName,
            getPrayerList()[currentPrayerTimeIndex].currentNamazTime,
            timeDifferenceMillis,
            totalDifferenceMillis
        )
    }

     fun getPrayList(): List<String> {
        return GetAdhanDetails.getPrayTime(
            lat,
            lng,
            method,
            selectedPrayerDate
        )
    }

    private fun getPrayListWithCustomLocation(): List<String> {
        return GetAdhanDetails.getPrayTime(
            lat,
            lng,
            method,
            selectedPrayerDate
        )
    }

    private fun getPrayerList(): List<PrayerTime> {
        return listOf(
            PrayerTime(
                "Fajr",
                convertTimeToMillis(convertToFunTime(getPrayListTime().fajr.toEpochMilliseconds()))
            ), PrayerTime(
                "Sunrise",
                convertTimeToMillis(convertToFunTime(getPrayListTime().sunrise.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Dhuhr",
                convertTimeToMillis(convertToFunTime(getPrayListTime().dhuhr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Asr",
                convertTimeToMillis(convertToFunTime(getPrayListTime().asr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Maghrib",
                convertTimeToMillis(convertToFunTime(getPrayListTime().maghrib.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Isha",
                convertTimeToMillis(convertToFunTime(getPrayListTime().isha.toEpochMilliseconds()))
            ), PrayerTime(
                "Midnight",
                1712083320000
            ), PrayerTime(
                "LastThird",
                1712004000000
            )
        )
    }

    private fun getPrayListTime(): PrayerTimes {
        return GetAdhanDetails.getPrayTimeInLong(
            lat,
            lng,
            method
        )
    }

    private suspend fun getValuesFromDB() = withContext(Dispatchers.IO) {
        setLatLongFromDB()
        val selectedMethod = getPrayerMethod()
         selectedJurisprudence = getPrayerJurisprudence()
        method = getMethod(
            selectedMethod = selectedMethod,
            selectedJurisprudence = selectedJurisprudence
        )
    }

    suspend fun setLatLongFromDB() {
        lat = getUserLatLong()?.latitude ?: 0.0
        lng = getUserLatLong()?.longitude ?: 0.0
    }


}
