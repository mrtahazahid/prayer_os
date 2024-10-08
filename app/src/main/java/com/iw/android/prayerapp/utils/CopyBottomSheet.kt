package com.iw.android.prayerapp.utils

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.databinding.CopyDialogBinding
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class CopyBottomSheet :
    BottomSheetDialogFragment(), View.OnClickListener {
    private var bottomSheetBehavior: BottomSheetBehavior<*>? = null

    var soundName: String = "Adhan"
    var soundAdhan: Int = R.raw.adhan_abdul_basit
    var soundTones: Int = R.raw.adhan_abdul_basit
    var selectedPosition: Int = 0
    var isForAdhan: Boolean = false
    var isForNotification: Boolean = false
    var isSoundSelected: Boolean = false
    var isSilent: Boolean = false
    var isVibrate: Boolean = false
    var isOff: Boolean = false
    private lateinit var binding: CopyDialogBinding
    private lateinit var viewModel: TimeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.MyBottSheetDialog)
    }

    override fun onStart() {
        super.onStart()
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheet = super.onCreateDialog(savedInstanceState)

        //inflating layout
        val view = View.inflate(requireContext(), R.layout.copy_dialog, null)
        //binding views to data binding.
        binding = DataBindingUtil
            .bind<CopyDialogBinding>(view) as CopyDialogBinding
        //setting layout with bottom sheet
        bottomSheet.setContentView(view)


        //  (view.parent as View).setBackgroundResource(R.drawable.top_round_corner_bg)

        val fragment =
            parentFragmentManager.fragments[0].childFragmentManager.fragments[0]
        viewModel = ViewModelProvider(fragment)[TimeViewModel::class.java]
        bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
        //setting Peek at the 16:9 ratio key line of its parent.
        bottomSheetBehavior?.peekHeight = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        bottomSheetBehavior?.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss()
                }
            }

            override fun onSlide(view: View, v: Float) {}
        })
        setOnClickListener()
        return bottomSheet
    }

    private fun setOnClickListener() {
        binding.btnCancelDialog.setOnClickListener(this)
        binding.textViewConfirm.setOnClickListener(this)


    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onClick(v: View?) {
        when (v?.id) {
            binding.btnCancelDialog.id -> {
                dismiss()
            }

            binding.textViewConfirm.id -> {
                binding.progress.show()
                copy()

            }
        }

    }

    private fun copy() = lifecycleScope.launch {
        if (viewModel.getFajrDetail() != null) {
            val fajrData = viewModel.getFajrDetail()
            if (isForNotification) {
                fajrData!!.notificationSound?.soundAdhan = soundAdhan
                fajrData!!.notificationSound?.soundTone = soundTones
                fajrData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                fajrData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                fajrData.notificationSound?.isSilent = isSilent
                fajrData.notificationSound?.isForAdhan = isForAdhan
                fajrData.notificationSound?.isVibrate = isVibrate
                fajrData.notificationSound?.isOff = isOff
            } else {
                fajrData!!.reminderSound?.soundAdhan = soundAdhan
                fajrData!!.reminderSound?.soundTone = soundTones
                fajrData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                fajrData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                fajrData.reminderSound?.isSilent = isSilent
                fajrData.reminderSound?.isForAdhan = isForAdhan
                fajrData.reminderSound?.isVibrate = isVibrate
                fajrData.reminderSound?.isOff = isOff
            }


            viewModel.saveFajrDetail(fajrData)
        } else {
            val fajrData = if (isForNotification) {
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Fajr",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            } else {
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Fajr",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }


            viewModel.saveFajrDetail(fajrData)
        }
//
        if (viewModel.getSunriseDetail() != null) {
            val sunRiseData = viewModel.getSunriseDetail()
            if (isForNotification) {
                sunRiseData!!.notificationSound?.soundAdhan = soundAdhan
                sunRiseData!!.notificationSound?.soundTone = soundTones
                sunRiseData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                sunRiseData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                sunRiseData.notificationSound?.isSilent = isSilent
                sunRiseData.notificationSound?.isForAdhan = isForAdhan
                sunRiseData.notificationSound?.isVibrate = isVibrate
                sunRiseData.notificationSound?.isOff = isOff
            } else {
                sunRiseData!!.reminderSound?.soundAdhan = soundAdhan
                sunRiseData!!.reminderSound?.soundTone = soundTones
                sunRiseData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                sunRiseData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                sunRiseData.reminderSound?.isSilent = isSilent
                sunRiseData.reminderSound?.isForAdhan = isForAdhan
                sunRiseData.reminderSound?.isVibrate = isVibrate
                sunRiseData.reminderSound?.isOff = isOff
            }


            viewModel.saveSunriseDetail(sunRiseData)
        } else {
            val sunriseData = if (isForNotification) {
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Sunrise",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            } else {
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Sunrise",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }
            viewModel.saveSunriseDetail(sunriseData)
        }
//
        if (viewModel.getDuhrDetail() != null) {
            val duhrData = viewModel.getDuhrDetail()
            if (isForNotification) {
                duhrData!!.notificationSound?.soundAdhan = soundAdhan
                duhrData!!.notificationSound?.soundTone = soundTones
                duhrData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                duhrData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                duhrData.notificationSound?.isSilent = isSilent
                duhrData.notificationSound?.isForAdhan = isForAdhan
                duhrData.notificationSound?.isVibrate = isVibrate
                duhrData.notificationSound?.isOff = isOff
            } else {
                duhrData!!.reminderSound?.soundAdhan = soundAdhan
                duhrData!!.reminderSound?.soundTone = soundTones
                duhrData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                duhrData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                duhrData.reminderSound?.isSilent = isSilent
                duhrData.reminderSound?.isForAdhan = isForAdhan
                duhrData.reminderSound?.isVibrate = isVibrate
                duhrData.reminderSound?.isOff = isOff
            }

            viewModel.saveDuhrDetail(duhrData)
        } else {
            val duhrData = if (isForNotification) {
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Dhuhr",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",
                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            } else {
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Dhuhr",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }
            viewModel.saveDuhrDetail(duhrData)
        }
//
        if (viewModel.getAsrDetail() != null) {
            val asrData = viewModel.getAsrDetail()
            if (isForNotification) {
                asrData!!.notificationSound?.soundAdhan = soundAdhan
                asrData!!.notificationSound?.soundTone = soundTones
                asrData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                asrData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                asrData.notificationSound?.isSilent = isSilent
                asrData.notificationSound?.isForAdhan = isForAdhan
                asrData.notificationSound?.isVibrate = isVibrate
                asrData.notificationSound?.isOff = isOff
            } else {
                asrData!!.reminderSound?.soundAdhan = soundAdhan
                asrData!!.reminderSound?.soundTone = soundTones
                asrData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                asrData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                asrData.reminderSound?.isSilent = isSilent
                asrData.reminderSound?.isForAdhan = isForAdhan
                asrData.reminderSound?.isVibrate = isVibrate
                asrData.reminderSound?.isOff = isOff
            }


            viewModel.saveAsrDetail(asrData)
        } else {
            val asrData = if (isForNotification) {
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Asr",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            } else {
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Asr",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }

            viewModel.saveAsrDetail(asrData)
        }
//
        if (viewModel.getMagribDetail() != null) {
            val maghribData = viewModel.getMagribDetail()
            if (isForNotification) {
                maghribData!!.notificationSound?.soundAdhan = soundAdhan
                maghribData!!.notificationSound?.soundTone = soundTones
                maghribData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                maghribData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                maghribData.notificationSound?.isSilent = isSilent
                maghribData.notificationSound?.isForAdhan = isForAdhan
                maghribData.notificationSound?.isVibrate = isVibrate
                maghribData.notificationSound?.isOff = isOff
            } else {
                maghribData!!.reminderSound?.soundAdhan = soundAdhan
                maghribData!!.reminderSound?.soundTone = soundTones
                maghribData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                maghribData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                maghribData.reminderSound?.isSilent = isSilent
                maghribData.reminderSound?.isForAdhan = isForAdhan
                maghribData.reminderSound?.isVibrate = isVibrate
                maghribData.reminderSound?.isOff = isOff
            }

            viewModel.saveMagribDetail(maghribData)
        } else {
            val maghribData = if (isForNotification) {
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Maghrib",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )

            } else {
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Maghrib",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )

            }

            viewModel.saveMagribDetail(maghribData)
        }
//
        if (viewModel.getIshaDetail() != null) {
            val ishaData = viewModel.getIshaDetail()
           if(isForNotification){
               ishaData!!.notificationSound?.soundAdhan = soundAdhan
               ishaData!!.notificationSound?.soundTone = soundTones
               ishaData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
               ishaData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
               ishaData.notificationSound?.isSilent = isSilent
               ishaData.notificationSound?.isForAdhan = isForAdhan
               ishaData.notificationSound?.isVibrate = isVibrate
               ishaData.notificationSound?.isOff = isOff
           }else{
               ishaData!!.reminderSound?.soundAdhan = soundAdhan
               ishaData!!.reminderSound?.soundTone = soundTones
               ishaData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
               ishaData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
               ishaData.reminderSound?.isSilent = isSilent
               ishaData.reminderSound?.isForAdhan = isForAdhan
               ishaData.reminderSound?.isVibrate = isVibrate
               ishaData.reminderSound?.isOff = isOff
           }

            viewModel.saveIshaDetail(ishaData)
        } else {
            val ishaData =  if(isForNotification){
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Isha",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }else{
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Isha",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }

            viewModel.saveIshaDetail(ishaData)
        }
//
        if (viewModel.getMidNightDetail() != null) {
            val midnightData = viewModel.getMidNightDetail()
            if(isForNotification){
                midnightData!!.notificationSound?.soundAdhan = soundAdhan
                midnightData!!.notificationSound?.soundTone = soundTones
                midnightData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                midnightData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                midnightData.notificationSound?.isSilent = isSilent
                midnightData.notificationSound?.isForAdhan = isForAdhan
                midnightData.notificationSound?.isVibrate = isVibrate
                midnightData.notificationSound?.isOff = isOff
            }else{
                midnightData!!.reminderSound?.soundAdhan = soundAdhan
                midnightData!!.reminderSound?.soundTone = soundTones
                midnightData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                midnightData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                midnightData.reminderSound?.isSilent = isSilent
                midnightData.reminderSound?.isForAdhan = isForAdhan
                midnightData.reminderSound?.isVibrate = isVibrate
                midnightData.reminderSound?.isOff = isOff
            }


            viewModel.saveMidNightDetail(midnightData)
        } else {
            val midnightData = if(isForNotification){
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Mid night",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }else{
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Mid night",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tone",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }

            viewModel.saveMidNightDetail(midnightData)
        }

        if (viewModel.getLastNightDetail() != null) {
            val lastThirdData = viewModel.getLastNightDetail()
            if (isForNotification){
                lastThirdData!!.notificationSound?.soundAdhan = soundAdhan
                lastThirdData!!.notificationSound?.soundTone = soundTones
                lastThirdData.notificationSound?.soundName = if (isForAdhan) soundName else "Adhan"
                lastThirdData.notificationSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                lastThirdData.notificationSound?.isSilent = isSilent
                lastThirdData.notificationSound?.isForAdhan = isForAdhan
                lastThirdData.notificationSound?.isVibrate = isVibrate
                lastThirdData.notificationSound?.isOff = isOff
            }else{
                lastThirdData!!.reminderSound?.soundAdhan = soundAdhan
                lastThirdData!!.reminderSound?.soundTone = soundTones
                lastThirdData.reminderSound?.soundName = if (isForAdhan) soundName else "Adhan"
                lastThirdData.reminderSound?.soundToneName = if (!isForAdhan) soundName else "Tone"
                lastThirdData.reminderSound?.isSilent = isSilent
                lastThirdData.reminderSound?.isForAdhan = isForAdhan
                lastThirdData.reminderSound?.isVibrate = isVibrate
                lastThirdData.reminderSound?.isOff = isOff
            }


            viewModel.saveLastNightDetail(lastThirdData)
        } else {

            val lastThirdData = if (isForNotification){
                NotificationData(
                    notificationSound = CurrentNamazNotificationData(
                        currentNamazName = "Last Night",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tones",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }else{
                NotificationData(
                    reminderSound = CurrentNamazNotificationData(
                        currentNamazName = "Last Night",
                        soundName = if (isForAdhan) soundName else "Adhan",
                        soundToneName = if (!isForAdhan) soundName else "Tones",

                        isSoundSelected = isSoundSelected,
                        isForAdhan = isForAdhan,
                        isVibrate = isVibrate,
                        isSilent = isSilent,
                        isOff = isOff,
                        soundAdhan = soundAdhan,
                        soundTone = soundTones
                    ),
                )
            }

            viewModel.saveLastNightDetail(lastThirdData)
        }

        delay(5000)
        dismiss()
    }

}