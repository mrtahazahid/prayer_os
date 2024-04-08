package com.iw.android.prayerapp.ui.main.notificationList

import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    var notificationList = arrayListOf<NotificationData>()
    private var madhab: Madhab? = null
    private var method: CalculationParameters? = null
    var selectedPrayerDate = Date()
    val userLatLong: UserLatLong? = null

    init {
        viewModelScope.launch {
            getMethod()
            if (getPrayerJurisprudence().isNullOrEmpty()) {
                madhab = if (getPrayerJurisprudence().toInt() == 1) {
                    Madhab.HANAFI
                } else {
                    Madhab.SHAFI
                }
            }
notificationList.add(NotificationData(namazName = "Fajr", namazTime = getFormattedDate(0)[0]))
notificationList.add(NotificationData(namazName = "Sunrise", namazTime = getFormattedDate(0)[1]))
notificationList.add(NotificationData(namazName = "Dhuhr", namazTime = getFormattedDate(0)[2]))
notificationList.add(NotificationData(namazName = "Asr", namazTime = getFormattedDate(0)[3]))
notificationList.add(NotificationData(namazName = "Maghrib", namazTime = getFormattedDate(0)[4]))
notificationList.add(NotificationData(namazName = "Isha", namazTime = getFormattedDate(0)[5]))

            notificationList.add(NotificationData(namazName = "Fajr", namazTime = getFormattedDate(1)[0]))
            notificationList.add(NotificationData(namazName = "Sunrise", namazTime = getFormattedDate(1)[1]))
            notificationList.add(NotificationData(namazName = "Dhuhr", namazTime = getFormattedDate(1)[2]))
            notificationList.add(NotificationData(namazName = "Asr", namazTime = getFormattedDate(1)[3]))
            notificationList.add(NotificationData(namazName = "Maghrib", namazTime = getFormattedDate(1)[4]))
            notificationList.add(NotificationData(namazName = "Isha", namazTime = getFormattedDate(1)[5]))

            notificationList.add(NotificationData(namazName = "Fajr", namazTime = getFormattedDate(2)[0]))
            notificationList.add(NotificationData(namazName = "Sunrise", namazTime = getFormattedDate(2)[1]))
            notificationList.add(NotificationData(namazName = "Dhuhr", namazTime = getFormattedDate(2)[2]))
            notificationList.add(NotificationData(namazName = "Asr", namazTime = getFormattedDate(2)[3]))
            notificationList.add(NotificationData(namazName = "Maghrib", namazTime = getFormattedDate(2)[4]))
            notificationList.add(NotificationData(namazName = "Isha", namazTime = getFormattedDate(2)[5]))

            notificationList.add(NotificationData(namazName = "Fajr", namazTime = getFormattedDate(3)[0]))
            notificationList.add(NotificationData(namazName = "Sunrise", namazTime = getFormattedDate(3)[1]))
            notificationList.add(NotificationData(namazName = "Dhuhr", namazTime = getFormattedDate(3)[2]))
            notificationList.add(NotificationData(namazName = "Asr", namazTime = getFormattedDate(3)[3]))
            notificationList.add(NotificationData(namazName = "Maghrib", namazTime = getFormattedDate(3)[4]))
            notificationList.add(NotificationData(namazName = "Isha", namazTime = getFormattedDate(3)[5]))
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
        }
    }

    fun getFormattedDate(offset: Int):ArrayList<String>{
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
}