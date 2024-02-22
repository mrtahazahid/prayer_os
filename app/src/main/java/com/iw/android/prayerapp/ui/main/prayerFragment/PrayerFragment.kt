package com.iw.android.prayerapp.ui.main.prayerFragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.databinding.FragmentPrayerBinding
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.extension.formatRemainingTime
import com.iw.android.prayerapp.extension.getIslamicDate
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
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
    private var prayerTimeList = arrayListOf<PrayerTime>()
    private var countDownTimer: CountDownTimer? = null

    val viewModel: PrayerViewModel by viewModels()

    private lateinit var namazTimesList: ArrayList<String>

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    @SuppressLint("SimpleDateFormat")
    override fun initialize() {
        currentLatitude = viewModel.userLatLong?.latitude ?: 0.0
        currentLongitude = viewModel.userLatLong?.longitude ?: 0.0
        binding.textViewTodayIslamicDate.text = getIslamicDate()

        val madhab = if (viewModel.getSavedPrayerJurisprudence.toInt() == 0) {
            Madhab.SHAFI
        } else {
            Madhab.HANAFI
        }
        val getPrayerTime = getPrayTime(currentLatitude, currentLongitude, madhab, Date())
        val timeZoneID = TimeZone.getDefault().id
        val formatter = SimpleDateFormat("hh:mm a")
        formatter.timeZone = TimeZone.getTimeZone(timeZoneID)

        for (i in getPrayerTime) {
            namazTimesList.add(i)
        }
        upComingNamazTime()
    }

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.upComingPrayerTimeView.setOnClickListener(this)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer?.cancel()
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
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


    @SuppressLint("SetTextI18n")
    private fun upComingNamazTime() {
        val getPrayerTime = getPrayTimeInLong(currentLatitude, currentLongitude)
        prayerTimeList.add(PrayerTime("Fajr", getPrayerTime.fajr.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Dhuhr", getPrayerTime.dhuhr.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Asr", getPrayerTime.asr.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Maghrib", getPrayerTime.maghrib.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Isha", getPrayerTime.isha.toEpochMilliseconds()))
        prayerTimeList.asSequence()

        val currentNamaz = getTimeDifferenceToNextPrayer(System.currentTimeMillis(), prayerTimeList)

        when (currentNamaz.namazName) {
            "Fajr" -> {
                binding.textViewFifthNamaz.text =
                    "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                binding.textViewSecondNamaz.text =
                    "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                binding.textViewThirdNamaz.text =
                    "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                binding.textViewFourthNamaz.text =
                    "Isha: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                binding.textViewCurrentNamazName.text = "Fajr"
                binding.textViewCurrentNamazTime.text = convertToFunTime(currentNamaz.namazTime)
            }

            "Dhuhr" -> {
                binding.textViewFifthNamaz.text =
                    "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                binding.textViewSecondNamaz.text =
                    "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                binding.textViewThirdNamaz.text =
                    "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                binding.textViewFourthNamaz.text =
                    "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                binding.textViewCurrentNamazName.text = "Dhuhr"
                binding.textViewCurrentNamazTime.text = convertToFunTime(currentNamaz.namazTime)
            }

            "Asr" -> {
                binding.textViewFifthNamaz.text =
                    "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                binding.textViewSecondNamaz.text =
                    "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                binding.textViewThirdNamaz.text =
                    "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                binding.textViewFourthNamaz.text =
                    "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                binding.textViewCurrentNamazName.text = "Asr"
                binding.textViewCurrentNamazTime.text = convertToFunTime(currentNamaz.namazTime)
            }

            "Maghrib" -> {
                binding.textViewFifthNamaz.text =
                    "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                binding.textViewSecondNamaz.text =
                    "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                binding.textViewThirdNamaz.text =
                    "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                binding.textViewFourthNamaz.text =
                    "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                binding.textViewCurrentNamazName.text = "Maghrib"
                binding.textViewCurrentNamazTime.text = convertToFunTime(currentNamaz.namazTime)
            }

            "Isha" -> {
                binding.textViewFifthNamaz.text =
                    "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                binding.textViewSecondNamaz.text =
                    "Fajr: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                binding.textViewThirdNamaz.text =
                    "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                binding.textViewFourthNamaz.text =
                    "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                binding.textViewCurrentNamazName.text = "Isha"
                binding.textViewCurrentNamazTime.text = convertToFunTime(currentNamaz.namazTime)
            }
        }

        startCountdown(currentNamaz.timeDifference)
    }

    private fun startCountdown(timeDifferenceMillis: Long) {
        countDownTimer = object : CountDownTimer(timeDifferenceMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()

                // Update progress bar and display remaining time
                binding.progressbar.max = timeDifferenceMillis.toInt()
                binding.progressbar.progress = millisUntilFinished.toInt()

                val remainingTime = formatRemainingTime(secondsRemaining)
                binding.textViewRemainingTime.text = remainingTime
            }

            override fun onFinish() {
                // Prayer time has arrived, handle accordingly
                binding.textViewRemainingTime.text = getString(R.string.prayer_time)
                // You can perform any additional actions when the prayer time arrives
            }
        }

        countDownTimer?.start()
    }

    private fun getTimeDifferenceToNextPrayer(
        currentTimeMillis: Long,
        prayerTimesMillis: ArrayList<PrayerTime>
    ): PrayerTime {
        var upperTimeIndex = prayerTimesMillis.size - 1

        // Check if current time is greater than the upper array time
        if (currentTimeMillis > prayerTimesMillis[upperTimeIndex].namazTime) {
            upperTimeIndex = prayerTimesMillis.size - 1
        } else {
            // Find the upper time in the array
            for (i in 0 until prayerTimesMillis.size - 1) {
                if (currentTimeMillis < prayerTimesMillis[i + 1].namazTime) {
                    upperTimeIndex = i
                    break
                }
            }
        }

        val timeDifference = prayerTimesMillis[upperTimeIndex].namazTime - currentTimeMillis
        return PrayerTime(
            prayerTimesMillis[upperTimeIndex].namazName,
            prayerTimesMillis[upperTimeIndex].namazTime,
            timeDifference
        )
    }


}