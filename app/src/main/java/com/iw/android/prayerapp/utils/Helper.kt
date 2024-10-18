package com.iw.android.prayerapp.utils

import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

object Helper {

    fun generateDatesAsDateObjects(): List<Date> {
        val year = LocalDate.now().year
        val dateList = mutableListOf<Date>()

        // Loop through each month
        for (month in Month.values()) {
            val monthLength = month.length(LocalDate.now().isLeapYear)

            // Loop through each day in the month
            for (day in 1..monthLength) {
                val date = LocalDate.of(year, month, day)

                // Convert LocalDate to Date object
                val dateObject = Date.from(date.atStartOfDay().toInstant(ZoneOffset.UTC))
                dateList.add(dateObject)
            }
        }

        return dateList
    }

    val hijriMonths = arrayOf(
        "Muh", "Saf", "Ra-A", "Ra-T", "Jum-A", "Jum-T", "Raj", "Sha", "Ram", "Sha", "Dhu-Q", "Dhu-H"
    )

    data class DateInfo(val dayOfMonth: Int, val dayOfWeek: String, val previousMonth: Month, val hijriDayOfMonth: Int, val hijriMonth: String)
    fun generateDayAndWeekInfo(offsetDays: Long = -11): List<DateInfo> {
        val year = LocalDate.now().year
        val dateInfoList = mutableListOf<DateInfo>()
        var previousMonth: Month? = null
        var currentHijriMonthIndex = 0
        var hijriDayOfMonth = 1

        // Loop through each month
        for (month in Month.values()) {
            val monthLength = month.length(LocalDate.now().isLeapYear)

            // Loop through each day in the month
            for (day in 1..monthLength) {
                val date = LocalDate.of(year, month, day)

                // Calculate the approximate Hijri date using an offset
                val hijriDate = date.plusDays(offsetDays)

                // Get the day of the month and the day of the week
                val currentMonth = date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) // e.g., "Jan", "Feb"
                val dayOfMonth = date.dayOfMonth
                val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) // e.g., "Mon", "Tue"

                // Print month name if the month has changed
                if (previousMonth != month) {
                    previousMonth = month       // Update previousMonth to the current one
                }

                // Approximate Hijri date
                val hijriDayOfMonth1 = hijriDate.dayOfMonth
                val hijriMonth = hijriDate.month.getDisplayName(TextStyle.FULL, Locale("ar")) // Use Arabic month names for Hijri calendar

                dateInfoList.add(DateInfo(dayOfMonth, dayOfWeek, previousMonth, hijriDayOfMonth1, hijriMonths[currentHijriMonthIndex]))

                // Update Hijri day and month
                hijriDayOfMonth++
                if (hijriDayOfMonth > 30) {  // Assuming each Hijri month has 30 days for simplicity
                    hijriDayOfMonth = 1
                    currentHijriMonthIndex = (currentHijriMonthIndex + 1) % hijriMonths.size
                }
            }
        }

        return dateInfoList
    }
}