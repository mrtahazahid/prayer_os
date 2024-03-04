package com.iw.android.prayerapp.base.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.repo.BaseRepository
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.NotificationSettingData
import com.iw.android.prayerapp.data.response.UserLatLong
import kotlinx.coroutines.launch


abstract class BaseViewModel(private val repository: BaseRepository) : ViewModel() {


    suspend fun saveLoginUserId(userLoginId: String) = repository.saveLoginUserId(userLoginId)
    suspend fun getLoginUserId() = repository.getLoginUserId()

    suspend fun savePrayerMethod(prayerMethod: String) = repository.savePrayerMethod(prayerMethod)
    suspend fun getPrayerMethod() = repository.getPrayerMethod()
    suspend fun savePrayerElevation(prayerElevation: String) =
        repository.savePrayerElevationRule(prayerElevation)

    suspend fun getPrayerElevation() = repository.getPrayerElevation()
    suspend fun savePrayerJurisprudence(prayerJurisprudence: String) =
        repository.savePrayerJurisprudence(prayerJurisprudence)

    suspend fun getPrayerJurisprudence() = repository.getPrayerJurisprudence()

    suspend fun getUserInfoData() = repository.getLoginUserData()

    suspend fun saveUserLatLong(userLatLong: UserLatLong) {
        repository.saveUserLatLong(userLatLong)
    }

    fun saveFajrDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveFajrDetail(prayerDetail)
    }

    suspend fun getFajrDetail() = repository.getFajrDetail()
    fun saveSunriseDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveSunriseDetail(prayerDetail)
    }

    suspend fun getSunriseDetail() = repository.getSunriseDetail()
    fun saveDuhrDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveDuhrDetail(prayerDetail)
    }

    suspend fun getDuhrDetail() = repository.getDuhrDetail()
    fun saveAsrDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveAsrDetail(prayerDetail)
    }

    suspend fun getAsrDetail() = repository.getAsrDetail()

    fun saveMagribDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveMagribDetail(prayerDetail)
    }

    suspend fun getMagribDetail() = repository.getMagribDetail()
    fun saveIshaDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveIshaDetail(prayerDetail)
    }

    suspend fun getIshaDetail() = repository.getIshaDetail()

    fun saveMidNightDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveMidnightDetail(prayerDetail)
    }

    suspend fun getMidNightDetail() = repository.getMidnightDetail()

    fun saveLastNightDetail(prayerDetail: NotificationData) = viewModelScope.launch {
        repository.saveLastThirdDetail(prayerDetail)
    }


    suspend fun getLastNightDetail() = repository.getLastThirdDetail()


    suspend fun saveIsOnBoarding() {
        repository.setIsOnboarding()
    }

    suspend fun getAutomaticLocationValue() = repository.getLocationAutomatic()
    suspend fun setLocationAutomatic(value: Boolean) {
        repository.setLocationAutomatic(value)
    }

    suspend fun getUserLatLong(): UserLatLong? {
        Log.d("getUserLatLong", repository.getUserLatLong().toString())
        return repository.getUserLatLong()
    }

    fun setGeofenceRadius(value: Int) = viewModelScope.launch {
        repository.setGeofenceRadius(value)
    }

    suspend fun getGeofenceRadius(): Int = repository.getGeofenceRadius()

    fun saveSettingNotificationData(data: NotificationSettingData) = viewModelScope.launch {
        repository.saveSettingNotificationData(data)
    }

    suspend fun getSettingNotificationData(): NotificationSettingData =
        repository.getSettingNotificationData()

    suspend fun getAllNotificationData(): List<NotificationData?> =
        repository.getAllNotificationData()

}