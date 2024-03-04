package com.iw.android.prayerapp.ui.main.prayerFragment

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
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
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class PrayerFragment : BaseFragment(R.layout.fragment_prayer), View.OnClickListener {


    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var countDownTimer: CountDownTimer? = null

    val viewModel: PrayerViewModel by viewModels()
    private var currentNamazName = ""

    lateinit var notifications: Notification

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
        notifications = Notification(requireContext())
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
        notifications.notify(currentNamazName)
    }

    override fun setObserver() {

    }

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

        when (currentNamaz.currentNamazName) {
            "Fajr" -> {
                binding.textViewFifthNamaz.text =
                    "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                binding.textViewSecondNamaz.text =
                    "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                binding.textViewThirdNamaz.text =
                    "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                binding.textViewFourthNamaz.text =
                    "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                binding.textViewCurrentNamazName.text = "Fajr"
                binding.textViewCurrentNamazTime.text =
                    convertToFunTime(currentNamaz.currentNamazTime)
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
                binding.textViewCurrentNamazTime.text =
                    convertToFunTime(currentNamaz.currentNamazTime)
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
                binding.textViewCurrentNamazTime.text =
                    convertToFunTime(currentNamaz.currentNamazTime)
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
                binding.textViewCurrentNamazTime.text =
                    convertToFunTime(currentNamaz.currentNamazTime)
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
                binding.textViewCurrentNamazTime.text =
                    convertToFunTime(currentNamaz.currentNamazTime)
            }
        }

        startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
    }

    private fun startCountdown(timeDifferenceMillis: Long, totalTime: Long) {
        countDownTimer = object : CountDownTimer(timeDifferenceMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                binding.progressbar.max = totalTime.toInt()
                binding.progressbar.progress = millisUntilFinished.toInt()

                val remainingTime = formatRemainingTime(secondsRemaining)
                binding.textViewRemainingTime.text = remainingTime
            }

            override fun onFinish() {
                notifications.notify(currentNamazName)
                // Prayer time has arrived, handle accordingly
                getTimeDifferenceToNextPrayer()

                //
            }
        }

        countDownTimer?.start()
    }




    private fun getTimeDifferenceToNextPrayer(): PrayerTime {
        val getPrayerTime = getPrayTimeInLong(currentLatitude, currentLongitude)

        val prayerTimeList = listOf(
            PrayerTime(
                "Fajr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Dhuhr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Asr",
                convertTimeToMillis(convertToFunTime(getPrayerTime.asr.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Maghrib",
                convertTimeToMillis(convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds()))
            ),
            PrayerTime(
                "Isha",
                convertTimeToMillis(convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()))
            )
        )

        val currentTimeMillis = convertAndGetCurrentTimeMillis()

        Log.d("fajr", getPrayerTime.fajr.toEpochMilliseconds().toString())
        Log.d("currentTimeMillis", currentTimeMillis.toString())

        // Iterate through the array to find the next prayer time
        var nextPrayerTimeIndex = 0
        var currentPrayerTimeIndex = 0
        var previousPrayerTimeIndex = 0
        for (i in prayerTimeList.indices) {
            if (prayerTimeList[i].currentNamazTime > currentTimeMillis) {
                if (prayerTimeList[i].currentNamazName == "Fajr") {
                    previousPrayerTimeIndex = prayerTimeList.size - 1
                    currentPrayerTimeIndex = i
                    nextPrayerTimeIndex = i + 1
                } else {
                    previousPrayerTimeIndex = i - 1
                    currentPrayerTimeIndex = i
                    nextPrayerTimeIndex = i + 1
                }
                break
            }
        }

        // Calculate the time difference between the current time and the next prayer time
        val timeDifferenceMillis = prayerTimeList[currentPrayerTimeIndex].currentNamazTime - currentTimeMillis


        // Calculate the total time difference between the previous and up-coming prayer
        val totalDifferenceMillis =  if(prayerTimeList[currentPrayerTimeIndex].currentNamazName == "Fajr"){
            prayerTimeList[previousPrayerTimeIndex].currentNamazTime - prayerTimeList[nextPrayerTimeIndex].currentNamazTime
        }else{
            prayerTimeList[nextPrayerTimeIndex].currentNamazTime - prayerTimeList[previousPrayerTimeIndex].currentNamazTime
        }
        currentNamazName = prayerTimeList[currentPrayerTimeIndex].currentNamazName
        // Return the PrayerTime object with time differences
        return PrayerTime(
            prayerTimeList[currentPrayerTimeIndex].currentNamazName,
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime,
            timeDifferenceMillis,
            totalDifferenceMillis
        )
    }



    fun convertAndGetCurrentTimeMillis(): Long {
        return LocalDateTime.now()
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
    fun convertTimeToMillis(timeString: String): Long {
        val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Set the date to a fixed value (e.g., today's date) to avoid unexpected behavior
        val currentDate = Date()

        try {
            // Parse the time string by combining it with the current date
            val combinedDateTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
                .parse(
                    "${
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(currentDate)
                    } $timeString"
                )

            return combinedDateTime?.time ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }


}