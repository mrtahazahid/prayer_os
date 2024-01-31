package com.iw.android.prayerapp.fragments.timeFragment

import androidx.lifecycle.ViewModel
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.data.PrayTime
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeViewModel@Inject constructor()  : ViewModel() {
    var prayTimeArray = arrayListOf<PrayTime>()

    init {
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Fajr",
                "5:37AM",
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Sunrise",
                "6:58AM",
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Dhuhr",
                "12:25PM",
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Asr",
                "3:21PM",
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Maghrib",
                "5:45PM",
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Isha",
                "7:02PM",
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Midnight",
                "11:42PM",
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Last Third",
                "1:40AM",
            )
        )

    }


}