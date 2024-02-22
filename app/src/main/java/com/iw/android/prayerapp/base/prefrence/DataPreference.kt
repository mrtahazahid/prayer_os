package com.iw.android.prayerapp.base.prefrence

import android.content.Context
import androidx.datastore.core.DataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.APPLICATION_ID
import com.iw.android.prayerapp.base.response.LoginUserResponse
import com.iw.android.prayerapp.data.response.UserLatLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APPLICATION_ID)

class DataPreference @Inject constructor(
    @ApplicationContext context: Context
) {

    private val appContext = context.applicationContext

    val accessToken: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN] ?: ""
        }

    val refreshToken: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN] ?: ""
        }

    val loginUserId: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[USER_ID] ?: ""
        }

    val prayerMethod: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[PRAYER_METHOD] ?: ""
        }


    val prayerJurisprudence: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[PRAYER_JURISPRUDENCE] ?: ""
        }


    val prayerElevation: Flow<String>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[PRAYER_ELEVATION_RULE] ?: ""
        }

    val isUserLogin: Flow<Boolean>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[IS_LOGIN] ?: false
        }

    val automaticLocation: Flow<Boolean>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[AUTOMATIC_LOCATION] ?: false
        }

    val isOnBoarding: Flow<Boolean>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[IS_ONBOARDING] ?: false
        }

    suspend fun saveAccessTokens(accessToken: String) {
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = "Bearer $accessToken"
        }
    }

    suspend fun getBooleanData(key: Preferences.Key<Boolean>): Boolean =
        appContext.dataStore.data.map { preferences ->
            preferences[key] ?: false
        }.first()

    suspend fun setBooleanData(key: Preferences.Key<Boolean>, value: Boolean) {
        appContext.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getStringData(key: Preferences.Key<String>): String =
        appContext.dataStore.data.map { preferences ->
            preferences[key] ?: ""
        }.first()

    suspend fun setStringData(key: Preferences.Key<String>, value: String) {
        appContext.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun getIntegerData(key: Preferences.Key<Int>): Int =
        appContext.dataStore.data.map { preferences ->
            preferences[key] ?: 75
        }.first()

    suspend fun setIntegerData(key: Preferences.Key<Int>, value: Int) {
        appContext.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun clear() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun clearLoginDataForUpdate() {
        setStringData(USER_INFO, "")
    }

    suspend fun performLogout() {
        setBooleanData(IS_LOGIN, false)
        setStringData(USER_ID, "")
        setStringData(USER_INFO, "")
        setStringData(ACCESS_TOKEN, "")
        setStringData(REFRESH_TOKEN, "")
    }

    suspend fun setUpdateUserProfile(userInfo: LoginUserResponse) {
        setStringData(USER_INFO, Gson().toJson(userInfo))

    }

    suspend fun setUserLatLong(userLatLong: UserLatLong) {
        setStringData(USER_LAT_LONG, Gson().toJson(userLatLong))

    }

    suspend fun getUpdatedUserProfile(): LoginUserResponse? {
        return Gson().fromJson(
            getStringData(USER_INFO),
            LoginUserResponse::class.java
        )
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("key_refresh_token")
        val IS_LOGIN = booleanPreferencesKey("key_is_login")
        val IS_ONBOARDING = booleanPreferencesKey("key_is_onboarding")
        val USER_ID = stringPreferencesKey("key_user_id")
        val GEOFENCE_RADIUS = intPreferencesKey("key_geofence_radius")
        val AUTOMATIC_LOCATION = booleanPreferencesKey("key_automatic_location")
        val PRAYER_METHOD = stringPreferencesKey("key_method")
        val USER_LAT_LONG = stringPreferencesKey("key_user_lat_long")
        val PRAYER_JURISPRUDENCE = stringPreferencesKey("key_jurisprudence")
        val PRAYER_ELEVATION_RULE = stringPreferencesKey("key_user_elevation")
        val USER_INFO = stringPreferencesKey("key_user_info")
        val FAJR_INFO = stringPreferencesKey("key_fajr_info")
        val SUNRISE_INFO = stringPreferencesKey("key_sunrise_info")
        val DHUHR_INFO = stringPreferencesKey("key_dhuhr_info")
        val ASR_INFO = stringPreferencesKey("key_asr_info")
        val MAGRIB_INFO = stringPreferencesKey("key_magrib_info")
        val ISHA_INFO = stringPreferencesKey("key_isha_info")
        val MIDNIGHT_INFO = stringPreferencesKey("key_midnight_info")
        val LASTTHIRD_INFO = stringPreferencesKey("key_lastnight_info")
        val APPLICATION_ID = "com.iw.android.prayerapp"
    }
}