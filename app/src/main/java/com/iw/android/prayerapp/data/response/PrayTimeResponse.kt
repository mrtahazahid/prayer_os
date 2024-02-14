package com.iw.android.prayerapp.data.response

data class PrayTime(
    val image: Int,
    val title: String,
    val time: String
)

data class IslamicHolidayResponse(
    val title: String,
    val islamicDayTitle: String,
    val dayTitle: String
)

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
