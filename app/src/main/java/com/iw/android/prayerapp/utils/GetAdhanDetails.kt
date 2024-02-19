package com.iw.android.prayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.Qibla
import com.batoulapps.adhan2.data.DateComponents
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.data.response.LocationData
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object GetAdhanDetails : AppCompatActivity() {



    @SuppressLint("SimpleDateFormat")
     fun getPrayTime(latitude: Double, longitude: Double,madhab: Madhab): ArrayList<String> {
        val coordinates = Coordinates(latitude, longitude);
        val timeZoneID = TimeZone.getDefault().id

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(Date())

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val date = DateComponents(year.toInt(), month.toInt(), day.toInt());
        val params = CalculationMethod.KARACHI.parameters.copy(madhab = madhab)



        val prayerTimes = PrayerTimes(coordinates, date, params)
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

    @SuppressLint("SimpleDateFormat")
    fun getPrayTimeInLong(latitude: Double, longitude: Double): PrayerTimes {
        val coordinates = Coordinates(latitude, longitude);

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(Date())

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val date = DateComponents(year.toInt(), month.toInt(), day.toInt());

        val params = CalculationMethod.KARACHI.parameters.copy(madhab = Madhab.HANAFI)

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