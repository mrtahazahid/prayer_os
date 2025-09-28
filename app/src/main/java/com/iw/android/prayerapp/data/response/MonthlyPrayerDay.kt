package com.iw.android.prayerapp.data.response

data class MonthlyPrayerDay(
    val month: String,
    val day: String,
    val date: String,
    val sunrise: String,
    val fajar: String,
    val zohar: String,
    val asar: String,
    val maghrib: String,
    val isha: String,
    val hijri: String,
    val hijriName: String
)

data class IslamicDate(
    val name: String, // Holds the abbreviated month name
    val date: Int    // Holds the date
)