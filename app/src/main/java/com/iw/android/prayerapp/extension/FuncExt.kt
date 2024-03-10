package com.iw.android.prayerapp.extension

import android.annotation.SuppressLint
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.iw.android.prayerapp.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun AppCompatActivity.setStatusBarWithBlackIcon(@ColorRes color: Int) {
    this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    this.window.statusBarColor = ContextCompat.getColor(this, color)
}
fun Fragment.setStatusBarWithBlackIcon(@ColorRes color: Int) {
    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    requireActivity().window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.black)
}

 fun formatRemainingTime(secondsRemaining: Int): String {
    val hours = TimeUnit.SECONDS.toHours(secondsRemaining.toLong())
    val minutes = TimeUnit.SECONDS.toMinutes(secondsRemaining.toLong()) % 60
    val seconds = secondsRemaining % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
@SuppressLint("SimpleDateFormat")
 fun convertToFunTime(timestamp: Long): String {
    val date = Date(timestamp)
    val sdf = SimpleDateFormat("hh:mm a")
    return sdf.format(date)


}

 fun getCurrentDateFormatted(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    return dateFormat.format(calendar.time)
}

 fun getIslamicDate(): String {
    val sdf = SimpleDateFormat("yyyy/M/dd")
    val currentDate = sdf.format(Date())

    val splitDate = currentDate.split("/")
    val year = splitDate[0].toInt()
    val month = splitDate[1].toInt()
    val day = splitDate[2].toInt()

    val gregorianDate: LocalDate = LocalDate.of(year, month, day)
    val hijrahDate = HijrahDate.from(gregorianDate)

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
    val formattedHijrahDate = hijrahDate.format(formatter)

    return formattedHijrahDate.replace(" ", " ") // Customize space as needed
}

fun getIslamicDateByOffSet(offset: Int): String {
    val sdf = SimpleDateFormat("yyyy/M/dd")
    val currentDate = sdf.format(Date())

    val splitDate = currentDate.split("/")
    val year = splitDate[0].toInt()
    val month = splitDate[1].toInt()
    val day = splitDate[2].toInt()

    val gregorianDate: LocalDate = LocalDate.of(year, month, day)

    // Add offset days to the current date
    val offsetGregorianDate = gregorianDate.plusDays(offset.toLong())

    val hijrahDate = HijrahDate.from(offsetGregorianDate)

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
    val formattedHijrahDate = hijrahDate.format(formatter)

    return formattedHijrahDate.replace(" ", " ") // Customize space as needed
}
