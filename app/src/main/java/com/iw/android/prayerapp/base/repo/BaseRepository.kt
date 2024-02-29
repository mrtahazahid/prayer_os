package com.iw.android.prayerapp.base.repo

import com.google.gson.Gson
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.IS_LOGIN
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.USER_ID
import com.iw.android.prayerapp.base.response.LoginUserResponse
import com.iw.android.prayerapp.base.network.BaseApi
import com.iw.android.prayerapp.base.network.SafeApiCall
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.AUTOMATIC_LOCATION
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.GEOFENCE_RADIUS
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.PRAYER_ELEVATION_RULE
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.PRAYER_JURISPRUDENCE
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.PRAYER_METHOD
import com.iw.android.prayerapp.data.response.NotificationData
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
    }

    suspend fun getFajrDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.FAJR_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveSunriseDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.SUNRISE_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getSunriseDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.SUNRISE_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveDuhrDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.DHUHR_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getDuhrDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.DHUHR_INFO),
            NotificationData::class.java
        )
    }


    suspend fun saveAsrDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.ASR_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getAsrDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.ASR_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveMagribDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.MAGRIB_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getMagribDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.MAGRIB_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveIshaDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.ISHA_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getIshaDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.ISHA_INFO),
            NotificationData::class.java
        )
    }

    suspend fun saveMidnightDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.MIDNIGHT_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getMidnightDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.MIDNIGHT_INFO),
            NotificationData::class.java
        )
    }


    suspend fun saveLastThirdDetail(namazDetail: NotificationData) {
        preferences.setStringData(DataPreference.LASTTHIRD_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getLastThirdDetail(): NotificationData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.LASTTHIRD_INFO),
            NotificationData::class.java
        )
    }


    suspend fun saveUserProfileData(userInfo: LoginUserResponse) {
        preferences.setStringData(DataPreference.USER_INFO, Gson().toJson(userInfo))
    }


    suspend fun logout() = safeApiCall {
        api.logout()
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

    suspend fun setUserLoggedIn() {
        preferences.setBooleanData(IS_LOGIN, true)
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


    suspend fun clearUserPreferenceData() {
        preferences.performLogout()
    }

    suspend fun clearUserLoginData() {
        preferences.clearLoginDataForUpdate()
    }
}