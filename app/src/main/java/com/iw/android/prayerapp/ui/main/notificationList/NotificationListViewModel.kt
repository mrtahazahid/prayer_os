package com.iw.android.prayerapp.ui.main.notificationList

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    var notificationList = arrayListOf<NotificationData>()
    private var madhab: Madhab? = null
    private var method: CalculationParameters? = null
    var selectedPrayerDate = Date()
    var userLatLong: UserLatLong? = null

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            getMethod()
            if (getPrayerJurisprudence().isNullOrEmpty()) {
                madhab = if (getPrayerJurisprudence().toInt() == 1) {
                    Madhab.HANAFI
                } else {
                    Madhab.SHAFI
                }
            }

            val currentNamaz = getTimeDifferenceToNextPrayer()
            when (currentNamaz.currentNamazName) {
                "Fajr" -> {
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Fajr",
                            namazTime = getFormattedDate(0)[0],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Sunrise",
                            namazTime = getFormattedDate(0)[1],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Dhuhr",
                            namazTime = getFormattedDate(0)[2],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Asr",
                            namazTime = getFormattedDate(0)[3],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Maghrib",
                            namazTime = getFormattedDate(0)[4],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Isha",
                            namazTime = getFormattedDate(0)[5],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                }

                "Dhuhr" -> {
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Dhuhr",
                            namazTime = getFormattedDate(0)[2],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Asr",
                            namazTime = getFormattedDate(0)[3],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Maghrib",
                            namazTime = getFormattedDate(0)[4],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Isha",
                            namazTime = getFormattedDate(0)[5],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                }

                "Asr" -> {

                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Asr",
                            namazTime = getFormattedDate(0)[3],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Maghrib",
                            namazTime = getFormattedDate(0)[4],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Isha",
                            namazTime = getFormattedDate(0)[5],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                }

                "Maghrib" -> {
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Maghrib",
                            namazTime = getFormattedDate(0)[4],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Isha",
                            namazTime = getFormattedDate(0)[5],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                }

                "Isha" -> {

                    notificationList.add(
                        NotificationData(
                            isForNotificationList = true,
                            namazName = "Isha",
                            namazTime = getFormattedDate(0)[5],
                            createdDate = getFormattedCreatedDate(0)
                        )
                    )
                }

                else -> {

                }

            }


            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Fajr",
                    namazTime = getFormattedDate(1)[0], createdDate = getFormattedCreatedDate(1)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Sunrise",
                    namazTime = getFormattedDate(1)[1], createdDate = getFormattedCreatedDate(1)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Dhuhr",
                    namazTime = getFormattedDate(1)[2], createdDate = getFormattedCreatedDate(1)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Asr",
                    namazTime = getFormattedDate(1)[3], createdDate = getFormattedCreatedDate(1)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Maghrib",
                    namazTime = getFormattedDate(1)[4], createdDate = getFormattedCreatedDate(1)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Isha",
                    namazTime = getFormattedDate(1)[5], createdDate = getFormattedCreatedDate(1)
                )
            )

            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Fajr",
                    namazTime = getFormattedDate(2)[0], createdDate = getFormattedCreatedDate(2)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Sunrise",
                    namazTime = getFormattedDate(2)[1], createdDate = getFormattedCreatedDate(2)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Dhuhr",
                    namazTime = getFormattedDate(2)[2], createdDate = getFormattedCreatedDate(2)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Asr",
                    namazTime = getFormattedDate(2)[3], createdDate = getFormattedCreatedDate(2)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Maghrib",
                    namazTime = getFormattedDate(2)[4], createdDate = getFormattedCreatedDate(2)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Isha",
                    namazTime = getFormattedDate(2)[5], createdDate = getFormattedCreatedDate(2)
                )
            )

            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Fajr",
                    namazTime = getFormattedDate(3)[0], createdDate = getFormattedCreatedDate(3)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Sunrise",
                    namazTime = getFormattedDate(3)[1], createdDate = getFormattedCreatedDate(3)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Dhuhr",
                    namazTime = getFormattedDate(3)[2], createdDate = getFormattedCreatedDate(3)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Asr",
                    namazTime = getFormattedDate(3)[3], createdDate = getFormattedCreatedDate(3)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Maghrib",
                    namazTime = getFormattedDate(3)[4], createdDate = getFormattedCreatedDate(3)
                )
            )
            notificationList.add(
                NotificationData(
                    isForNotificationList = true,
                    namazName = "Isha",
                    namazTime = getFormattedDate(3)[5], createdDate = getFormattedCreatedDate(3)
                )
            )
            for (data in getAllNotificationData()) {
                if (data != null) {
                    notificationList.add(data)
                }
            }
        }
    }


    private suspend fun getMethod() {
        if (getPrayerJurisprudence().isNullOrEmpty()) {
            madhab = if (getPrayerJurisprudence().toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }

        if (!getPrayerMethod().isNullOrEmpty()) {
            method = when (getPrayerMethod().toInt()) {
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
        } else {
            method = CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
        }
    }

    fun getFormattedDate(offset: Int): ArrayList<String> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val targetDate: Date = calendar.time
        selectedPrayerDate = targetDate
        return GetAdhanDetails.getPrayTime(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0,
            method ?: CalculationMethod.NORTH_AMERICA.parameters.copy(
                madhab = madhab ?: Madhab.HANAFI
            ),
            selectedPrayerDate
        )
    }

    fun getFormattedCreatedDate(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val targetDate: Date = calendar.time

        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(targetDate)
    }

    private fun getTimeDifferenceToNextPrayer(): PrayerTime {
        val getPrayerTime =
            GetAdhanDetails.getPrayTimeInLong(
                userLatLong?.latitude ?: 0.0,
                userLatLong?.longitude ?: 0.0,
                method!!
            )
        val prayerTimeList = listOf(
            PrayerTime(
                "Dhuhr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Fajr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Sunrise",
                convertTimeToMillis(convertToFunTime(getPrayerTime.sunrise.toEpochMilliseconds()))
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
            ),
            PrayerTime(
                "Mid night",
                convertTimeToMillis(convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Last Third",
                convertTimeToMillis(convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()))
            )
        )

        val currentTimeMillis = convertTimeToMillis(convertToFunTime(System.currentTimeMillis()))
        val currentTimeMillis1159 = convertTimeToMillis("11:59 PM")
        val currentTimeMillis12 = convertTimeToMillis("12:00 AM")
        Log.d("mid", convertTimeToMillis("11:42 PM").toString())
        Log.d("last", convertTimeToMillis("01:40 AM").toString())


        var nextPrayerTimeIndex = 0
        var currentPrayerTimeIndex = 0
        var previousPrayerTimeIndex = 0
        for ((index, _) in prayerTimeList.withIndex()) {
            if (prayerTimeList[index].currentNamazTime > currentTimeMillis) {
                if (prayerTimeList[index].currentNamazName == "Fajr") {
                    previousPrayerTimeIndex = 4
                    currentPrayerTimeIndex = index
                    nextPrayerTimeIndex = index + 1
                } else if (prayerTimeList[index].currentNamazName == "Isha") {
                    previousPrayerTimeIndex = index - 1
                    currentPrayerTimeIndex = index
                    nextPrayerTimeIndex = 0
                } else {
                    previousPrayerTimeIndex = index - 1
                    currentPrayerTimeIndex = index
                    nextPrayerTimeIndex = index + 1
                }
                break
            } else {
                continue
            }


        }

        val timeDifferenceMillis =
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime - currentTimeMillis

        val totalDifferenceMillis =
            when (prayerTimeList[currentPrayerTimeIndex].currentNamazName) {
                "Fajr" -> {
                    prayerTimeList[currentPrayerTimeIndex].currentNamazTime - prayerTimeList[previousPrayerTimeIndex].currentNamazTime
                }

                "Isha" -> {
                    prayerTimeList[nextPrayerTimeIndex].currentNamazTime - prayerTimeList[currentPrayerTimeIndex].currentNamazTime
                }

                else -> {
                    prayerTimeList[nextPrayerTimeIndex].currentNamazTime - prayerTimeList[previousPrayerTimeIndex].currentNamazTime
                }
            }

        return if (currentTimeMillis >= prayerTimeList[4].currentNamazTime && currentTimeMillis <= currentTimeMillis1159) {
            return PrayerTime(
                "No Namaz Left",
                0,
                0,
                0
            )
        } else if (currentTimeMillis >= currentTimeMillis12 && currentTimeMillis < prayerTimeList[0].currentNamazTime) {
            val timeDifferenceMillis1 = prayerTimeList[0].currentNamazTime - currentTimeMillis
            val totalDifferenceMillis1 = prayerTimeList[0].currentNamazTime - currentTimeMillis12

            return PrayerTime(
                "Fajr",
                prayerTimeList[0].currentNamazTime,
                timeDifferenceMillis1,
                totalDifferenceMillis1
            )
        } else {
            PrayerTime(
                prayerTimeList[currentPrayerTimeIndex].currentNamazName,
                prayerTimeList[currentPrayerTimeIndex].currentNamazTime,
                timeDifferenceMillis,
                totalDifferenceMillis
            )
        }
    }

    fun convertTimeToMillis(timeString: String): Long {

        // Set the date to a fixed value (e.g., today's date) to avoid unexpected behavior
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
}