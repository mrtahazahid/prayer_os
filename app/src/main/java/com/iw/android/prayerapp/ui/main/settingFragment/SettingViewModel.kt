package com.iw.android.prayerapp.ui.main.settingFragment

import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.UserLatLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel(repository) {
    var getSavedPrayerMethod = ""
    var getSavedPrayerJurisprudence = ""
    var getSavedPrayerElevationRule = ""
    var getUserLatLong: UserLatLong? = null
    var getAutomaticLocation = false


    init {
        viewModelScope.launch {
            getSavedPrayerMethod = getPrayerMethod()
            getSavedPrayerJurisprudence = getPrayerJurisprudence()
            getSavedPrayerElevationRule = getPrayerElevation()
            getUserLatLong = getUserLatLong()
            getAutomaticLocation = getAutomaticLocationValue()

        }
    }

    fun setLocationAutomaticValue(value: Boolean) = viewModelScope.launch {
        setLocationAutomatic(value)
    }
}