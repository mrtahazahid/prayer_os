package com.iw.android.prayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.Qibla
import com.batoulapps.adhan2.data.DateComponents
import com.iw.android.prayerapp.data.response.LocationData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object GetAdhanDetails : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    fun getPrayTime(latitude: Double, longitude: Double, param: CalculationParameters, date: Date): ArrayList<String> {
        val coordinates = Coordinates(latitude, longitude)
        val timeZoneID = TimeZone.getDefault().id

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(date)

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val dateComponents = DateComponents(year.toInt(), month.toInt(), day.toInt())

        val prayerTimes = PrayerTimes(coordinates, dateComponents, param)
        val formatter = SimpleDateFormat("hh:mm a")
        formatter.timeZone = TimeZone.getTimeZone(timeZoneID)

        return arrayListOf(
            formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.sunrise.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.asr.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.isha.toEpochMilliseconds()))
        )
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentPrayerTime(prayerTimes: List<Long>, currentTime: Long): Pair<String, Long>? {
        for (i in prayerTimes.indices) {
            val currentPrayerTime = prayerTimes[i]
            val nextPrayerTime = if (i < prayerTimes.size - 1) prayerTimes[i + 1] else prayerTimes[0]

            if (currentTime < nextPrayerTime) {
                val remainingTime = nextPrayerTime - currentTime
                val formattedTime = formatRemainingTime(remainingTime)
                return Pair(formattedTime, remainingTime)
            }
        }

        return null
    }

    private fun formatRemainingTime(millisRemaining: Long): String {
        val secondsRemaining = (millisRemaining / 1000).toInt()
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    @SuppressLint("SimpleDateFormat")
    fun getPrayTimeWithNames(latitude: Double, longitude: Double, param: CalculationParameters, date: Date): List<Pair<String, String>> {
        val coordinates = Coordinates(latitude, longitude)
        val timeZoneID = TimeZone.getDefault().id

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(date)

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val dateComponents = DateComponents(year.toInt(), month.toInt(), day.toInt())

        val prayerTimes = PrayerTimes(coordinates, dateComponents, param)
        val formatter = SimpleDateFormat("hh:mm a")
        formatter.timeZone = TimeZone.getTimeZone(timeZoneID)

        val prayerNames = listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")

        val prayerTimesWithNames = prayerNames.map { prayerName ->
            val prayerTimeInstant = when (prayerName) {
                "Fajr" -> prayerTimes.fajr
                "Sunrise" -> prayerTimes.sunrise
                "Dhuhr" -> prayerTimes.dhuhr
                "Asr" -> prayerTimes.asr
                "Maghrib" -> prayerTimes.maghrib
                "Isha" -> prayerTimes.isha
                else -> throw IllegalArgumentException("Invalid prayer name: $prayerName")
            }

            val formattedTime = formatter.format(Date(prayerTimeInstant.toEpochMilliseconds()))
            Pair(prayerName, formattedTime)
        }

        return prayerTimesWithNames
    }

    @SuppressLint("SimpleDateFormat")
    fun getPrayTimeInLong(latitude: Double, longitude: Double,params: CalculationParameters): PrayerTimes {
        val coordinates = Coordinates(latitude, longitude);

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(Date())

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val date = DateComponents(year.toInt(), month.toInt(), day.toInt());



        return PrayerTimes(coordinates, date, params)
    }

    fun getQiblaDirection(latitude: Double, longitude: Double): Qibla {
        val coordinates = Coordinates(latitude, longitude)
        return Qibla(coordinates)
    }


    fun getTimeZoneAndCity(context: Context, latitude: Double, longitude: Double): LocationData? {
        val geocoder = Geocoder(context, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses!!.isNotEmpty()) {
                val timeZone = TimeZone.getDefault().id
                val cityName = addresses[0].locality ?: "Unknown City"

                return LocationData(timeZone, cityName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}