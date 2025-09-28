package com.iw.android.prayerapp.utils.time

import android.util.Log
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import java.util.Locale

 fun subtractMinutesFromTime(currentTime: String, minutesToSubtract: Int): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
        val parsedTime = LocalTime.parse(currentTime, formatter)
        val resultTime = parsedTime.minusMinutes(minutesToSubtract.toLong())
        resultTime.format(formatter)
    } catch (e: DateTimeParseException) {
        Log.d("DateTimeParseException", e.message.toString())
        currentTime
    }
}


 fun addMinutesToTime(currentTime: String, minutesToAdd: Int): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH)
        val parsedTime = LocalTime.parse(currentTime, formatter)
        val resultTime = parsedTime.plusMinutes(minutesToAdd.toLong())
        resultTime.format(formatter)
    } catch (e: DateTimeParseException) {
        Log.d("DateTimeParseException", e.message.toString())
        currentTime
    }
}

 fun extractNumberFromString(input: String): Int {
    return if (input.isNullOrEmpty() || input == "off") {
        0
    } else {
        val regex = "\\d+".toRegex()  // Find one or more digits
        regex.find(input)?.value?.toInt() ?: 0
    }

}

fun isTodayFriday(): Boolean {
    val calendar = Calendar.getInstance()
    val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    return dayOfWeek == Calendar.FRIDAY
}