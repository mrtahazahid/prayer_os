package com.iw.android.prayerapp.base.prefrence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.APPLICATION_ID
import com.iw.android.prayerapp.base.response.LoginUserResponse
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.data.response.IqamaNotificationData
import com.iw.android.prayerapp.data.response.JummuahData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.NotificationSettingData
import com.iw.android.prayerapp.data.response.UserLatLong
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

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

    val automaticLocation: Flow<Boolean>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[AUTOMATIC_LOCATION] ?: false
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

    suspend fun saveSettingNotificationData(data: NotificationSettingData) {
        setStringData(DataPreference.SETTING_NOTIFICATION_DATA, Gson().toJson(data))
    }

    suspend fun getSettingNotificationData(): NotificationSettingData? = Gson().fromJson(
        getStringData(DataPreference.SETTING_NOTIFICATION_DATA),
        NotificationSettingData::class.java
    )

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

    suspend fun getUserLatLong(): UserLatLong? {
        return Gson().fromJson(
            getStringData(USER_LAT_LONG),
            UserLatLong::class.java
        )

    }

    suspend fun getCurrentNamazNotificationData(): CurrentNamazNotificationData? = Gson().fromJson(
        getStringData(CURRENT_NAMAZ_NOTIFICATION_DATA),
        CurrentNamazNotificationData::class.java
    )

    suspend fun setUpdateUserProfile(userInfo: LoginUserResponse) {
        setStringData(USER_INFO, Gson().toJson(userInfo))

    }

    suspend fun getIqamaNotificationSetting(): IqamaNotificationData? {
        return Gson().fromJson(
            getStringData(IQAMA_NOTIFICATION_SETTING),
            IqamaNotificationData::class.java
        )
    }

    suspend fun getJummuahSetting(): JummuahData? {
        return Gson().fromJson(
            getStringData(JUMMUAH_SETTING),
            JummuahData::class.java
        )
    }

    suspend fun getIqamaIshaDetail(): IqamaData? {
        return Gson().fromJson(
            getStringData(IQAMA_ISHA),
            IqamaData::class.java
        )
    }

    suspend fun getIqamaMaghribDetail(): IqamaData? {
        return Gson().fromJson(
            getStringData(IQAMA_MAGHRIB),
            IqamaData::class.java
        )
    }

    suspend fun getIqamaAsrDetail(): IqamaData? {
        return Gson().fromJson(
            getStringData(IQAMA_ASR),
            IqamaData::class.java
        )
    }

    suspend fun getIqamaDhuhrDetail(): IqamaData? {
        return Gson().fromJson(
            getStringData(IQAMA_DHUHR),
            IqamaData::class.java
        )
    }

    suspend fun getIqamaFajrDetail(): IqamaData? {
        return Gson().fromJson(
            getStringData(IQAMA_FAJR),
            IqamaData::class.java
        )
    }


//    suspend fun saveNotificationData(data: NotificationData) {
//        if(getNotificationData().isEmpty()){
//            setStringData(NOTIFICATION_DATA, Gson().toJson(data))
//        }else{
//            val notification = getNotificationData().add(data)
//            setStringData(NOTIFICATION_DATA, Gson().toJson(notification))
//        }
//
//    }
//
//    suspend fun getNotificationData(): ArrayList<NotificationData?> {
//        val json = Json { ignoreUnknownKeys = true }
//        val preferences = appContext.dataStore.data.first()
//        val jsonString = preferences[NOTIFICATION_DATA] ?: ""
//        return Gson().fromJson(jsonString, object : TypeToken<ArrayList<NotificationData>>() {}.type)
//    }

    suspend fun saveNotificationData(newItem: NotificationData) {
        val jsonString = appContext.dataStore.data
            .first()[NOTIFICATION_DATA]
            ?: "[]" // Default to an empty array if the key is not present

        val list: MutableList<NotificationData> = Json.decodeFromString(jsonString)

        // Check if NotificationData with the same createdDate already exists
        val existingItem = list.find { it.createdDate == newItem.createdDate }

        if (existingItem != null) {
            // If the item already exists, update its values
            existingItem.notificationSound = newItem.notificationSound
            existingItem.reminderNotificationSound = newItem.reminderNotificationSound
            existingItem.reminderTime = newItem.reminderTime
            existingItem.duaType = newItem.duaType
            existingItem.duaTime = newItem.duaTime
            // Update other fields as needed
        } else {
            // If the item doesn't exist, add it to the list
            list.add(newItem)
        }

        val updatedJsonString = Json.encodeToString(list)

        appContext.dataStore.edit { preferences ->
            preferences[NOTIFICATION_DATA] = updatedJsonString
        }
    }

    suspend fun getNotificationData(): List<NotificationData> {
        val jsonString = appContext.dataStore.data.first()[NOTIFICATION_DATA] ?: return emptyList()
        return mapJsonToList(jsonString)
    }

    suspend fun updateNotificationData(position: Int, data: NotificationData) {
        val jsonString = appContext.dataStore.data
            .first()[NOTIFICATION_DATA]
            ?: "[]" // Default to an empty array if the key is not present

        val list: MutableList<NotificationData> = Json.decodeFromString(jsonString)
        list[position] = data

        val updatedJsonString = Json.encodeToString(list)

        appContext.dataStore.edit { preferences ->
            preferences[NOTIFICATION_DATA] = updatedJsonString
        }
    }

    private fun convertListToJsonString(list: NotificationData): String {
        // adjust settings if needed
        return Json.encodeToString(list)
    }

    private fun mapJsonToList(jsonString: String): List<NotificationData> {
        val json = Json { ignoreUnknownKeys = true } // adjust settings if needed
        return json.decodeFromString(jsonString)
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
        val NOTIFICATION_DATA = stringPreferencesKey("key_notification_data")
        val SETTING_NOTIFICATION_DATA = stringPreferencesKey("key_setting_notification_data")
        val CURRENT_NAMAZ_NOTIFICATION_DATA =
            stringPreferencesKey("key_current_namaz_notification_data")
        val DHUHR_INFO = stringPreferencesKey("key_dhuhr_info")
        val ASR_INFO = stringPreferencesKey("key_asr_info")
        val MAGRIB_INFO = stringPreferencesKey("key_magrib_info")
        val ISHA_INFO = stringPreferencesKey("key_isha_info")
        val MIDNIGHT_INFO = stringPreferencesKey("key_midnight_info")
        val LASTTHIRD_INFO = stringPreferencesKey("key_lastnight_info")
        val IQAMA_FAJR = stringPreferencesKey("key_iqama_fajr")
        val IQAMA_DHUHR = stringPreferencesKey("key_iqama_dhuhr")
        val IQAMA_ASR = stringPreferencesKey("key_iqama-asr")
        val IQAMA_MAGHRIB = stringPreferencesKey("key_iqama_magrib")
        val IQAMA_ISHA = stringPreferencesKey("key_iqama_isha")
        val JUMMUAH_SETTING = stringPreferencesKey("key_jummuah_setting")
        val IQAMA_NOTIFICATION_SETTING = stringPreferencesKey("key_iqama_notification_setting")
        val IQAMA_DISPLAY_SETTING = stringPreferencesKey("key_iqama_display_setting")
        val APPLICATION_ID = "com.iw.android.prayerapp"
    }
}