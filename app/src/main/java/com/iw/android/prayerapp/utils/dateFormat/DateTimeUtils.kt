package com.iw.android.prayerapp.utils.dateFormat

import java.util.Calendar
import java.util.Date

class DateTimeUtils {
    fun calculateHoursAndMinutesBetween(startDate: Date, endDate: Date): Pair<Long, Long> {
        val totalTimeDifference = Math.abs(endDate.time - startDate.time)
        val currentTime = Calendar.getInstance().time
        val totalTimeDifferenceFromCurrentToEndTime = Math.abs(endDate.time - currentTime.time)

        return Pair(totalTimeDifference, totalTimeDifferenceFromCurrentToEndTime)
    }
}
