package com.iw.android.prayerapp.ui.main.iqamaFragment


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.NotificationData
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
class IqamaViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel(repository) {

 var iqamaList = arrayListOf<IqamaData>()

    init {
        iqamaList.add(IqamaData("Fajr"))
        iqamaList.add(IqamaData("Duhr"))
        iqamaList.add(IqamaData("Asr"))
        iqamaList.add(IqamaData("Maghrib"))
        iqamaList.add(IqamaData("Isha"))
    }
}