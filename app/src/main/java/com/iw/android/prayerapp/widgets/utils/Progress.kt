package com.iw.android.prayerapp.widgets.utils

import com.iw.android.prayerapp.R

fun getProgressDrawable(percentPassed: Int): Int {
    return when (percentPassed) {
        in 1..4 -> R.drawable.ic_percent_1
        in 5..9 -> R.drawable.ic_percent_5
        in 10..14 -> R.drawable.ic_percent_10
        in 15..19 -> R.drawable.ic_percent_15
        in 20..24 -> R.drawable.ic_percent_20
        in 25..29 -> R.drawable.ic_percent_25
        in 30..34 -> R.drawable.ic_percent_30
        in 35..39 -> R.drawable.ic_percent_35
        in 40..44 -> R.drawable.ic_percent_40
        in 45..49 -> R.drawable.ic_percent_45
        in 50..54 -> R.drawable.ic_percent_50
        in 55..59 -> R.drawable.ic_percent_55
        in 60..64 -> R.drawable.ic_percent_60
        in 65..69 -> R.drawable.ic_percent_65
        in 70..74 -> R.drawable.ic_percent_70
        in 75..79 -> R.drawable.ic_percent_75
        in 80..84 -> R.drawable.ic_percent_80
        in 85..89 -> R.drawable.ic_percent_85
        in 90..94 -> R.drawable.ic_percent_90
        in 95..99 -> R.drawable.ic_percent_95
        in 100..Int.MAX_VALUE -> R.drawable.ic_percent_100
        else -> R.drawable.ic_percent_100
    }
}