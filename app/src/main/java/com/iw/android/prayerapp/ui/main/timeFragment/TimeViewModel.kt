//package com.iw.android.prayerapp.fragments.timeFragment
//
//import android.content.Context
//import android.util.Log
//import androidx.lifecycle.ViewModel
//import com.iw.android.prayerapp.R
//import com.iw.android.prayerapp.data.PrayTime
//import com.iw.android.prayerapp.utils.AppConstant
//import com.iw.android.prayerapp.utils.GetAdhanDetails
//import com.iw.android.prayerapp.utils.TinyDB
//import com.iw.android.prayerapp.utils.TinyDb
//import dagger.hilt.android.lifecycle.HiltViewModel
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//
//@HiltViewModel
//class TimeViewModel @Inject constructor(private val repository: TimeRepository)  : ViewModel() {
//    var prayTimeArray = arrayListOf<PrayTime>()
//
//val tinyDB  = repository.tinyDB
//
//    private var currentLatitude = 0.0
//    private var currentLongitude = 0.0
//
//
//    init {
//
//        currentLatitude =
//            if (tinyDB.getDouble(AppConstant.CURRENT_LATITUDE).isNaN() || tinyDB.getDouble(
//                    AppConstant.CURRENT_LATITUDE
//                ).toString().isEmpty()
//            ) {
//                0.0
//            } else {
//                tinyDB.getDouble(AppConstant.CURRENT_LATITUDE)
//            }
//
//        currentLongitude =
//            if (tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE).isNaN() || tinyDB.getDouble(
//                    AppConstant.CURRENT_LONGITUDE
//                ).toString().isEmpty()
//            ) {
//                0.0
//            } else {
//                tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE)
//            }
//        val getPrayerTime = GetAdhanDetails.getPrayTime(currentLatitude, currentLongitude)
//
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Fajr",
//                getPrayerTime[0],
//            )
//        )
//
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Sunrise",
//                "6:58AM",
//            )
//        )
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Dhuhr",
//                getPrayerTime[1],
//            )
//        )
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Asr",
//                getPrayerTime[2],
//            )
//        )
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Maghrib",
//                getPrayerTime[3],
//            )
//        )
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Isha",
//                getPrayerTime[4],
//            )
//        )
//
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Midnight",
//                "11:42PM",
//            )
//        )
//
//        prayTimeArray.add(
//            PrayTime(
//                R.drawable.ic_mike,
//                "Last Third",
//                "1:40AM",
//            )
//        )
//
//    }
//
//
//}