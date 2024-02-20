package com.iw.android.prayerapp.ui.main.timeFragment


import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.data.DateComponents
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerDetailData
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(private val repository: MainRepository)  : BaseViewModel(repository) {
    var userLatLong: UserLatLong? = null
    var getSavedPrayerJurisprudence = ""
    var selectedPrayerDate= Date()
     var prayTimeArray = arrayListOf<PrayTime>()

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            getSavedPrayerJurisprudence = getPrayerJurisprudence()
        }
    }
      fun getPrayList() = viewModelScope.launch {

        val madhab = if (getSavedPrayerJurisprudence.toInt() == 0) {
            Madhab.SHAFI
        } else {
            Madhab.HANAFI
        }

        val getPrayerTime = GetAdhanDetails.getPrayTime(userLatLong?.latitude ?:0.0, userLatLong?.longitude ?:0.0, madhab,selectedPrayerDate)

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Fajr",
                getPrayerTime[0], getFajrDetail()?: PrayerDetailData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Sunrise",
                getPrayerTime[1],getSunriseDetail()?: PrayerDetailData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Dhuhr",
                getPrayerTime[2],getDuhrDetail()?: PrayerDetailData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Asr",
                getPrayerTime[3],getAsrDetail()?: PrayerDetailData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Maghrib",
                getPrayerTime[4],getMagribDetail()?: PrayerDetailData()
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Isha",
                getPrayerTime[5],getIshaDetail()?: PrayerDetailData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Midnight",
                "11:42PM", getMidNightDetail()?: PrayerDetailData()
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Last Third",
                "1:40AM",getLastNightDetail()?: PrayerDetailData()
            )
        )
    }
}