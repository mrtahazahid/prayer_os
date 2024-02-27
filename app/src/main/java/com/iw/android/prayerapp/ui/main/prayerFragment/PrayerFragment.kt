package com.iw.android.prayerapp.ui.main.prayerFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
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
import com.iw.android.prayerapp.notificationService.Notification
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


    lateinit var notification: Notification

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
//        val hasWriteSettingsPermission = Settings.System.canWrite(context)
//        if (!hasWriteSettingsPermission) {
//            // You don't have permission, request it from the user
//            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
//            intent.data = Uri.parse("package:${requireContext().packageName}")
//            requireContext().startActivity(intent)
//        } else {
//            notification = Notification(requireContext())
//            notification.notify("user", "Title", "Message", "YourApp")
//        }


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
        val currentNamaz = getTimeDifferenceToNextPrayer()

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
                getTimeDifferenceToNextPrayer()
                // You can perform any additional actions when the prayer time arrives
            }
        }

        countDownTimer?.start()
    }

    private fun getTimeDifferenceToNextPrayer(): PrayerTime {

        val getPrayerTime = getPrayTimeInLong(currentLatitude, currentLongitude)
        prayerTimeList.add(PrayerTime("Fajr", getPrayerTime.fajr.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Dhuhr", getPrayerTime.dhuhr.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Asr", getPrayerTime.asr.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Maghrib", getPrayerTime.maghrib.toEpochMilliseconds()))
        prayerTimeList.add(PrayerTime("Isha", getPrayerTime.isha.toEpochMilliseconds()))

        val currentTimeMillis = System.currentTimeMillis()

        // Find the next prayer time
        var nextPrayerTimeIndex = 0
        while (nextPrayerTimeIndex < prayerTimeList.size && prayerTimeList[nextPrayerTimeIndex + 1].namazTime < currentTimeMillis) {
            nextPrayerTimeIndex += 2
        }

        // If all prayer times are before the current time, set the next prayer time to the last one
        if (nextPrayerTimeIndex == prayerTimeList.size) {
            nextPrayerTimeIndex -= 2
        }

        // Get the name and time of the next prayer
        val nextPrayerName = prayerTimeList[nextPrayerTimeIndex].namazName
        val nextPrayerTime = prayerTimeList[nextPrayerTimeIndex + 1].namazTime

        // Iterate through the array, move prayer times less than currentTimeMillis to last index,
        // and break the loop when a prayer time greater than currentTimeMillis is found
        var upperTimeIndex = prayerTimeList.size - 1
        for (i in 0 until prayerTimeList.size - 1) {
            if (prayerTimeList[i + 1].namazTime < currentTimeMillis) {
                // Move the prayer time less than currentTimeMillis to the last index
                val tempName = prayerTimeList[i].namazName
                val tempTime = prayerTimeList[i + 1].namazTime
                for (j in i until prayerTimeList.size - 2) {
                    prayerTimeList[j] = prayerTimeList[j + 2]
                }
                prayerTimeList[upperTimeIndex - 1] = PrayerTime(tempName, tempTime, 0)
            } else {
                // Break the loop when a prayer time greater than currentTimeMillis is found
                upperTimeIndex = i
                break
            }
        }

        val timeDifference = prayerTimeList[upperTimeIndex].namazTime - currentTimeMillis

        Log.d("nextPrayerName", nextPrayerName)
        Log.d("nextPrayerTime", nextPrayerTime.toString())
        Log.d("namazName", prayerTimeList[upperTimeIndex].namazName)
        Log.d("namazTime", prayerTimeList[upperTimeIndex].namazTime.toString())
        Log.d("prayerTimeList", prayerTimeList.toString())

        // Return the PrayerTime object with time difference
        return PrayerTime(
            prayerTimeList[upperTimeIndex].namazName,
            prayerTimeList[upperTimeIndex].namazTime,
            timeDifference
        )
    }

}