package com.iw.android.prayerapp.ui.main.iqamaFragment


import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IqamaViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    var iqamaList = arrayListOf<IqamaData>()

    init {
        viewModelScope.launch {
            iqamaList.add(
                IqamaData(
                    "Fajr",
                    getIqamaFajrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                    getIqamaFajrDetail()?.iqamaTime
                )
            )
            iqamaList.add(
                IqamaData(
                    "Dhuhr",
                    getIqamaDhuhrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                    getIqamaDhuhrDetail()?.iqamaTime
                )
            )
            iqamaList.add(
                IqamaData(
                    "Asr",
                    getIqamaAsrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                    getIqamaAsrDetail()?.iqamaTime
                )
            )
            iqamaList.add(
                IqamaData(
                    "Maghrib",
                    getIqamaMaghribDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                    getIqamaMaghribDetail()?.iqamaTime
                )
            )
            iqamaList.add(
                IqamaData(
                    "Isha",
                    getIqamaIshaDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                    getIqamaIshaDetail()?.iqamaTime
                )
            )
        }
    }
}