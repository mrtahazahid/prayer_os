package com.iw.android.prayerapp.base.prefrence


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.iw.android.prayerapp.base.response.LocationResponse
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataPreference.APPLICATION_ID)

class DataPreference @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val dataStore = context.dataStore

    val prayerMethod: Flow<String> get() = getStringFlow(PRAYER_METHOD)
    val prayerJurisprudence: Flow<String> get() = getStringFlow(PRAYER_JURISPRUDENCE)
    val prayerElevation: Flow<String> get() = getStringFlow(PRAYER_ELEVATION_RULE)
    val automaticLocation: Flow<Boolean> get() = getBooleanFlow(AUTOMATIC_LOCATION, true)
    val float: Flow<Float> get() = getFloatFlow(FLOAT)
    val boolean: Flow<Boolean> get() = getBooleanFlow(BOOLEAN)
    val isFirstTime: Flow<Boolean> get() = getBooleanFlow(IS_FIRST_TIME, true)

    // region Basic Getters

    private fun getBooleanFlow(key: Preferences.Key<Boolean>, default: Boolean = false): Flow<Boolean> =
        dataStore.data.map { it[key] ?: default }

    private fun getStringFlow(key: Preferences.Key<String>, default: String = ""): Flow<String> =
        dataStore.data.map { it[key] ?: default }

    private fun getFloatFlow(key: Preferences.Key<Float>, default: Float = 0f): Flow<Float> =
        dataStore.data.map { it[key] ?: default }

    suspend fun getBooleanData(key: Preferences.Key<Boolean>): Boolean = getBooleanFlow(key).first()
    suspend fun getFloatData(key: Preferences.Key<Float>): Float = getFloatFlow(key).first()
    suspend fun getStringData(key: Preferences.Key<String>): String = getStringFlow(key).first()
    suspend fun getIntegerData(key: Preferences.Key<Int>): Int =
        dataStore.data.map { it[key] ?: 75 }.first()

    suspend fun setBooleanData(key: Preferences.Key<Boolean>, value: Boolean) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setFloatData(key: Preferences.Key<Float>, value: Float) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setStringData(key: Preferences.Key<String>, value: String) {
        dataStore.edit { it[key] = value }
    }

    suspend fun setIntegerData(key: Preferences.Key<Int>, value: Int) {
        dataStore.edit { it[key] = value }
    }

    suspend fun removeKey(key: Preferences.Key<*>) {
        dataStore.edit { it.remove(key) }
    }

    suspend fun containsKey(key: Preferences.Key<*>): Boolean {
        return dataStore.data.first().contains(key)
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    // endregion

    // region JSON Helpers

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private inline fun <reified T> decodeJson(jsonString: String): T? =
        runCatching { json.decodeFromString<T>(jsonString) }.getOrNull()

    private inline fun <reified T> encodeJson(data: T): String =
        json.encodeToString(data)

    // endregion

    // region Grouped Preferences

    suspend fun getPrayerPreferences(): PrayerPreferences =
        PrayerPreferences(
            prayerMethod.first(),
            prayerJurisprudence.first(),
            prayerElevation.first(),
            automaticLocation.first()
        )

    data class PrayerPreferences(
        val method: String,
        val jurisprudence: String,
        val elevationRule: String,
        val automaticLocation: Boolean
    )

    // endregion

    // region Single Objects

    suspend fun getUserLatLong(): UserLatLong? = decodeJson(getStringData(USER_LAT_LONG))

    suspend fun getSettingNotificationData(): NotificationSettingData? =
        decodeJson(getStringData(SETTING_NOTIFICATION_DATA))

    suspend fun saveSettingNotificationData(data: NotificationSettingData) {
        setStringData(SETTING_NOTIFICATION_DATA, encodeJson(data))
    }

    suspend fun getFajrDetail(): NotificationData? = decodeJson(getStringData(CURRENT_NAMAZ_Fajr_NOTIFICATION_DATA))
    suspend fun getSunriseDetail(): NotificationData? = decodeJson(getStringData(SUNRISE_INFO))
    suspend fun getDuhrDetail(): NotificationData? = decodeJson(getStringData(CURRENT_NAMAZ_DHUHR_NOTIFICATION_DATA))
    suspend fun getAsrDetail(): NotificationData? = decodeJson(getStringData(CURRENT_NAMAZ_ASR_NOTIFICATION_DATA))
    suspend fun getMagribDetail(): NotificationData? = decodeJson(getStringData(CURRENT_NAMAZ_MAGHRIB_NOTIFICATION_DATA))
    suspend fun getIshaDetail(): NotificationData? = decodeJson(getStringData(CURRENT_NAMAZ_ISHA_NOTIFICATION_DATA))
    suspend fun getMidnightDetail(): NotificationData? = decodeJson(getStringData(MIDNIGHT_INFO))
    suspend fun getLastThirdDetail(): NotificationData? = decodeJson(getStringData(LASTTHIRD_INFO))

    suspend fun getIqamaFajrDetail(): IqamaData? = decodeJson(getStringData(IQAMA_FAJR))
    suspend fun getIqamaDhuhrDetail(): IqamaData? = decodeJson(getStringData(IQAMA_DHUHR))
    suspend fun getIqamaAsrDetail(): IqamaData? = decodeJson(getStringData(IQAMA_ASR))
    suspend fun getIqamaMaghribDetail(): IqamaData? = decodeJson(getStringData(IQAMA_MAGHRIB))
    suspend fun getIqamaIshaDetail(): IqamaData? = decodeJson(getStringData(IQAMA_ISHA))

    suspend fun getIqamaNotificationSetting(): IqamaNotificationData? = decodeJson(getStringData(IQAMA_NOTIFICATION_SETTING))
    suspend fun getJummuahSetting(): JummuahData? = decodeJson(getStringData(JUMMUAH_SETTING))

    // endregion

    // region NotificationData List

    suspend fun getNotificationData(): List<NotificationData> {
        val jsonString = getStringData(NOTIFICATION_DATA)
        return decodeJson(jsonString) ?: emptyList()
    }

    suspend fun saveNotificationData(newItem: NotificationData) {
        val list = getNotificationData().toMutableList()
        val index = list.indexOfFirst { it.namazName == newItem.namazName }
        if (index >= 0) list[index] = newItem else list.add(newItem)
        setStringData(NOTIFICATION_DATA, encodeJson(list))
    }

    suspend fun updateNotificationData(position: Int, data: NotificationData) {
        val list = getNotificationData().toMutableList()
        if (position in list.indices) {
            list[position] = data
            setStringData(NOTIFICATION_DATA, encodeJson(list))
        }
    }

    suspend fun removeNotificationData(index: Int) {
        val list = getNotificationData().toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            setStringData(NOTIFICATION_DATA, encodeJson(list))
        }
    }

    // endregion

    // region Location List

    suspend fun saveRecentLocationDataIntoList(newItem: LocationResponse) {
        val list = getRecentLocationDataIntoList().toMutableList()
        if (list.none { it.locationName == newItem.locationName }) {
            list.add(newItem)
            setStringData(RECENT_LOCATION_ITEM_DATA, encodeJson(list))
        }
    }

    suspend fun getRecentLocationDataIntoList(): List<LocationResponse> {
        val jsonString = getStringData(RECENT_LOCATION_ITEM_DATA)
        return decodeJson(jsonString) ?: emptyList()
    }

    // endregion

    companion object {
        const val APPLICATION_ID = "com.iw.android.prayerapp"

        val IS_ONBOARDING = booleanPreferencesKey("key_is_onboarding")
        val IS_FIRST_TIME = booleanPreferencesKey("key_is_first_time")
        val BOOLEAN = booleanPreferencesKey("key_boolean")
        val FLOAT = floatPreferencesKey("key_float")
        val GEOFENCE_RADIUS = intPreferencesKey("key_geofence_radius")
        val PRAYER_METHOD = stringPreferencesKey("key_method")
        val AUTOMATIC_LOCATION = booleanPreferencesKey("key_automatic_location")
        val USER_LAT_LONG = stringPreferencesKey("key_user_lat_long")
        val PRAYER_JURISPRUDENCE = stringPreferencesKey("key_jurisprudence")
        val PRAYER_ELEVATION_RULE = stringPreferencesKey("key_user_elevation")
        val SUNRISE_INFO = stringPreferencesKey("key_sunrise_info")
        val NOTIFICATION_DATA = stringPreferencesKey("key_notification_data")
        val RECENT_LOCATION_ITEM_DATA = stringPreferencesKey("key_recent_data")
        val SETTING_NOTIFICATION_DATA = stringPreferencesKey("key_setting_notification_data")
        val CURRENT_NAMAZ_Fajr_NOTIFICATION_DATA = stringPreferencesKey("key_current_namaz_fajr_notification_data")
        val CURRENT_NAMAZ_DHUHR_NOTIFICATION_DATA = stringPreferencesKey("key_current_namaz_dhuhr_notification_data")
        val CURRENT_NAMAZ_ASR_NOTIFICATION_DATA = stringPreferencesKey("key_current_namaz_asr_notification_data")
        val CURRENT_NAMAZ_MAGHRIB_NOTIFICATION_DATA = stringPreferencesKey("key_current_namaz_maghrib_notification_data")
        val CURRENT_NAMAZ_ISHA_NOTIFICATION_DATA = stringPreferencesKey("key_current_namaz_isha_notification_data")
        val MIDNIGHT_INFO = stringPreferencesKey("key_midnight_info")
        val LASTTHIRD_INFO = stringPreferencesKey("key_lastnight_info")
        val IQAMA_FAJR = stringPreferencesKey("key_iqama_fajr")
        val IQAMA_DHUHR = stringPreferencesKey("key_iqama_dhuhr")
        val IQAMA_ASR = stringPreferencesKey("key_iqama_asr")
        val TIME_DATA_SETTING = stringPreferencesKey("key_time_data_setting")
        val IQAMA_MAGHRIB = stringPreferencesKey("key_iqama_magrib")
        val IQAMA_ISHA = stringPreferencesKey("key_iqama_isha")
        val JUMMUAH_SETTING = stringPreferencesKey("key_jummuah_setting")
        val IQAMA_NOTIFICATION_SETTING = stringPreferencesKey("key_iqama_notification_setting")
        val IQAMA_DISPLAY_SETTING = stringPreferencesKey("key_iqama_display_setting")
    }
}
