package com.iw.android.prayerapp.data.response

import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import kotlinx.serialization.Serializable

data class PrayTime(
    val image: Int,
    val title: String,
    val time: String,
    var createdDate: String = "",
    val namazDetail: NotificationData,
    var isCurrentNamaz: Boolean = false,
)

data class IqamaData(
    val namazName: String,
    var namazTime: String = "",
    var iqamaType: String = DuaTypeEnum.OFF.getValue(),
    var iqamaTime: IqamaTime? = null
)

data class JummuahData(
    var khutbaTime: String = "12:00 AM",
    var reminderTime: String = "off",
    var isEnabled: Boolean = false,
    var reminderTimeFormatted: String = ""
)

data class IqamaNotificationData(
    var reminderTime: String = "off",
    var reminderSound: Int = 0,
    var soundName: String = "",
    var updateInterval: String
)

data class IqamaDisplaySetting(
    var isTimeCountDownTimer: Boolean = false,
    var isShowInList: Boolean = false
)

data class IqamaTime(
    var iqamaTime: String = "12:00 AM",
    var iqamaMinutes: String = "",
    var iqamaMinutesTime: String = "",

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

data class IqamaTimeList(
    val namazName: String,
    val nameTimeString: String,
    val nameTimeLong: Long,
)

data class NotificationPrayerTime(
    val currentNamazName: String,
    val currentNamazTime: String,
    var isCalled: Boolean = false,
)

@Serializable
data class NotificationData(
    var namazName: String = "",
    var namazTime: String = "",
    var notificationSound: String = "Tones",
    var sound: Int? = null,
    var reminderSound: Int? = null,
    var reminderNotificationSound: String = "Tones",
    var duaType: String = DuaTypeEnum.OFF.getValue(),
    var duaTime: String = "12:00 AM",
    var reminderTimeMinutes: String = "12:00 AM",
    var reminderTime: String = "12:00 AM",
    var createdDate: String = "",
    var duaReminderMinutes: String = "off",
    var isForNotification: Boolean = false,
    var currentNotificationTypePosition: Int = 0
)

data class MonthlyCalenderData(
    val date: String,
    val day: String,
    val fjr: String,
    val shk: String,
    val dhr: String,
    val asr: String,
    val mgb: String,
    val ish: String,
    val hijri: String,
)

data class CurrentNamazNotificationData(
    val currentNamazName: String,
    val soundName: String = "Adhan",
    val soundToneName: String= "Tones",
    val position: Int,
    val isSoundSelected: Boolean,
    val isForAdhan: Boolean,
    val isVibrate: Boolean,
    val isSilent: Boolean,
    val isOff: Boolean,
    val sound: Int? = null,

    )

data class NotificationSettingData(
    val snoozeTime: String? = null,
    val isAdhanDuaOn: Boolean? = null,
    val isPrayOnTap: Boolean? = null,

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
    val selectedItemAdhanTitle: String = "",
    val selectedItemTonesTitle: String = "",
    val soundName: String = "Tones",
)


data class MoreData(
    val image: Int,
    val title: String
)

data class ContentData(
    val url: String
)
