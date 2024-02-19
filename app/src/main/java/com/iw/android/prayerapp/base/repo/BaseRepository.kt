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
import com.iw.android.prayerapp.data.response.PrayerDetailData
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

    suspend fun saveFajrDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.FAJR_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getFajrDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.FAJR_INFO),
            PrayerDetailData::class.java
        )
    }

    suspend fun saveSunriseDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.SUNRISE_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getSunriseDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.SUNRISE_INFO),
            PrayerDetailData::class.java
        )
    }

    suspend fun saveDuhrDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.DHUHR_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getDuhrDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.DHUHR_INFO),
            PrayerDetailData::class.java
        )
    }


    suspend fun saveAsrDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.ASR_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getAsrDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.ASR_INFO),
            PrayerDetailData::class.java
        )
    }

    suspend fun saveMagribDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.MAGRIB_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getMagribDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.MAGRIB_INFO),
            PrayerDetailData::class.java
        )
    }

    suspend fun saveIshaDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.ISHA_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getIshaDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.ISHA_INFO),
            PrayerDetailData::class.java
        )
    }

    suspend fun saveMidnightDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.MIDNIGHT_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getMidnightDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.MIDNIGHT_INFO),
            PrayerDetailData::class.java
        )
    }


    suspend fun saveLastThirdDetail(namazDetail: PrayerDetailData) {
        preferences.setStringData(DataPreference.LASTTHIRD_INFO, Gson().toJson(namazDetail))
    }

    suspend fun getLastThirdDetail(): PrayerDetailData? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.LASTTHIRD_INFO),
            PrayerDetailData::class.java
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