package com.iw.android.prayerapp.base.repo

import com.google.gson.Gson
import com.iw.android.prayerapp.base.network.BaseApi
import com.iw.android.prayerapp.base.network.SafeApiCall
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.AUTOMATIC_LOCATION
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.GEOFENCE_RADIUS
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.PRAYER_ELEVATION_RULE
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.PRAYER_JURISPRUDENCE
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.PRAYER_METHOD
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.USER_ID
import com.iw.android.prayerapp.base.response.LoginUserResponse
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.data.response.IqamaDisplaySetting
import com.iw.android.prayerapp.data.response.IqamaNotificationData
import com.iw.android.prayerapp.data.response.JummuahData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.NotificationSettingData
import com.iw.android.prayerapp.data.response.UserLatLong
import kotlinx.coroutines.flow.first
import javax.inject.Singleton

@Singleton
abstract class BaseRepository(
    private val api: BaseApi,
    val preferences: DataPreference
) : SafeApiCall {


    suspend fun getLoginUserId(): String {
        return preferences.loginUserId.first()
    }

    suspend fun getPrayerMethod(): String {
        return preferences.prayerMethod.first()
    }

    suspend fun getPrayerJurisprudence(): String {
        return preferences.prayerJurisprudence.first()
    }

    suspend fun getPrayerElevation(): String {
        return preferences.prayerElevation.first()
    }

    suspend fun getLoginUserData(): LoginUserResponse? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.USER_INFO),
            LoginUserResponse::class.java
        )
    }

    suspend fun getUserLatLong(): UserLatLong? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.USER_LAT_LONG),
            UserLatLong::class.java
        )

    }

    suspend fun saveUserLatLong(namazDetail: UserLatLong) {
        preferences.setStringData(DataPreference.USER_LAT_LONG, Gson().toJson(namazDetail))
    }

    suspend fun saveFajrDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.FAJR_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getFajrDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.FAJR_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveSunriseDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.SUNRISE_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getSunriseDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.SUNRISE_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveDuhrDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.DHUHR_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getDuhrDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.DHUHR_INFO),
            NotificationData::class.java
        )
    }


    suspend fun saveAsrDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.ASR_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getAsrDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.ASR_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveMagribDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.MAGRIB_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getMagribDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.MAGRIB_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveIshaDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.ISHA_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getIshaDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.ISHA_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveMidnightDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.MIDNIGHT_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getMidnightDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.MIDNIGHT_INFO),
            NotificationData::class.java
        )
    }


    suspend fun saveLastThirdDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.LASTTHIRD_INFO, Gson().toJson(namazDetail))
        saveNotificationData(namazDetail)
    }

    suspend fun getLastThirdDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.LASTTHIRD_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveLoginUserId(userId: String) {
        preferences.setStringData(USER_ID, userId)
    }

    suspend fun savePrayerMethod(prayerMethod: String) {
        preferences.setStringData(PRAYER_METHOD, prayerMethod)
    }

    suspend fun savePrayerJurisprudence(prayerJurisprudence: String) {
        preferences.setStringData(PRAYER_JURISPRUDENCE, prayerJurisprudence)
    }

    suspend fun savePrayerElevationRule(prayerElevationRule: String) {
        preferences.setStringData(PRAYER_ELEVATION_RULE, prayerElevationRule)
    }

    suspend fun setIsOnboarding() {
        preferences.setBooleanData(IS_ONBOARDING, true)
    }

    suspend fun setLocationAutomatic(value: Boolean) {
        preferences.setBooleanData(AUTOMATIC_LOCATION, value)
    }

    suspend fun getLocationAutomatic(): Boolean = preferences.automaticLocation.first()

    suspend fun setGeofenceRadius(value: Int) {
        preferences.setIntegerData(GEOFENCE_RADIUS, value)
    }

    suspend fun getGeofenceRadius() = preferences.getIntegerData(GEOFENCE_RADIUS)

    suspend fun saveNotificationData(data: NotificationData) {
        preferences.saveNotificationData(data)
    }

    suspend fun getAllNotificationData(): List<NotificationData?> =
        preferences.getNotificationData()

    suspend fun saveSettingNotificationData(data: NotificationSettingData) {
        preferences.setStringData(DataPreference.SETTING_NOTIFICATION_DATA, Gson().toJson(data))
    }

    suspend fun getSettingNotificationData(): NotificationSettingData? = Gson().fromJson(
        preferences.getStringData(DataPreference.SETTING_NOTIFICATION_DATA),
        NotificationSettingData::class.java
    )

    suspend fun saveCurrentNamazNotificationData(currentNamazNotificationData: CurrentNamazNotificationData) {
        preferences.setStringData(
            DataPreference.CURRENT_NAMAZ_NOTIFICATION_DATA,
            Gson().toJson(currentNamazNotificationData)
        )

    }

    suspend fun getCurrentNamazNotificationData(): CurrentNamazNotificationData? = Gson().fromJson(
        preferences.getStringData(DataPreference.CURRENT_NAMAZ_NOTIFICATION_DATA),
        CurrentNamazNotificationData::class.java
    )

    suspend fun deleteCurrentNamazNotificationData() {
        preferences.setStringData(
            DataPreference.CURRENT_NAMAZ_NOTIFICATION_DATA,
            Gson().toJson(null)
        )
    }

    suspend fun saveIqamaFajrDetail(namazDetail: IqamaData) {
        preferences.setStringData(DataPreference.IQAMA_FAJR, Gson().toJson(namazDetail))
    }

    suspend fun getIqamaFajrDetail(): IqamaData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_FAJR),
            IqamaData::class.java
        )
    }


    suspend fun saveIqamaDhuhrDetail(namazDetail: IqamaData) {
        preferences.setStringData(DataPreference.IQAMA_DHUHR, Gson().toJson(namazDetail))
    }

    suspend fun getIqamaDhuhrDetail(): IqamaData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_DHUHR),
            IqamaData::class.java
        )
    }

    suspend fun saveIqamaAsrDetail(namazDetail: IqamaData) {
        preferences.setStringData(DataPreference.IQAMA_ASR, Gson().toJson(namazDetail))
    }

    suspend fun getIqamaAsrDetail(): IqamaData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_ASR),
            IqamaData::class.java
        )
    }

    suspend fun saveIqamaMaghribDetail(namazDetail: IqamaData) {
        preferences.setStringData(DataPreference.IQAMA_MAGHRIB, Gson().toJson(namazDetail))
    }

    suspend fun getIqamaMaghribDetail(): IqamaData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_MAGHRIB),
            IqamaData::class.java
        )
    }

    suspend fun saveIqamaIshaDetail(namazDetail: IqamaData) {
        preferences.setStringData(DataPreference.IQAMA_ISHA, Gson().toJson(namazDetail))
    }

    suspend fun getIqamaIshaDetail(): IqamaData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_ISHA),
            IqamaData::class.java
        )
    }

    suspend fun saveIqamaDisplaySetting(data: IqamaDisplaySetting) {
        preferences.setStringData(DataPreference.IQAMA_DISPLAY_SETTING, Gson().toJson(data))
    }

    suspend fun getIqamaDisplaySetting(): IqamaDisplaySetting? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_DISPLAY_SETTING),
            IqamaDisplaySetting::class.java
        )
    }


    suspend fun saveJummuahSetting(data: JummuahData) {
        preferences.setStringData(DataPreference.JUMMUAH_SETTING, Gson().toJson(data))
    }

    suspend fun getJummuahSetting(): JummuahData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.JUMMUAH_SETTING),
            JummuahData::class.java
        )
    }

    suspend fun saveIqamaNotificationSetting(data: IqamaNotificationData) {
        preferences.setStringData(DataPreference.IQAMA_NOTIFICATION_SETTING, Gson().toJson(data))
    }

    suspend fun getIqamaNotificationSetting(): IqamaNotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.IQAMA_NOTIFICATION_SETTING),
            IqamaNotificationData::class.java
        )
    }



}