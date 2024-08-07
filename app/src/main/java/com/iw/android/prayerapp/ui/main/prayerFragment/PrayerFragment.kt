package com.iw.android.prayerapp.ui.main.prayerFragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.databinding.DialogExitBinding
import com.iw.android.prayerapp.databinding.FragmentPrayerBinding
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.extension.formatRemainingTime
import com.iw.android.prayerapp.extension.getIslamicDateByOffSet
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.GetAdhanDetails.getPrayTimeInLong
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrayerFragment : BaseFragment(R.layout.fragment_prayer), View.OnClickListener {


    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var notificationReceiver: BroadcastReceiver

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var countDownTimer: CountDownTimer? = null


    val viewModel: PrayerViewModel by viewModels()
    private var currentNamazName = ""

    private lateinit var namazTimesList: ArrayList<String>
    private var method: CalculationParameters? = null
    private var madhab: Madhab? = null
    private var isOffsetViewShow = false

    private var dialogExitBinding: DialogExitBinding? = null
    private var dialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrayerBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        (requireActivity() as MainActivity).showBottomSheet()
        binding.progressbar.visibility = View.VISIBLE


        notificationReceiver = NotificationReceiver()

        // Register the receiver for local broadcasts
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            notificationReceiver,
            IntentFilter("com.iw.android.prayerapp.NOTIFICATION")
        )


        namazTimesList = ArrayList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
        setOnBackPressedListener()
    }


    @SuppressLint("SimpleDateFormat")
    override fun initialize() {
        getMethod()

        binding.progressbar.apply {
            // or with gradient
            progressBarColorStart = resources.getColor(R.color.app_green)

            progressBarColorEnd = resources.getColor(R.color.small_icon3)
            progressBarColorDirection = CircularProgressBar.GradientDirection.RIGHT_TO_LEFT

            // Set background ProgressBar Color
            backgroundProgressBarColor = resources.getColor(R.color.progress_bg)

            // Set Width
            progressBarWidth = 21f // in DP
            backgroundProgressBarWidth = 21f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT

        }


        val location = GetAdhanDetails.getTimeZoneAndCity(
            requireContext(), viewModel.userLatLong?.latitude ?: 0.0,
            viewModel.userLatLong?.longitude ?: 0.0
        )
        binding.textViewCity.text = location?.city ?: "City"

        currentLatitude = viewModel.userLatLong?.latitude ?: 0.0
        currentLongitude = viewModel.userLatLong?.longitude ?: 0.0


        val getPrayerTime =
            getPrayTimeInLong(currentLatitude, currentLongitude, method!!)

        binding.textViewTodayIslamicDate.text =
            if (convertToFunTime(System.currentTimeMillis()) > convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())) {
                getIslamicDateByOffSet(1)
            } else {
                getIslamicDateByOffSet(0)
            }
        upComingNamazTime()
    }

    override fun setObserver() {
    }


    override fun setOnClickListener() {
        binding.upComingPrayerTimeView.setOnClickListener(this)
        binding.textViewFifthNamaz.setOnClickListener(this)
        binding.textViewSecondNamaz.setOnClickListener(this)
        binding.textViewThirdNamaz.setOnClickListener(this)
        binding.textViewFourthNamaz.setOnClickListener(this)
        binding.topViewText.setOnClickListener(this)
        binding.incrementPlusTwoTextView.setOnClickListener(this)
        binding.incrementPlusOneTextView.setOnClickListener(this)
        binding.incrementPlusZeroTextView.setOnClickListener(this)
        binding.incrementMinusOneTextView.setOnClickListener(this)
        binding.incrementMinusTwoTextView.setOnClickListener(this)
        binding.mainView.setOnClickListener(this)
        binding.imageViewPause.setOnClickListener {
            notifications.stopPrayer()
            binding.imageViewPause.gone()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        countDownTimer?.cancel()
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.upComingPrayerTimeView.id -> {
                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(
                        binding.textViewCurrentNamazName.text.toString()
                    )
                )
            }


            binding.textViewFifthNamaz.id -> {
                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(
                        binding.textViewFifthNamaz.text.toString()
                    )
                )
            }


            binding.textViewFourthNamaz.id -> {
                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(
                        binding.textViewFourthNamaz.text.toString()
                    )
                )
            }


            binding.textViewThirdNamaz.id -> {
                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(
                        binding.textViewThirdNamaz.text.toString()
                    )
                )
            }


            binding.textViewSecondNamaz.id -> {
                findNavController().navigate(
                    PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(
                        binding.textViewSecondNamaz.text.toString()
                    )
                )
            }

            binding.topViewText.id -> {
                if (!isOffsetViewShow) {
                    isOffsetViewShow = true
                    binding.cardViewOffSet.show()
                } else {
                    isOffsetViewShow = false
                    binding.cardViewOffSet.gone()
                }
            }

            binding.mainView.id -> {
                if (isOffsetViewShow) {
                    isOffsetViewShow = false
                    binding.cardViewOffSet.gone()
                }
            }

            binding.incrementPlusTwoTextView.id -> {
                binding.textViewTodayIslamicDate.text = getIslamicDateByOffSet(2)
                isOffsetViewShow = false
                binding.cardViewOffSet.gone()
            }

            binding.incrementPlusOneTextView.id -> {
                binding.textViewTodayIslamicDate.text = getIslamicDateByOffSet(1)
                isOffsetViewShow = false
                binding.cardViewOffSet.gone()
            }

            binding.incrementPlusZeroTextView.id -> {
                binding.textViewTodayIslamicDate.text = getIslamicDateByOffSet(0)
                isOffsetViewShow = false
                binding.cardViewOffSet.gone()
            }

            binding.incrementMinusOneTextView.id -> {
                binding.textViewTodayIslamicDate.text = getIslamicDateByOffSet(-1)
                isOffsetViewShow = false
                binding.cardViewOffSet.gone()
            }

            binding.incrementMinusTwoTextView.id -> {
                binding.textViewTodayIslamicDate.text = getIslamicDateByOffSet(-2)
                isOffsetViewShow = false
                binding.cardViewOffSet.gone()
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun upComingNamazTime() {
        val getPrayerTime =
            getPrayTimeInLong(currentLatitude, currentLongitude, method!!)
        val currentNamaz = getTimeDifferenceToNextPrayer()

        if (isTodayFriday()) {
            when (currentNamaz.currentNamazName) {
                "Fajr" -> {
                    binding.textViewFifthNamaz.text =
                        "Jumuah: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Fajr"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Fajr"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)

                }

                "Dhuhr" -> {
                    binding.textViewFifthNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Jumuah"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Jumuah"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                "Asr" -> {
                    binding.textViewFifthNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Jumuah: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Asr"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Asr"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                "Maghrib" -> {
                    binding.textViewFifthNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Jumuah: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Maghrib"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Maghrib"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                "Isha" -> {
                    binding.textViewFifthNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Jumuah: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Isha"
                    currentNamazName = "Isha"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                else -> {
                    binding.textViewFifthNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Jumuah: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "No Namaz Left"
                    binding.textViewCurrentNamazTime.text = "00:00"
                    binding.progressbar.progress = 0F
                    binding.progressbar.progressMax = 100F
                }
            }

        } else {
            when (currentNamaz.currentNamazName) {
                "Fajr" -> {
                    binding.textViewFifthNamaz.text =
                        "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Fajr"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Fajr"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)

                }

                "Dhuhr" -> {
                    binding.textViewFifthNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Dhuhr"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Dhuhr"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                "Asr" -> {
                    binding.textViewFifthNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Asr"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Asr"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                "Maghrib" -> {
                    binding.textViewFifthNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Maghrib"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    currentNamazName = "Maghrib"
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                "Isha" -> {
                    binding.textViewFifthNamaz.text =
                        "Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "Isha"
                    currentNamazName = "Isha"
                    binding.textViewCurrentNamazTime.text =
                        convertToFunTime(currentNamaz.currentNamazTime)
                    startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
                }

                else -> {
                    binding.textViewFifthNamaz.text =
                        "Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                    binding.textViewSecondNamaz.text =
                        "Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}"
                    binding.textViewThirdNamaz.text =
                        "Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}"
                    binding.textViewFourthNamaz.text =
                        "Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}"
                    binding.textViewCurrentNamazName.text = "No Namaz Left"
                    binding.textViewCurrentNamazTime.text = "00:00"
                    binding.progressbar.progress = 0F
                    binding.progressbar.progressMax = 100F
                }

            }


            viewModel.addCurrentNamazToList(
                NotificationData(
                    namazName = currentNamaz.currentNamazName,
                    namazTime = convertToFunTime(currentNamaz.currentNamazTime),
                    createdDate = getCurrentDate()
                )
            )

        }
    }

    private fun startCountdown(timeDifferenceMillis: Long, totalTime: Long) {
        countDownTimer = object : CountDownTimer(timeDifferenceMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                binding.progressbar.progressMax = totalTime.toFloat()
                binding.progressbar.progress = millisUntilFinished.toFloat()
                val remainingTime = formatRemainingTime(secondsRemaining)
                binding.textViewRemainingTime.text = remainingTime
            }

            override fun onFinish() {
                //showNotification(currentNamazName)
                upComingNamazTime()

            }
        }
        countDownTimer?.start()
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy") // Customize the format as needed
        return currentDate.format(formatter)
    }


    private fun getTimeDifferenceToNextPrayer(): PrayerTime {
        val getPrayerTime = getPrayTimeInLong(currentLatitude, currentLongitude, method!!)
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

        val currentTimeMillis =
            convertTimeToMillis(convertToFunTime(System.currentTimeMillis()))
        val currentTimeMillis1159 = convertTimeToMillis("11:59 PM")
        val currentTimeMillis12 = convertTimeToMillis("12:00 AM")

        var currentPrayerTimeIndex = 0
        var previousPrayerTimeIndex = 0
        for ((index, _) in prayerTimeList.withIndex()) {
            if (prayerTimeList[index].currentNamazTime > currentTimeMillis) {
                if (prayerTimeList[index].currentNamazName == "Fajr") {
                    previousPrayerTimeIndex = 4
                    currentPrayerTimeIndex = index
                } else if (prayerTimeList[index].currentNamazName == "Isha") {
                    previousPrayerTimeIndex = index - 1
                    currentPrayerTimeIndex = index
                } else {
                    previousPrayerTimeIndex = index - 1
                    currentPrayerTimeIndex = index
                }
                break
            } else {
                continue
            }


        }

        val timeDifferenceMillis =
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime - currentTimeMillis

        val totalDifferenceMillis =
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime - prayerTimeList[previousPrayerTimeIndex].currentNamazTime
        currentNamazName = prayerTimeList[currentPrayerTimeIndex].currentNamazName

        return if (currentTimeMillis >= prayerTimeList[4].currentNamazTime && currentTimeMillis <= currentTimeMillis1159) {
            val obj = DateTimeUtils()
            val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
            var totalTime1: Long = 0
            var totalTimeFromCurrent: Long = 0


            try {
                val startDateString =
                    "${getFormattedDate(0)} ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}"
                val endDateString =
                    "${getFormattedDate(1)} ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}"

                val startDate = dateFormat.parse(startDateString)
                val endDate = dateFormat.parse(endDateString)

                val (totaltime, curentTimeDifference) = obj.calculateHoursAndMinutesBetween(
                    startDate,
                    endDate
                )
                totalTime1 = totaltime
                totalTimeFromCurrent = curentTimeDifference

            } catch (e: Exception) {
                e.printStackTrace()
            }
            return PrayerTime(
                "Fajr",
                getPrayerTime.fajr.toEpochMilliseconds(),
                totalTimeFromCurrent,
                totalTime1
            )

        } else if (currentTimeMillis >= currentTimeMillis12 && currentTimeMillis < prayerTimeList[0].currentNamazTime) {
            val timeDifferenceMillis1 = prayerTimeList[0].currentNamazTime - currentTimeMillis
            val totalDifferenceMillis1 =
                prayerTimeList[0].currentNamazTime - currentTimeMillis12

            return PrayerTime(
                "Fajr",
                prayerTimeList[0].currentNamazTime,
                timeDifferenceMillis1,
                totalDifferenceMillis1
            )
        } else {
            PrayerTime(
                prayerTimeList[currentPrayerTimeIndex].currentNamazName,
                prayerTimeList[currentPrayerTimeIndex].currentNamazTime,
                timeDifferenceMillis,
                totalDifferenceMillis
            )
        }
    }


    fun convertTimeToMillis(timeString: String): Long {

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

    private fun getMethod() {
        if (!viewModel.getSavedPrayerJurisprudence.isNullOrEmpty()) {
            madhab = if (viewModel.getSavedPrayerJurisprudence.toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }

        if (!viewModel.getMethods.isNullOrEmpty()) {
            method = when (viewModel.getMethods.toInt()) {
                0 -> {
                    CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                1 -> {
                    CalculationMethod.NORTH_AMERICA.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                2 -> {
                    CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                3 -> {
                    CalculationMethod.EGYPTIAN.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                4 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                5 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                6 -> {
                    CalculationMethod.UMM_AL_QURA.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                8 -> {
                    CalculationMethod.UMM_AL_QURA.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                9 -> {
                    CalculationMethod.DUBAI.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                10 -> {
                    CalculationMethod.KUWAIT.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                11 -> {
                    CalculationMethod.SINGAPORE.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                12 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                13 -> {
                    CalculationMethod.QATAR.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                14 -> {
                    CalculationMethod.KARACHI.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                else -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }
            }
        } else {
            CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
        }
    }

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitMessageBox()

                }
            })
    }

    private fun getFormattedDate(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val targetDate: Date = calendar.time

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(targetDate)
    }

    fun toggleImageVisibility() {
        _binding?.imageViewPause?.show()
    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val showImage = intent?.getBooleanExtra("show_image", true) ?: false
            if (showImage) {
                toggleImageVisibility()
            } else {
                notifications.stopPrayer()
                _binding?.imageViewPause?.gone()
            }

        }
    }

    fun isTodayFriday(): Boolean {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.FRIDAY
    }

    private fun showExitMessageBox() {
        // Build and show the alert dialog
        dialog = AlertDialog.Builder(requireContext()).create()
        dialogExitBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.dialog_exit,
            null,
            false
        )
        dialog?.let { dialog ->
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setView(dialogExitBinding!!.root)
            dialog.setCancelable(false)


            dialogExitBinding!!.buttonCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogExitBinding!!.imageViewCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogExitBinding!!.buttonExit.setOnClickListener {
                dialog.dismiss()
                requireActivity().finish()
            }

            dialog.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        dialog = null
        dialogExitBinding = null
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }
}


class DateTimeUtils {
    fun calculateHoursAndMinutesBetween(startDate: Date, endDate: Date): Pair<Long, Long> {
        val totaltimeDifference = Math.abs(endDate.time - startDate.time)
        val currentTime = Calendar.getInstance().time
        val totaltimeDifferenceFromCurrentToEndTime = Math.abs(endDate.time - currentTime.time)

        return Pair(totaltimeDifference, totaltimeDifferenceFromCurrentToEndTime)
    }
}
