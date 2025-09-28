package com.iw.android.prayerapp.ui.main.monthlyCalender

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationParameters
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.IslamicDate
import com.iw.android.prayerapp.data.response.MonthlyPrayerDay
import com.iw.android.prayerapp.extension.converterForMonthly
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.Helper
import com.iw.android.prayerapp.utils.method.getMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MonthlyViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    private val fullYearData = mutableListOf<MonthlyPrayerDay>()
    private val pageSize = 30
    private var currentPage = 0
    var isLoading = MutableLiveData<Boolean>()

    private val _pagedData = MutableLiveData<List<MonthlyPrayerDay>>()
    val pagedData: LiveData<List<MonthlyPrayerDay>> = _pagedData

    init {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val userLatLong = getUserLatLong()
            val selectedMethodFromDB = getPrayerMethod()
            val selectedJurisprudenceFromDB = getPrayerJurisprudence()
            val method = getMethod(
                selectedMethod = selectedMethodFromDB,
                selectedJurisprudence = selectedJurisprudenceFromDB
            )
            setMonthlyCalenderData(
                userLatLong?.latitude ?: 0.0,
                userLatLong?.longitude ?: 0.0,
                method
            )
            loadNextPage()
        }
    }

    fun loadNextPage() {
        if ((pagedData.value?.size ?: 0) >= fullYearData.size) return // No more data

        val start = currentPage * pageSize
        val end = minOf(start + pageSize, fullYearData.size)

        val nextPage = fullYearData.subList(start, end)
        val currentList = _pagedData.value.orEmpty().toMutableList()
        currentList.addAll(nextPage)

        _pagedData.postValue(currentList)
        currentPage++
    }


    private fun setMonthlyCalenderData(lat: Double, lng: Double, method: CalculationParameters) {
        val allDateInfo = Helper.generateDayAndWeekInfo()
        val hijriData = getIslamicDatesForYear()
        for ((index, i) in Helper.generateDatesAsDateObjects().withIndex()) {
            val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                lat,
                lng,
                method,
                i
            )
            fullYearData.add(
                MonthlyPrayerDay(
                    month = allDateInfo[index].previousMonth.getDisplayName(
                        TextStyle.SHORT,
                        Locale.ENGLISH
                    ),
                    day = allDateInfo[index].dayOfWeek,
                    date = allDateInfo[index].dayOfMonth.toString(),
                    sunrise = converterForMonthly(getPrayerTime.sunrise.toEpochMilliseconds()),
                    fajar = converterForMonthly(getPrayerTime.fajr.toEpochMilliseconds()),
                    zohar = converterForMonthly(getPrayerTime.dhuhr.toEpochMilliseconds()),
                    asar = converterForMonthly(getPrayerTime.asr.toEpochMilliseconds()),
                    maghrib = converterForMonthly(getPrayerTime.maghrib.toEpochMilliseconds()),
                    isha = converterForMonthly(getPrayerTime.isha.toEpochMilliseconds()),
                    hijri = hijriData[index].date.toString(),
                    hijriName = hijriData[index].name
                )
            )
        }
    }

    private fun getIslamicDatesForYear(): List<IslamicDate> {
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)
        val islamicDatesList = mutableListOf<IslamicDate>()

        val startDate = LocalDate.now()
        val endDate = startDate.plusYears(1)

        var currentDate = startDate
        while (currentDate.isBefore(endDate)) {
            val hijriDate = HijrahDate.from(currentDate)
            val formattedHijriDate = hijriDate.format(formatter)

            val splitDate = formattedHijriDate.split(" ")
            val shortMonth = splitDate[1]  // e.g., "Raj"
            val day = splitDate[0].toInt() // e.g., "19"

            islamicDatesList.add(IslamicDate(name = shortMonth, date = day))
            currentDate = currentDate.plusDays(1)
        }

        return islamicDatesList
    }
}