package com.iw.android.prayerapp.data.response

import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import kotlinx.serialization.Serializable

data class PrayTime(
    val image: Int,
    val title: String,
    val time: String,
    var createdDate: String = "",
    val namazDetail: NotificationData
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

data class PrayerTime(
    val currentNamazName: String,
    val currentNamazTime: Long,
    val timeDifference: Long = 0,
    val totalTime: Long = 0
)

@Serializable
data class NotificationData(
    var namazName: String = "",
    var namazTime: String = "",
    var notificationSoundPosition: Int = 0,
    var notificationSound: String = "Tones",
    var notificationSoundId: Int = 0,
    var reminderNotificationSoundPosition: Int = 0,
    var reminderNotificationSound: String = "Tones",
    var reminderNotificationSoundId: Int = 0,
    var reminderTime: String = "off",
    var duaType: String = DuaTypeEnum.OFF.getValue(),
    var duaReminder: String = "off",
    var duaTime: String = "12:00 AM",
    var isNotificationCall: Boolean = false,
    var isReminderNotificationCall: Boolean = false,
    var createdDate: String = ""
)

data class NotificationSettingData(
    val snoozeTime: String,
    val isAdhanDuaOn: Boolean,
    val isPrayOnTap: Boolean,

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
