package com.iw.android.prayerapp.ui.main.moreFragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import com.batoulapps.adhan2.CalculationParameters
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.extension.convertToFunDateTime
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.extension.getAndroidVersion
import com.iw.android.prayerapp.extension.getDeviceName
import com.iw.android.prayerapp.utils.GetAdhanDetails
import java.util.Locale

suspend fun sendUserToGmail(
    context: Context,
    lat: Double,
    lng: Double,
    method: CalculationParameters,
    methodInt: Int,
    madhab: Int
) {
    val location = GetAdhanDetails.getTimeZoneAndCity(
        context, lat,
        lng
    )
    val methodsList = ArrayList<String>().apply {
        add("Muslim World League")
        add("Islamic Society of N.America (ISNA)")
        add("Moonsighting Committee")
        add("Egyptian General Authority of Survey")
        add("Algerian Ministry of Awqaf and Religious Affairs")
        add("Tunisian Ministry of Religious Affairs")
        add("London Unified Prayer Timetable")
        add("Umm Al-Quran University")
        add("Umm Al-Qura University, Makkah")
        add("Authority of Dubai - UAE")
        add("Kuwait")
        add("Singapore")
        add("Other")
        add("Authority of Qatar")
        add("Karachi")
    }
    val juriList = arrayListOf<String>().apply {
        add("Standard")
        add("Hanafi")
    }
    val getPrayerTime =
        GetAdhanDetails.getPrayTimeInLong(lat, lng, method)
    try {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            val recipients = arrayOf("help@praywatch.app")
            intent.putExtra(Intent.EXTRA_EMAIL, recipients)
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Android: Feedback For " + context.getString(R.string.app_name)
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                """Assalamu Alaikum, please specify your question, feature request, or bug report:




===================
Version: 1.8

-------------------
Phone Information
-------------------
Device: ${getDeviceName()}
Version: Android ${getAndroidVersion()}
Background: true

-------------------
Notifications
-------------------
Status: authorized
Pending: 49
Tray: 2

-------------------
Prayer Times
-------------------
Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}
Sunrise: ${convertToFunTime(getPrayerTime.sunrise.toEpochMilliseconds())}
Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}
Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}
Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}
Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}

-------------------
Prayer Parameters
-------------------
Method: ${methodsList[methodInt]}
Jurisprudence: ${juriList[madhab]}
Auto GPS: true
Time zone: ${location?.timeZone}
Locale: ${Locale.getDefault()}
Region: ${location?.timeZone?.substringAfterLast("/")}
Hijri offset: 0
Adjustments: none
Coordinates: true
Cache: ${convertToFunDateTime(getCacheDirectoryLastModified(context))}
     """.trimIndent()
            )
            intent.type = "text/html"
            intent.setPackage("com.google.android.gm")
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    } catch (ignored: Exception) {
    }
}

fun getCacheDirectoryLastModified(context: Context): Long {
    val cacheDir = context.cacheDir
    return cacheDir.lastModified()
}

fun shareApp(context: Context) {
    val packageName = context.packageName
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Check out this app")
        putExtra(
            Intent.EXTRA_TEXT,
            "Hey! Check out this Islamic app:\nhttps://play.google.com/store/apps/details?id=$packageName"
        )
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share app via"))
}

fun openCustomTab(context: Context, url: String?) {
    val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
    val customTabsIntent: CustomTabsIntent = builder.build()
    customTabsIntent.launchUrl(context, Uri.parse(url))
}