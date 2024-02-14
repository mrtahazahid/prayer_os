package com.iw.android.prayerapp.ui.main

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentPrayerBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.screens.MainActivity
import com.iw.android.prayerapp.utils.AppConstant.Companion.CURRENT_LATITUDE
import com.iw.android.prayerapp.utils.AppConstant.Companion.CURRENT_LONGITUDE
import com.iw.android.prayerapp.utils.AppConstant.Companion.getUserCurrentTime
import com.iw.android.prayerapp.utils.AppConstant.Companion.getUserCurrentTimeLong
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTime
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTimeInLong
import com.iw.android.prayerapp.utils.TinyDB

class PrayerFragment : BaseFragment(R.layout.fragment_prayer), View.OnClickListener {

    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private lateinit var tinyDB: TinyDB
    private lateinit var namazTimesList: ArrayList<String>
    private var namazNames = arrayOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrayerBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        (requireActivity() as MainActivity).showBottomSheet()
        tinyDB = TinyDB(context)
        binding.progressbar.visibility = View.VISIBLE
        namazTimesList = ArrayList()
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalStdlibApi::class)
    override fun initialize() {
        currentLatitude =
            if (tinyDB.getDouble(CURRENT_LATITUDE).isNaN() || tinyDB.getDouble(CURRENT_LATITUDE)
                    .toString().isEmpty()
            ) {
                0.0
            } else {
                tinyDB.getDouble(CURRENT_LATITUDE)
            }

        currentLongitude = if (tinyDB.getDouble(CURRENT_LONGITUDE).isNaN() || tinyDB.getDouble(CURRENT_LONGITUDE)
                .toString().isEmpty()
        ) {
            0.0
        } else {
            tinyDB.getDouble(CURRENT_LONGITUDE)
        }

        val getPrayerTime = getPrayTime(currentLatitude, currentLongitude)

        for (i in getPrayerTime) {
            namazTimesList.add(i)
        }

        binding.apply {
            for (i in 0..<namazTimesList.size) {
                Log.d("checkLatLon", "onCreateView: Current Time => $i => ${namazTimesList[i]}")

                if (getUserCurrentTime() == namazTimesList[i]) {
                    adhanTime.text = namazTimesList[i]
                    adhanName.text = namazNames[i]
                } else {
                    fajarTime.text = namazTimesList[0]
                    dhuhrTime.text = namazTimesList[1]
                    asrTime.text = namazTimesList[2]
                    ishaTime.text = namazTimesList[4]
                }
            }
        }
        upComingNamazTime()
    }

    override fun setObserver() {

    }

    override fun setOnClickListener() {
binding.upComingPrayerTimeView.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun upComingNamazTime() {
        val getPrayerTime = getPrayTimeInLong(currentLatitude, currentLongitude)
        val getCurrentTime = getUserCurrentTimeLong()

        Log.d(
            "checkLatLon",
            "upComingNamazTime: ${getPrayerTime.fajr.toEpochMilliseconds()} , $getCurrentTime"
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id){
            binding.upComingPrayerTimeView.id->{
                 findNavController().navigate(PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment("prayer"))
            }
        }
    }
}