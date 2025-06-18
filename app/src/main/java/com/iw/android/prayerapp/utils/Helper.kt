package com.iw.android.prayerapp.utils

import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale

object Helper {


    fun generateDatesAsDateObjects(): List<Date> {
        val startDate = LocalDate.now()
        val endDate = startDate.plusYears(1)
        val dateList = mutableListOf<Date>()

        var currentDate = startDate
        while (currentDate.isBefore(endDate)) {
            val dateObject = Date.from(currentDate.atStartOfDay(ZoneOffset.UTC).toInstant())
            dateList.add(dateObject)
            currentDate = currentDate.plusDays(1)
        }

        return dateList
    }

    val hijriMonths = arrayOf(
        "Muh", "Saf", "Ra-A", "Ra-T", "Jum-A", "Jum-T", "Raj", "Sha", "Ram", "Sha", "Dhu-Q", "Dhu-H"
    )

    data class DateInfo(
        val dayOfMonth: Int,
        val dayOfWeek: String,
        val previousMonth: Month,
        val hijriDayOfMonth: Int,
        val hijriMonth: String
    )

    fun generateDayAndWeekInfo(offsetDays: Long = -11): List<DateInfo> {
        val today = LocalDate.now()
        val endDate = today.plusYears(1)
        val dateInfoList = mutableListOf<DateInfo>()

        var previousMonth: Month? = null
        var currentHijriMonthIndex = 0
        var hijriDayOfMonth = 1

        var currentDate = today
        while (currentDate.isBefore(endDate)) {
            val hijriDate = currentDate.plusDays(offsetDays)

            val currentMonth = currentDate.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val dayOfMonth = currentDate.dayOfMonth
            val dayOfWeek = currentDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

            // Month change tracking (optional if needed)
            if (previousMonth != currentDate.month) {
                previousMonth = currentDate.month
            }

            val hijriDay = hijriDate.dayOfMonth
            val hijriMonth = hijriMonths[currentHijriMonthIndex]

            dateInfoList.add(
                DateInfo(
                    dayOfMonth = dayOfMonth,
                    dayOfWeek = dayOfWeek,
                    previousMonth = previousMonth!!,
                    hijriDayOfMonth = hijriDay,
                    hijriMonth = hijriMonth
                )
            )

            hijriDayOfMonth++
            if (hijriDayOfMonth > 30) {
                hijriDayOfMonth = 1
                currentHijriMonthIndex = (currentHijriMonthIndex + 1) % hijriMonths.size
            }

            currentDate = currentDate.plusDays(1)
        }

        return dateInfoList
    }
}