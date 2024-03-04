package com.iw.android.prayerapp.ui.main.timeFragment


import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel(repository) {
    var userLatLong: UserLatLong? = null
    var getSavedPrayerJurisprudence = ""
    var selectedPrayerDate = Date()
    var prayTimeArray = arrayListOf<PrayTime>()

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            getSavedPrayerJurisprudence = getPrayerJurisprudence()
            getPrayList()
        }
    }

    fun getPrayList() = viewModelScope.launch {

        val madhab = if (getSavedPrayerJurisprudence.toInt() == 0) {
            Madhab.SHAFI
        } else {
            Madhab.HANAFI
        }

        val getPrayerTime = GetAdhanDetails.getPrayTime(
            userLatLong?.latitude ?: 0.0,
            userLatLong?.longitude ?: 0.0,
            madhab,
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
                R.drawable.ic_mike,
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
                R.drawable.ic_mike,
                "Midnight",
                "11:42PM",
                formatDateWithCurrentTime(selectedPrayerDate),
                getMidNightDetail() ?: NotificationData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Last Third",
                "1:40AM",
                formatDateWithCurrentTime(selectedPrayerDate),
                getLastNightDetail() ?: NotificationData()
            )
        )
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


}