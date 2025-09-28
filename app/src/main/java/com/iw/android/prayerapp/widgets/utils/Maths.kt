package com.iw.android.prayerapp.widgets.utils

import com.iw.android.prayerapp.extension.convertToFunTimeWithoutAMPM

fun buildUpcomingList(
    times: List<Pair<String, Long>>,
    current: String
): List<Pair<String, String>> {
    val index = times.indexOfFirst { it.first == current }
    if (index == -1) return emptyList()

    val next = mutableListOf<Pair<String, String>>()

    for (i in 1..4) {
        val pair = times[(index + i) % times.size]
        next.add(pair.first to convertToFunTimeWithoutAMPM(pair.second))
    }
    return next
}