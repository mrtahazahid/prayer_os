package com.iw.android.prayerapp.data.response

data class PrayTime(
    val image: Int,
    val title: String,
    val time: String,
    val namazDetail: PrayerDetailData
)

data class SoundData(
    val title: String,
    val soundFile: Int,
    var isSoundSelected: Boolean
)

data class LocationData(
    val timeZone: String,
    val city: String
)

data class SaveLocationData(
    val isAutomatic: Boolean,
    val geofence: String
)

data class PrayerDetailData(
    val notificationSoundPosition: Int = 0,
    val notificationSound: String = "Tones",
    val reminderNotificationSoundPosition: Int = 0,
    val reminderNotificationSound: String = "Tones",
    val reminderTime: String = "off",
    val duaReminder: String = "off",
    val duaTime: String = "12:00 AM"
)

data class IslamicHolidayResponse(
    val title: String,
    val islamicDayTitle: String,
    val dayTitle: String
)

data class UserLatLong(val latitude: Double, val longitude: Double)

data class PrayerSoundData(
    val title: String,
    val icon: Int,
    val type: String,
    val isImageForwardShow: Boolean,
    var isItemSelected: Boolean,
    val selectedItemTitle: String = ""
)


data class MoreData(
    val image: Int,
    val title: String
)

data class ContentData(
    val url: String
)
