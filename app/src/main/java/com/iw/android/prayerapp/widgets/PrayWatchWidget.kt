package com.iw.android.prayerapp.widgets

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.response.WidgetData
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.extension.formatRemainingTime
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.dateFormat.convertTimeToMillis
import com.iw.android.prayerapp.utils.time.isTodayFriday
import com.iw.android.prayerapp.widgets.utils.Constants
import com.iw.android.prayerapp.widgets.utils.buildUpcomingList
import com.iw.android.prayerapp.widgets.utils.getProgressDrawable

class MyWidget : AppWidgetProvider() {

    private var prayerList :ArrayList<WidgetData> = arrayListOf()
    private var currentNamazName = ""
    private var countdownHandler: Handler? = null
    private var countdownRunnable: Runnable? = null

    override fun onDisabled(context: Context) {
        countdownHandler?.removeCallbacks(countdownRunnable ?: return)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        Log.d("onUpdate","called")
        val prefs = context.getSharedPreferences(Constants.MY_WIDGET_PREF, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(Constants.MY_WIDGET_PRAYER_LIST_PREF, null)
        val hijriDate = prefs.getString(Constants.MY_WIDGET_HIJRI_DATE_PREF, "N/A")

        if (!jsonString.isNullOrEmpty()) {
            val gson = Gson()
            val type = object : TypeToken<List<WidgetData>>() {}.type
            prayerList = gson.fromJson(jsonString, type)

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.pray_home_widget)
                upComingNamazTime(context,views,appWidgetId,hijriDate?:"N/A")
                val intent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                appWidgetManager.updateAppWidget(appWidgetId, views)

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun getTimeDifferenceToNextPrayer(): PrayerTime {

        val prayerTimes = listOf(
            PrayerTime(prayerList[0].namazName,prayerList[0].namazTime),
            PrayerTime(prayerList[1].namazName,prayerList[1].namazTime),
            PrayerTime(prayerList[2].namazName,prayerList[2].namazTime),
            PrayerTime(prayerList[3].namazName,prayerList[3].namazTime),
            PrayerTime(prayerList[4].namazName,prayerList[4].namazTime)
        )

        val currentTime = System.currentTimeMillis()

        // Find the next upcoming prayer
        val nextPrayer = prayerTimes.firstOrNull { it.currentNamazTime > currentTime }
        val currentTimeMillis1159 = convertTimeToMillis("11:59 PM")
        val currentTimeMillis12 = convertTimeToMillis("12:00 AM")

        return when {
            // If it's after Isha but before midnight
            currentTime >= prayerTimes.last().currentNamazTime && currentTime <= currentTimeMillis1159 -> {
                val ishaTime = prayerTimes.last().currentNamazTime
                val fajrTime =
                    prayerTimes.first().currentNamazTime + 24 * 60 * 60 * 1000 // next day

                val timeDifference = fajrTime - currentTime
                val totalTime = fajrTime - ishaTime

                PrayerTime(
                    "FJR",
                    prayerTimes.first().currentNamazTime,
                    timeDifference,
                    totalTime
                )
            }

            // If it's after midnight but before Fajr
            currentTime in (currentTimeMillis12 until prayerTimes.first().currentNamazTime) -> {
                val timeDifference = prayerTimes.first().currentNamazTime - currentTime
                val totalTime = prayerTimes.first().currentNamazTime - currentTimeMillis12

                PrayerTime(
                    "FJR",
                    prayerTimes.first().currentNamazTime,
                    timeDifference,
                    totalTime
                )
            }

            // Normal case: between two prayers
            nextPrayer != null -> {
                val nextIndex = prayerTimes.indexOf(nextPrayer)
                val prevIndex =
                    if (nextPrayer.currentNamazName == "FJR") prayerTimes.lastIndex else nextIndex - 1

                val previousPrayerTime =
                    if (prevIndex >= 0) prayerTimes[prevIndex].currentNamazTime else 0L

                val timeDifference = nextPrayer.currentNamazTime - currentTime
                val totalTime = nextPrayer.currentNamazTime - previousPrayerTime

                nextPrayer.copy(
                    timeDifference = timeDifference,
                    totalTime = totalTime
                )
            }

            // Fallback: no next prayer found (shouldn't happen)
            else -> PrayerTime(
                "FJR",
                prayerTimes.first().currentNamazTime,
                0L,
                0L
            )
        }
    }

    private fun startCountdown(
        context: Context,
        appWidgetId: Int,
        view: RemoteViews,
        timeDifferenceMillis: Long,
         totalTime: Long,
        hijriDate:String
    ) {
        countdownHandler?.removeCallbacks(countdownRunnable ?: Runnable { }) // Clear any previous countdown

        val appWidgetManager = AppWidgetManager.getInstance(context)
        var millisLeft = timeDifferenceMillis
        val percent = (millisLeft.toFloat() / totalTime.toFloat()) * 100

        val drawableResId = getProgressDrawable(percent.toInt())
        view.setImageViewResource(R.id.progress, drawableResId)

        countdownHandler = Handler(Looper.getMainLooper())
        countdownRunnable = object : Runnable {
            override fun run() {
                if (millisLeft <= 0) {
                    view.setTextViewText(R.id.textViewCurrentTime, "00:00")
                    appWidgetManager.updateAppWidget(appWidgetId, view)
                    upComingNamazTime(context,view,appWidgetId,hijriDate) // optionally refresh
                    return
                }

                val secondsRemaining = (millisLeft / 1000).toInt()
                val remainingTime = formatRemainingTime(secondsRemaining)
                view.setTextViewText(R.id.textViewCurrentTime, remainingTime)

                // Push the updated view to the widget
                appWidgetManager.updateAppWidget(appWidgetId, view)

                millisLeft -= 1000
                countdownHandler?.postDelayed(this, 1000)
            }
        }

        countdownHandler?.post(countdownRunnable!!)
    }

    @SuppressLint("SetTextI18n")
    private fun upComingNamazTime(context: Context,view: RemoteViews,id:Int,hijriDate:String) {
        val currentNamaz = getTimeDifferenceToNextPrayer()

        val namazTimes = listOf(
            prayerList[0].namazName to prayerList[0].namazTime,
            prayerList[1].namazName to prayerList[1].namazTime,
            prayerList[2].namazName to prayerList[2].namazTime,
            prayerList[3].namazName to prayerList[3].namazTime,
            prayerList[4].namazName to prayerList[4].namazTime
        )

        val adjustedNames = if (isTodayFriday()) {
            namazTimes.map { (name, time) ->
                if (name == "DHR") "JMH" to time else name to time
            }
        } else {
            namazTimes
        }

        val currentName = when {
            isTodayFriday() && currentNamaz.currentNamazName == "DHR" -> "JMH"
            else -> currentNamaz.currentNamazName
        }

        val upcoming = buildUpcomingList(adjustedNames, currentNamaz.currentNamazName)

        view.setTextViewText(
            R.id.textViewHijri,
            hijriDate
        )

        view.setTextViewText(
            R.id.textViewTopLeft,
            "${upcoming.getOrNull(0)?.first ?: ""}: ${upcoming.getOrNull(0)?.second ?: ""}"
        )

        view.setTextViewText(
            R.id.textViewTopRight,
            "${upcoming.getOrNull(1)?.first ?: ""}: ${upcoming.getOrNull(1)?.second ?: ""}"
        )

        view.setTextViewText(
            R.id.textViewBottomLeft,
            "${upcoming.getOrNull(2)?.first ?: ""}: ${upcoming.getOrNull(2)?.second ?: ""}"
        )

        view.setTextViewText(
            R.id.textViewBottomRight,
            "${upcoming.getOrNull(3)?.first ?: ""}: ${upcoming.getOrNull(3)?.second ?: ""}"
        )

        val currentNamazFullName = when(currentName){
            "FJR"-> "Fajr"
            "DHR"-> "Dhuhr"
            "ASR"-> "Asr"
            "MGB"-> "Maghrib"
            "ISH"-> "Isha"
            "JMH"-> "Jumuah"
            else -> "No Namaz"
        }

        view.setTextViewText(
            R.id.textViewNamazName,
            currentNamazFullName
        )

        if (currentNamaz.timeDifference > 0) {
            startCountdown(context,id,view,currentNamaz.timeDifference,currentNamaz.totalTime,hijriDate)
            currentNamazName = currentName
        } else {
            view.setTextViewText(
                R.id.textViewNamazName,
                "No Namaz Left"
            )
            view.setTextViewText(
                R.id.textViewCurrentTime,
                "00:00"
            )
        }
    }
}

