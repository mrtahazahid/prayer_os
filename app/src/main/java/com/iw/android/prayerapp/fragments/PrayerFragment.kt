package com.iw.android.prayerapp.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentPrayerBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.utils.AppConstant.Companion.CURRENT_LATITUDE
import com.iw.android.prayerapp.utils.AppConstant.Companion.CURRENT_LONGITUDE
import com.iw.android.prayerapp.utils.AppConstant.Companion.getUserCurrentTime
import com.iw.android.prayerapp.utils.AppConstant.Companion.getUserCurrentTimeLong
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTime
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTimeInLong
import com.iw.android.prayerapp.utils.TinyDB

class PrayerFragment : Fragment() {

    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private lateinit var tinyDB: TinyDB
    private lateinit var namazTimesList: ArrayList<String>
    private var namazNames =  arrayOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrayerBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)

        tinyDB = TinyDB(context)
        binding.progressbar.visibility = View.VISIBLE
        namazTimesList = ArrayList()

        if (tinyDB.getDouble(CURRENT_LATITUDE).isNaN() || tinyDB.getDouble(CURRENT_LATITUDE).toString().isNullOrEmpty()) {
            currentLatitude = 0.0
        } else {
            currentLatitude = tinyDB.getDouble(CURRENT_LATITUDE)
        }

        if (tinyDB.getDouble(CURRENT_LONGITUDE).isNaN() || tinyDB.getDouble(CURRENT_LONGITUDE).toString().isNullOrEmpty()) {
            currentLongitude = 0.0
        } else {
            currentLongitude = tinyDB.getDouble(CURRENT_LONGITUDE)
        }

        var getPrayerTime = getPrayTime(currentLatitude, currentLongitude)

        for (i in getPrayerTime) {
            namazTimesList.add(i)
        }

        binding.apply {
            for (i in 0..<namazTimesList.size) {
                Log.d("checkLatLon", "onCreateView: Current Time => ${i} => ${namazTimesList[i]}")

                if (getUserCurrentTime() == namazTimesList[i]) {
                    adhanTime.text = namazTimesList[i]
                    adhanName.text = namazNames[i]
                }
                else{
                    fajarTime.text = namazTimesList[0]
                    dhuhrTime.text = namazTimesList[1]
                    asrTime.text = namazTimesList[2]
                    ishaTime.text = namazTimesList[4]
                }
            }
        }
        upComingNamazTime()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun upComingNamazTime() {
        val getPrayerTime = getPrayTimeInLong(currentLatitude,currentLongitude)
        val getCurrentTime = getUserCurrentTimeLong()

        Log.d("checkLatLon", "upComingNamazTime: ${getPrayerTime.fajr.toEpochMilliseconds()} , ${getCurrentTime}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}