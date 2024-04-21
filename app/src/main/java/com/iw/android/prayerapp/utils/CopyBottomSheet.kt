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
    var sound: Int = R.raw.adhan_abdul_basit
    var selectedPosition: Int = 0
    var isForAdhan: Boolean = false
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
            fajrData?.sound = sound
            fajrData?.notificationSound = soundName
            fajrData?.isSilent = isSilent
            fajrData?.isVibrate = isVibrate
            fajrData?.isOff = isOff
            viewModel.saveFajrDetail(fajrData!!)
        } else {
            val fajrData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveFajrDetail(fajrData)
        }

        if (viewModel.getSunriseDetail() != null) {
            val sunRiseData = viewModel.getSunriseDetail()
            sunRiseData?.sound = sound
            sunRiseData?.notificationSound = soundName
            sunRiseData?.isSilent = isSilent
            sunRiseData?.isVibrate = isVibrate
            sunRiseData?.isOff = isOff
            viewModel.saveSunriseDetail(sunRiseData!!)
        } else {
            val fajrData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveSunriseDetail(fajrData)
        }

        if (viewModel.getDuhrDetail() != null) {
            val duhrData = viewModel.getDuhrDetail()
            duhrData?.sound = sound
            duhrData?.notificationSound = soundName
            duhrData?.isSilent = isSilent
            duhrData?.isVibrate = isVibrate
            duhrData?.isOff = isOff
            viewModel.saveDuhrDetail(duhrData!!)
        } else {
            val duhrData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveDuhrDetail(duhrData)
        }

        if (viewModel.getAsrDetail() != null) {
            val asrData = viewModel.getAsrDetail()
            asrData?.sound = sound
            asrData?.notificationSound = soundName
            asrData?.isSilent = isSilent
            asrData?.isVibrate = isVibrate
            asrData?.isOff = isOff
            viewModel.saveAsrDetail(asrData!!)
        } else {
            val asrData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveAsrDetail(asrData)
        }

        if (viewModel.getMagribDetail() != null) {
            val maghribData = viewModel.getMagribDetail()
            maghribData?.sound = sound
            maghribData?.notificationSound = soundName
            maghribData?.isSilent = isSilent
            maghribData?.isVibrate = isVibrate
            maghribData?.isOff = isOff
            viewModel.saveMagribDetail(maghribData!!)
        } else {
            val maghribData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveMagribDetail(maghribData)
        }

        if (viewModel.getIshaDetail() != null) {
            val ishaData = viewModel.getIshaDetail()
            ishaData?.sound = sound
            ishaData?.notificationSound = soundName
            ishaData?.isSilent = isSilent
            ishaData?.isVibrate = isVibrate
            ishaData?.isOff = isOff
            viewModel.saveIshaDetail(ishaData!!)
        } else {
            val ishaData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveIshaDetail(ishaData)
        }

        if (viewModel.getMidNightDetail() != null) {
            val midnightData = viewModel.getMidNightDetail()
            midnightData?.sound = sound
            midnightData?.notificationSound = soundName
            midnightData?.isSilent = isSilent
            midnightData?.isVibrate = isVibrate
            midnightData?.isOff = isOff
            viewModel.saveMidNightDetail(midnightData!!)
        } else {
            val midnightData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveMidNightDetail(midnightData)
        }

        if (viewModel.getLastNightDetail() != null) {
            val lastThirdData = viewModel.getLastNightDetail()
            lastThirdData?.sound = sound
            lastThirdData?.notificationSound = soundName
            lastThirdData?.isSilent = isSilent
            lastThirdData?.isVibrate = isVibrate
            lastThirdData?.isOff = isOff
            viewModel.saveLastNightDetail(lastThirdData!!)
        } else {
            val lastThirdData = NotificationData(
                sound = sound,
                notificationSound = soundName,
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff
            )
            viewModel.saveLastNightDetail(lastThirdData)
        }

        if (viewModel.getFajrCurrentNamazNotificationData() != null) {
            val fajrData = viewModel.getFajrCurrentNamazNotificationData()
            fajrData?.sound = sound
            if (isForAdhan) {
                fajrData?.soundName = soundName
                fajrData?.soundToneName = "Tones"
            } else {
                fajrData?.soundName = "Adhan"
                fajrData?.soundToneName = soundName
            }
            fajrData?.position = selectedPosition
            fajrData?.isSilent = isSilent
            fajrData?.isVibrate = isVibrate
            fajrData?.isOff = isOff
            fajrData?.isSoundSelected = isSoundSelected
            viewModel.saveFajrCurrentNamazNotificationData(fajrData!!)
        } else {
            val fajrData =  CurrentNamazNotificationData(
                sound = sound,
                soundName = if(isForAdhan)soundName else "Adhan",
                soundToneName =   if(!isForAdhan)soundName else "Tones",
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff, currentNamazName = "Fajr", position = selectedPosition,isSoundSelected=isSoundSelected, isForAdhan = isForAdhan
            )
            viewModel.saveFajrCurrentNamazNotificationData(fajrData)
        }

        if (viewModel.getDhuhrCurrentNamazNotificationData() != null) {
            val dhuhrData = viewModel.getDhuhrCurrentNamazNotificationData()
            dhuhrData?.sound = sound
            if (isForAdhan) {
                dhuhrData?.soundName = soundName
                dhuhrData?.soundToneName = "Tones"
            } else {
                dhuhrData?.soundName = "Adhan"
                dhuhrData?.soundToneName = soundName
            }
            dhuhrData?.position = selectedPosition
            dhuhrData?.isSilent = isSilent
            dhuhrData?.isVibrate = isVibrate
            dhuhrData?.isOff = isOff
            dhuhrData?.isSoundSelected = isSoundSelected
            viewModel.saveDhuhrCurrentNamazNotificationData(dhuhrData!!)
        } else {
            val dhuhrData =  CurrentNamazNotificationData(
                sound = sound,
                soundName = if(isForAdhan)soundName else "Adhan",
                soundToneName =   if(!isForAdhan)soundName else "Tones",
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff, currentNamazName = "Fajr", position = selectedPosition,isSoundSelected=isSoundSelected, isForAdhan = isForAdhan
            )
            viewModel.saveDhuhrCurrentNamazNotificationData(dhuhrData)
        }


        if (viewModel.getAsrCurrentNamazNotificationData() != null) {
            val asrData = viewModel.getAsrCurrentNamazNotificationData()
            asrData?.sound = sound
            if (isForAdhan) {
                asrData?.soundName = soundName
                asrData?.soundToneName = "Tones"
            } else {
                asrData?.soundName = "Adhan"
                asrData?.soundToneName = soundName
            }
            asrData?.position = selectedPosition
            asrData?.isSilent = isSilent
            asrData?.isVibrate = isVibrate
            asrData?.isOff = isOff
            asrData?.isSoundSelected = isSoundSelected
            viewModel.saveAsrCurrentNamazNotificationData(asrData!!)
        } else {
            val asrData =  CurrentNamazNotificationData(
                sound = sound,
                soundName = if(isForAdhan)soundName else "Adhan",
                soundToneName =   if(!isForAdhan)soundName else "Tones",
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff, currentNamazName = "Fajr", position = selectedPosition,isSoundSelected=isSoundSelected, isForAdhan = isForAdhan
            )
            viewModel.saveAsrCurrentNamazNotificationData(asrData)
        }


        if (viewModel.getMaghribCurrentNamazNotificationData() != null) {
            val maghribData = viewModel.getMaghribCurrentNamazNotificationData()
            maghribData?.sound = sound
            if (isForAdhan) {
                maghribData?.soundName = soundName
                maghribData?.soundToneName = "Tones"
            } else {
                maghribData?.soundName = "Adhan"
                maghribData?.soundToneName = soundName
            }
            maghribData?.position = selectedPosition
            maghribData?.isSilent = isSilent
            maghribData?.isVibrate = isVibrate
            maghribData?.isOff = isOff
            maghribData?.isSoundSelected = isSoundSelected
            viewModel.saveMaghribCurrentNamazNotificationData(maghribData!!)
        } else {
            val maghribData =  CurrentNamazNotificationData(
                sound = sound,
                soundName = if(isForAdhan)soundName else "Adhan",
                soundToneName =   if(!isForAdhan)soundName else "Tones",
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff, currentNamazName = "Fajr", position = selectedPosition,isSoundSelected=isSoundSelected, isForAdhan = isForAdhan
            )
            viewModel.saveMaghribCurrentNamazNotificationData(maghribData)
        }


        if (viewModel.getIshaCurrentNamazNotificationData() != null) {
            val ishaData = viewModel.getIshaCurrentNamazNotificationData()
            ishaData?.sound = sound
            if (isForAdhan) {
                ishaData?.soundName = soundName
                ishaData?.soundToneName = "Tones"
            } else {
                ishaData?.soundName = "Adhan"
                ishaData?.soundToneName = soundName
            }
            ishaData?.position = selectedPosition
            ishaData?.isSilent = isSilent
            ishaData?.isVibrate = isVibrate
            ishaData?.isOff = isOff
            ishaData?.isSoundSelected = isSoundSelected
            viewModel.saveIshaCurrentNamazNotificationData(ishaData!!)
        } else {
            val ishaData =  CurrentNamazNotificationData(
                sound = sound,
                soundName = if(isForAdhan)soundName else "Adhan",
                soundToneName =   if(!isForAdhan)soundName else "Tones",
                isSilent = isSilent,
                isVibrate = isVibrate,
                isOff = isOff, currentNamazName = "Fajr", position = selectedPosition,isSoundSelected=isSoundSelected, isForAdhan = isForAdhan
            )
            viewModel.saveIshaCurrentNamazNotificationData(ishaData)
        }


        delay(5000)
        dismiss()
    }

}