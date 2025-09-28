package com.iw.android.prayerapp.data.response

data class MethodData(
    val title: String,
    val fajrAngle: String,
    val ishaAngle: String,
    val isDescriptionOn: Boolean=false,
    var isSelected: Boolean=false,
    val description: String="",
    var isItemLast: Boolean=false,
)
