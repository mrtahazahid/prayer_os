package com.iw.android.prayerapp.base.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.repo.BaseRepository
import com.iw.android.prayerapp.data.response.PrayerDetailData
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

    fun saveFajrDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveFajrDetail(prayerDetail)
    }

    suspend fun getFajrDetail() = repository.getFajrDetail()
    fun saveSunriseDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveSunriseDetail(prayerDetail)
    }

    suspend fun getSunriseDetail() = repository.getSunriseDetail()
    fun saveDuhrDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveDuhrDetail(prayerDetail)
    }

    suspend fun getDuhrDetail() = repository.getDuhrDetail()
    fun saveAsrDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveAsrDetail(prayerDetail)
    }

    suspend fun getAsrDetail() = repository.getAsrDetail()

    fun saveMagribDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveMagribDetail(prayerDetail)
    }

    suspend fun getMagribDetail() = repository.getMagribDetail()
    fun saveIshaDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveIshaDetail(prayerDetail)
    }

    suspend fun getIshaDetail() = repository.getIshaDetail()

    fun saveMidNightDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
        repository.saveMidnightDetail(prayerDetail)
    }

    suspend fun getMidNightDetail() = repository.getMidnightDetail()

    fun saveLastNightDetail(prayerDetail: PrayerDetailData) = viewModelScope.launch {
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
        return repository.getUserLatLong()
    }

    fun setGeofenceRadius(value: Int) = viewModelScope.launch {
        repository.setGeofenceRadius(value)
    }

    suspend fun getGeofenceRadius(): Int = repository.getGeofenceRadius()


}