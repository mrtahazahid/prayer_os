package com.iw.android.prayerapp.base.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.repo.BaseRepository
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.data.response.IqamaDisplaySetting
import com.iw.android.prayerapp.data.response.IqamaNotificationData
import com.iw.android.prayerapp.data.response.JummuahData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.NotificationSettingData
import com.iw.android.prayerapp.data.response.UserLatLong
import kotlinx.coroutines.launch


abstract class BaseViewModel(val repository: BaseRepository) : ViewModel() {


    suspend fun saveLoginUserId(userLoginId: String) = repository.saveLoginUserId(userLoginId)
    suspend fun getLoginUserId() = repository.getLoginUserId()

    suspend fun savePrayerMethod(prayerMethod: String) = repository.savePrayerMethod(prayerMethod)
    suspend fun getPrayerMethod() = repository.getPrayerMethod()


    suspend fun saveFloat(value: Float) = repository.saveFloat(value)
    suspend fun getFloat() = repository.getFloat()

    suspend fun setBoolean(value:Boolean) = repository.saveBoolean(value)
    suspend fun getBoolean() = repository.getBoolean()
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

    fun addCurrentNamazToList(data: NotificationData) = viewModelScope.launch {
        repository.saveNotificationData(data)
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
        return repository.getUserLatLong()
    }

    fun setGeofenceRadius(value: Int) = viewModelScope.launch {
        repository.setGeofenceRadius(value)
    }

    suspend fun getGeofenceRadius(): Int = repository.getGeofenceRadius()

    fun saveSettingNotificationData(data: NotificationSettingData) = viewModelScope.launch {
        repository.saveSettingNotificationData(data)
    }

    suspend fun getSettingNotificationData(): NotificationSettingData? =
        repository.getSettingNotificationData()

    suspend fun getAllNotificationData(): List<NotificationData?> =
        repository.getAllNotificationData()

    fun saveFajrCurrentNamazNotificationData(data: CurrentNamazNotificationData) =
        viewModelScope.launch {
            repository.saveFajrCurrentNamazNotificationData(data)
        }

    suspend fun getFajrCurrentNamazNotificationData(): CurrentNamazNotificationData? =
        repository.getFajrCurrentNamazNotificationData()

    fun saveDhuhrCurrentNamazNotificationData(data: CurrentNamazNotificationData) =
        viewModelScope.launch {
            repository.saveDhuhrCurrentNamazNotificationData(data)
        }

    suspend fun getDhuhrCurrentNamazNotificationData(): CurrentNamazNotificationData? =
        repository.getDhuhrCurrentNamazNotificationData()

    fun saveAsrCurrentNamazNotificationData(data: CurrentNamazNotificationData) =
        viewModelScope.launch {
            repository.saveAsrCurrentNamazNotificationData(data)
        }

    suspend fun getAsrCurrentNamazNotificationData(): CurrentNamazNotificationData? =
        repository.getAsrCurrentNamazNotificationData()


    fun saveMaghribCurrentNamazNotificationData(data: CurrentNamazNotificationData) =
        viewModelScope.launch {
            repository.saveMaghribCurrentNamazNotificationData(data)
        }

    suspend fun getMaghribCurrentNamazNotificationData(): CurrentNamazNotificationData? =
        repository.getMaghribCurrentNamazNotificationData()

    suspend fun getIshaCurrentNamazNotificationData(): CurrentNamazNotificationData? =
        repository.getIshaCurrentNamazNotificationData()

    fun saveIshaCurrentNamazNotificationData(data: CurrentNamazNotificationData) =
        viewModelScope.launch {
            repository.saveIshaCurrentNamazNotificationData(data)
        }


    suspend fun saveIqamaFajrDetail(prayerDetail: IqamaData) {
        repository.saveIqamaFajrDetail(prayerDetail)
    }

    suspend fun getIqamaFajrDetail() = repository.getIqamaFajrDetail()

    suspend fun saveIqamaDhuhrDetail(prayerDetail: IqamaData) {
        repository.saveIqamaDhuhrDetail(prayerDetail)
    }

    suspend fun getIqamaDhuhrDetail() = repository.getIqamaDhuhrDetail()

    suspend fun saveIqamaAsrDetail(prayerDetail: IqamaData) {
        repository.saveIqamaAsrDetail(prayerDetail)
    }

    suspend fun getIqamaAsrDetail() = repository.getIqamaAsrDetail()

    suspend fun saveIqamaMaghribDetail(prayerDetail: IqamaData) {
        repository.saveIqamaMaghribDetail(prayerDetail)
    }

    suspend fun getIqamaMaghribDetail() = repository.getIqamaMaghribDetail()

    suspend fun saveIqamaIshaDetail(prayerDetail: IqamaData) {
        repository.saveIqamaIshaDetail(prayerDetail)
    }

    suspend fun getIqamaIshaDetail() = repository.getIqamaIshaDetail()

    suspend fun saveJummuahSetting(data: JummuahData) {
        repository.saveJummuahSetting(data)
    }

    suspend fun getJummuahSetting() = repository.getJummuahSetting()


    suspend fun saveIqamaNotificationSetting(data: IqamaNotificationData) {
        repository.saveIqamaNotificationSetting(data)
    }

    suspend fun getIqamaNotificationSetting() = repository.getIqamaNotificationSetting()


    suspend fun saveIqamaDisplaySetting(data: IqamaDisplaySetting) {
        repository.saveIqamaDisplaySetting(data)
    }

    suspend fun getIqamaDisplaySetting() = repository.getIqamaDisplaySetting()

}