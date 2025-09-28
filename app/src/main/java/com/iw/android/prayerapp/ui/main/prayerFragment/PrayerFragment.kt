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
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.PrayerTimes
import com.google.gson.Gson
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.base.response.WidgetData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.databinding.DialogExitBinding
import com.iw.android.prayerapp.databinding.FragmentPrayerBinding
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.extension.formatRemainingTime
import com.iw.android.prayerapp.extension.getIslamicDateByOffSet
import com.iw.android.prayerapp.extension.getIslamicDateByOffSet2
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.dateFormat.getCurrentDate
import com.iw.android.prayerapp.utils.time.isTodayFriday
import com.iw.android.prayerapp.widgets.utils.Constants
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrayerFragment : BaseFragment(R.layout.fragment_prayer), View.OnClickListener {

    private var _binding: FragmentPrayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var getPrayTimeInLong: PrayerTimes
    private lateinit var notificationReceiver: BroadcastReceiver
    private var countDownTimer: CountDownTimer? = null
    private val viewModel: PrayerViewModel by viewModels()
    private var currentNamazName = ""
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
        setOnBackPressedListener()
    }

    override fun initialize() {
        getPrayTimeInLong = viewModel.getPrayTimeInLong()
        setProgressBar()
        setCityNameAndIslamicDateText()
        upComingNamazTime()
    }

    override fun setObserver() {}

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
        binding.imageViewStopAdhan.setOnClickListener {
            notifications.stopPrayer()
            binding.cardViewStopAdhan.gone()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        dialog = null
        dialogExitBinding = null
        countDownTimer?.cancel()
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.upComingPrayerTimeView.id -> navigateToPrayerSound(binding.textViewCurrentNamazName.text.toString())
            binding.textViewFifthNamaz.id -> navigateToPrayerSound(binding.textViewFifthNamaz.text.toString())
            binding.textViewFourthNamaz.id -> navigateToPrayerSound(binding.textViewFourthNamaz.text.toString())
            binding.textViewThirdNamaz.id -> navigateToPrayerSound(binding.textViewThirdNamaz.text.toString())
            binding.textViewSecondNamaz.id -> navigateToPrayerSound(binding.textViewSecondNamaz.text.toString())

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
        val currentNamaz = viewModel.getTimeDifferenceToNextPrayer()

        val namazTimes = listOf(
            "Fajr" to getPrayTimeInLong.fajr.toEpochMilliseconds(),
            "Dhuhr" to getPrayTimeInLong.dhuhr.toEpochMilliseconds(),
            "Asr" to getPrayTimeInLong.asr.toEpochMilliseconds(),
            "Maghrib" to getPrayTimeInLong.maghrib.toEpochMilliseconds(),
            "Isha" to getPrayTimeInLong.isha.toEpochMilliseconds()
        )
        val hijriDate =  if (convertToFunTime(System.currentTimeMillis()) > convertToFunTime(getPrayTimeInLong.maghrib.toEpochMilliseconds())
        ) {
            getIslamicDateByOffSet2(1)
        } else {
            getIslamicDateByOffSet2(0)
        }
        val widgetData = listOf(
            WidgetData("FJR",namazTimes[0].second),
            WidgetData("DHR",namazTimes[1].second),
            WidgetData("ASR",namazTimes[2].second),
            WidgetData("MGB",namazTimes[3].second),
            WidgetData("ISH",namazTimes[4].second)
        )

        val gson = Gson()
        val jsonString = gson.toJson(widgetData)
        val prefs = requireContext().getSharedPreferences(Constants.MY_WIDGET_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(Constants.MY_WIDGET_PRAYER_LIST_PREF, jsonString).apply()
        prefs.edit().putString(Constants.MY_WIDGET_HIJRI_DATE_PREF, hijriDate).apply()

        val adjustedNames = if (isTodayFriday()) {
            namazTimes.map { (name, time) ->
                if (name == "Dhuhr") "Jumuah" to time else name to time
            }
        } else {
            namazTimes
        }

        val currentName = when {
            isTodayFriday() && currentNamaz.currentNamazName == "Dhuhr" -> "Jumuah"
            else -> currentNamaz.currentNamazName
        }

        val upcoming = buildUpcomingList(adjustedNames, currentNamaz.currentNamazName)

        // Fill the slots in order
        binding.textViewFifthNamaz.text =
            "${upcoming.getOrNull(0)?.first ?: ""}: ${upcoming.getOrNull(0)?.second ?: ""}"
        binding.textViewSecondNamaz.text =
            "${upcoming.getOrNull(1)?.first ?: ""}: ${upcoming.getOrNull(1)?.second ?: ""}"
        binding.textViewThirdNamaz.text =
            "${upcoming.getOrNull(2)?.first ?: ""}: ${upcoming.getOrNull(2)?.second ?: ""}"
        binding.textViewFourthNamaz.text =
            "${upcoming.getOrNull(3)?.first ?: ""}: ${upcoming.getOrNull(3)?.second ?: ""}"

        binding.textViewCurrentNamazName.text = currentName
        binding.textViewCurrentNamazTime.text = convertToFunTime(currentNamaz.currentNamazTime)

        if (currentNamaz.timeDifference > 0) {
            startCountdown(currentNamaz.timeDifference, currentNamaz.totalTime)
            currentNamazName = currentName
        } else {
            binding.textViewCurrentNamazName.text = "No Namaz Left"
            binding.textViewCurrentNamazTime.text = "00:00"
            binding.progressbar.progress = 0f
            binding.progressbar.progressMax = 100f
        }

        // Save to DB
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.addCurrentNamazToList(
                NotificationData(
                    namazName = currentName,
                    namazTime = convertToFunTime(currentNamaz.currentNamazTime),
                    createdDate = getCurrentDate()
                )
            )
        }
    }

    private fun buildUpcomingList(
        times: List<Pair<String, Long>>,
        current: String
    ): List<Pair<String, String>> {
        val index = times.indexOfFirst { it.first == current }
        if (index == -1) return emptyList()

        val next = mutableListOf<Pair<String, String>>()

        for (i in 1..4) {
            val pair = times[(index + i) % times.size]
            next.add(pair.first to convertToFunTime(pair.second))
        }
        return next
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

    fun toggleImageVisibility() {
        _binding?.cardViewStopAdhan?.show()
    }


    private fun setCityNameAndIslamicDateText() {
        lifecycleScope.launch {
            val location = GetAdhanDetails.getTimeZoneAndCity(
                requireContext(),
                viewModel.userLatLong?.latitude ?: 0.0,
                viewModel.userLatLong?.longitude ?: 0.0
            )

            // Switch to main thread for UI update
            withContext(Dispatchers.Main) {
                binding.textViewCity.text = location?.city ?: "City"
            }
        }

        binding.textViewTodayIslamicDate.text =
            if (convertToFunTime(System.currentTimeMillis()) > convertToFunTime(
                    getPrayTimeInLong.maghrib.toEpochMilliseconds()
                )
            ) {
                getIslamicDateByOffSet(1)
            } else {
                getIslamicDateByOffSet(0)
            }
    }

    private fun setProgressBar() {
        binding.progressbar.apply {
            // or with gradient
            progressBarColorStart = ContextCompat.getColor(requireContext(), R.color.app_green)

            progressBarColorEnd = ContextCompat.getColor(requireContext(), R.color.app_green)
            progressBarColorDirection = CircularProgressBar.GradientDirection.RIGHT_TO_LEFT

            // Set background ProgressBar Color
            backgroundProgressBarColor =
                ContextCompat.getColor(requireContext(), R.color.progress_bg)

            // Set Width
            progressBarWidth = 21f // in DP
            backgroundProgressBarWidth = 21f // in DP

            // Other
            roundBorder = true
            startAngle = 180f
            progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
        }
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

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitMessageBox()

                }
            })
    }

    private fun navigateToPrayerSound(prayerName: String) {
        findNavController().navigate(
            PrayerFragmentDirections.actionPrayerFragmentToPrayerSoundFragment(prayerName)
        )
    }

    inner class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_OFF) {
                // Perform your action here
                _binding?.cardViewStopAdhan?.gone()
            }

            val showImage = intent?.getBooleanExtra("show_image", true) ?: false
            if (showImage) {
                toggleImageVisibility()
            } else {
                notifications.stopPrayer()
                _binding?.cardViewStopAdhan?.gone()
            }

        }
    }

}


