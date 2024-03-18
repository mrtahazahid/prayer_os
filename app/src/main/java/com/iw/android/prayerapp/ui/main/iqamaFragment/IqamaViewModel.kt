package com.iw.android.prayerapp.ui.main.iqamaFragment


import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IqamaViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    var iqamaList = arrayListOf<IqamaData>()
    var lat = 0.0
    var long = 0.0


    init {
        addList()
        viewModelScope.launch {
            lat = repository.getUserLatLong()?.latitude ?: 0.0
            long = repository.getUserLatLong()?.longitude ?: 0.0
        }

    }

    fun addList() = viewModelScope.launch {
        val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(lat, long)
        iqamaList.add(
            IqamaData(
                "Fajr", convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()),
                getIqamaFajrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaFajrDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Dhuhr",convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()),
                getIqamaDhuhrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaDhuhrDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Asr",convertToFunTime(getPrayerTime.asr.toEpochMilliseconds()),
                getIqamaAsrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaAsrDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Maghrib",convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds()),
                getIqamaMaghribDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaMaghribDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Isha",convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()),
                getIqamaIshaDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaIshaDetail()?.iqamaTime
            )
        )

    }
}