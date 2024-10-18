package com.iw.android.prayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.Qibla
import com.batoulapps.adhan2.SunnahTimes
import com.batoulapps.adhan2.data.DateComponents
import com.iw.android.prayerapp.data.response.LocationData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object GetAdhanDetails : AppCompatActivity() {
    @SuppressLint("SimpleDateFormat")
    fun getPrayTime(
        latitude: Double,
        longitude: Double,
        param: CalculationParameters,
        date: Date
    ): ArrayList<String> {
        val coordinates = Coordinates(latitude, longitude)
        val timeZoneID = TimeZone.getDefault().id

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(date)

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val dateComponents = DateComponents(year.toInt(), month.toInt(), day.toInt())

        Log.d("cordinate",coordinates.latitude.toString() + coordinates.longitude.toString()  )

        val prayerTimes = PrayerTimes(coordinates, dateComponents, param)
        val formatter = SimpleDateFormat("h:mm a")
        formatter.timeZone = TimeZone.getTimeZone(timeZoneID)




        val sunnahTimes = SunnahTimes(prayerTimes)

        Log.d(":check middleOfTheNight",convertToReadableForm(sunnahTimes.middleOfTheNight.toString()))

        return arrayListOf(
            formatter.format(Date(prayerTimes.fajr.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.sunrise.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.dhuhr.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.asr.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.maghrib.toEpochMilliseconds())),
            formatter.format(Date(prayerTimes.isha.toEpochMilliseconds())),
            formatter.format(Date(sunnahTimes.middleOfTheNight.toEpochMilliseconds())),
            formatter.format(Date(sunnahTimes.lastThirdOfTheNight.toEpochMilliseconds()))
        )
    }

    fun convertToReadableForm(isoDateTime: String): String {
        // Parse the ISO 8601 timestamp
        val instant = Instant.parse(isoDateTime)

        // Convert to the default time zone
        val defaultZoneId = ZoneId.systemDefault()
        val zonedDateTime = instant.atZone(defaultZoneId)

        // Format the date-time to "h:mm a"
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        return zonedDateTime.format(formatter)
    }
    fun calculatePrayerTimesForYear(
        latitude: Double,
        longitude: Double,
        param: CalculationParameters
    ): List<PrayerTimeData> {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val prayerTimeDataList = mutableListOf<PrayerTimeData>()

        val sdf = SimpleDateFormat("yyyy/M/dd")

        for (month in 0 until 12) {
            for (dayOfMonth in 1..calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) {
                calendar.set(year, month, dayOfMonth)
                val date = calendar.time
                val prayerTimes = getPrayTime(latitude, longitude, param, date)
                prayerTimeDataList.add(PrayerTimeData(date, prayerTimes))
            }
        }

        return prayerTimeDataList
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentPrayerTime(prayerTimes: List<Long>, currentTime: Long): Pair<String, Long>? {
        for (i in prayerTimes.indices) {
            val currentPrayerTime = prayerTimes[i]
            val nextPrayerTime =
                if (i < prayerTimes.size - 1) prayerTimes[i + 1] else prayerTimes[0]

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
    fun getPrayTimeWithNames(
        latitude: Double,
        longitude: Double,
        param: CalculationParameters,
        date: Date
    ): List<Pair<String, String>> {
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
        val formatter = SimpleDateFormat("h:mm a")
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
    fun getPrayTimeInLong(
        latitude: Double,
        longitude: Double,
        params: CalculationParameters
    ): PrayerTimes {
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

    @SuppressLint("SimpleDateFormat")
    fun getPrayTimeInLong(
        latitude: Double,
        longitude: Double,
        params: CalculationParameters,
        date: Date
    ): PrayerTimes {
        val coordinates = Coordinates(latitude, longitude);

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(date)

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
            val addresses = geocoder.getFromLocation(latitude, longitude, 2)

            if (addresses!!.isNotEmpty()) {
                val timeZone = TimeZone.getDefault().id
                val cityName = addresses[0].locality ?: extractCityName(addresses[1].locality)
                return LocationData(timeZone, cityName)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}

private suspend fun Geocoder.getAddress(
    latitude: Double,
    longitude: Double,
): Address? = withContext(Dispatchers.IO) {
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            suspendCoroutine { cont ->
                getFromLocation(latitude, longitude, 1) {
                    cont.resume(it.firstOrNull())
                }
            }
        } else {
            suspendCoroutine { cont ->
                @Suppress("DEPRECATION")
                val address = getFromLocation(latitude, longitude, 1)?.firstOrNull()
                cont.resume(address)
            }
        }
    } catch (e: Exception) {
        Log.d("Location Exception", e.toString())
        null
    }
}

fun extractCityName(subAdmin: String): String {
    val parts = subAdmin.split("=")
    if (parts.size == 2) {
        val cityNameWithSpace = parts[1].trim()
        val cityName = cityNameWithSpace.split(" ")[0] // Extracting only the first word
        return cityName
    }
    return "" // Return an empty string if the format is not as expected
}

data class PrayerTimeData(val date: Date, val prayerTimes: List<String>)