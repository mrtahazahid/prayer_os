package com.iw.android.prayerapp.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AppConstant {
    companion object {
        @JvmStatic
        val CURRENT_LATITUDE = "lat"
        val CURRENT_LONGITUDE = "lon"

        @SuppressLint("SimpleDateFormat")
        fun getUserCurrentTime(): String {
            val sdf = SimpleDateFormat("hh:mm a")
            return sdf.format(Date())
        }

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SimpleDateFormat")
        fun getUserCurrentTimeLong(): Long {
            return Calendar.getInstance().toInstant().toEpochMilli()
        }
    }
}