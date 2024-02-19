package com.iw.android.prayerapp.ui.main.prayerFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentPrayerBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.AppConstant.Companion.getUserCurrentTime
import com.iw.android.prayerapp.utils.AppConstant.Companion.getUserCurrentTimeLong
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTime
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTimeInLong
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class PrayerFragment : BaseFragment(R.layout.fragment_prayer), View.OnClickListener {

    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    val viewModel: PrayerViewModel by viewModels()


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
        currentLatitude = viewModel.userLatLong?.latitude ?: 0.0
        currentLongitude = viewModel.userLatLong?.longitude ?: 0.0

        val madhab = if (viewModel.getSavedPrayerJurisprudence.toInt() == 0) {
            Madhab.SHAFI
        } else {
            Madhab.HANAFI
        }
        val getPrayerTime = getPrayTime(currentLatitude, currentLongitude, madhab)

        val coordinates = Coordinates(currentLatitude, currentLongitude)
        val timeZoneID = TimeZone.getDefault().id

        val sdf = SimpleDateFormat("yyyy/M/dd")
        val currentDate = sdf.format(Date())

        var splitDate = currentDate.split("/")
        var year = splitDate[0]
        var month = splitDate[1]
        var day = splitDate[2]

        val date = DateComponents(year.toInt(), month.toInt(), day.toInt());


        val params = CalculationMethod.KARACHI.parameters.copy(madhab = Madhab.HANAFI)

        val prayerTimes = PrayerTimes(coordinates, date, params)

        val formatter = SimpleDateFormat("hh:mm a")
        formatter.timeZone = TimeZone.getTimeZone(timeZoneID)

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
                    dhuhrTime.text = namazTimesList[2]
                    asrTime.text = namazTimesList[3]
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
        when (v?.id) {
            binding.upComingPrayerTimeView.id -> {
                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(
                        "prayer"
                    )
                )
            }
        }
    }
}