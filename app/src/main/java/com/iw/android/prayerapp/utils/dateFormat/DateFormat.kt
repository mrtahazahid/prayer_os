package com.iw.android.prayerapp.utils.dateFormat

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale


 fun formatDateWithCurrentTime(date: Date): String {
    val calendar = Calendar.getInstance()
    calendar.time = date

    // Get the current time components
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)

    // Set the current time to the date
    calendar.set(Calendar.HOUR_OF_DAY, currentHour)
    calendar.set(Calendar.MINUTE, currentMinute)

    // Format the date with time
    val dateFormat = SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.getDefault())
    return dateFormat.format(calendar.time)
}

 fun convertTimeToMillis(timeString: String): Long {
   val currentDate = Date()
   try {
      // Parse the time string by combining it with the current date
      val combinedDateTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
         .parse(
            "${
               SimpleDateFormat(
                  "yyyy-MM-dd",
                  Locale.getDefault()
               ).format(currentDate)
            } $timeString"
         )

      return combinedDateTime?.time ?: 0
   } catch (e: Exception) {
      e.printStackTrace()
   }

   return 0
}

 fun convertAndGetCurrentTimeMillis(): Long {
   return LocalDateTime.now()
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli()
}

fun getCurrentDate(): String {
    val currentDate = LocalDate.now()
    val formatter =
        DateTimeFormatter.ofPattern("dd MMM yyyy") // Customize the format as needed
    return currentDate.format(formatter)
}


 fun formattedDateForTimeScreen(offset: Int): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, offset)
    val targetDate: Date = calendar.time
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    return dateFormat.format(targetDate)
}


