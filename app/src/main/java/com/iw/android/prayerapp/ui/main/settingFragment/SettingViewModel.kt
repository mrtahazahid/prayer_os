package com.iw.android.prayerapp.ui.main.settingFragment

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.LocationData
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.utils.GetAdhanDetails.getTimeZoneAndCity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor( repository: MainRepository) :
    BaseViewModel(repository) {
    var getSavedPrayerMethod = ""
    var getSavedPrayerJurisprudence = ""
    var getSavedPrayerElevationRule = ""
    var getUserLatLong: UserLatLong? = null
    var getAutomaticLocation = false
    private val _location = MutableLiveData<LocationData?>()
    val location: LiveData<LocationData?> = _location

    init {
        viewModelScope.launch {
            getSavedPrayerMethod = getPrayerMethod()
            getSavedPrayerJurisprudence = getPrayerJurisprudence()
            getSavedPrayerElevationRule = getPrayerElevation()
            getUserLatLong = getUserLatLong()
            getAutomaticLocation = getAutomaticLocationValue()

        }
    }

    fun fetchTimeZoneAndCity(context: Context) {
        viewModelScope.launch {
            val locationData = withContext(Dispatchers.IO) {
                getTimeZoneAndCity(
                    context,
                    getUserLatLong?.latitude ?: 0.0,
                    getUserLatLong?.longitude ?: 0.0
                )
            }
            _location.postValue(locationData)
        }
    }

    fun setLocationAutomaticValue(value: Boolean) = viewModelScope.launch {
        setLocationAutomatic(value)
    }
}