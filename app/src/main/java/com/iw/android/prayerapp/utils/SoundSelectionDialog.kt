package com.iw.android.prayerapp.utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.databinding.SoundSelectionDialogBinding
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.PrayerEnumType
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.SoundViewModel
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.OnClick
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.RowItemPrayerSound
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundSelectionDialog : DialogFragment(), View.OnClickListener,
    OnClick {

    private var _binding: SoundSelectionDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SoundViewModel
    var prayerSoundList = arrayListOf<PrayerSoundData>()
    private var isItemClick = true
    var namazName = ""
    private var soundAdhan: Int? = null
    private var soundTone: Int? = null
    var selectedSound: Int = 0
     var selectedItemPosition: Int = 0
     var selectedSoundPosition: Int = 0
     var selectedSoundTonePosition: Int = 0
    var selectedSoundAdhanName: String = "Adhan"
    var selectedSoundToneName: String = "Tones"
    var isForNotification = false
    var isForAdhan = false
    var isVibrateSelected = false
    var isOffSelected = false
    var isSoundSelected = false
    var isSilentSelected = false
    private var isDataForSave = false
    var listener: SoundDataPass? = null

    private var viewTypeArray = ArrayList<ViewType<*>>()

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
                lifecycleScope.launch {
                    if (isItemClick) {
                        isItemClick = false
                        item.data()?.let { data ->

                        }
                    }
                    delay(1000)
                    isItemClick = true
                }
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment =
            parentFragmentManager.fragments[0].childFragmentManager.fragments[0]
        viewModel = ViewModelProvider(fragment)[SoundViewModel::class.java]
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SoundSelectionDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    private fun initialize() {
        binding.textViewTitle.text = "${namazName} Sound"
        setData()
        addPrayerSoundList()
        setRecyclerView()
    }

    private fun addPrayerSoundList() {
        prayerSoundList.add(
            PrayerSoundData(
                "Adhan", R.drawable.ic_mike, PrayerEnumType.ADHAN.getValue(),
                isImageForwardShow = true,
                isItemSelected = false,
                selectedItemAdhanTitle = "adhan",
                selectedItemTonesTitle = "Tones",
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Tones", R.drawable.ic_tone, PrayerEnumType.TONES.getValue(),
                isImageForwardShow = true,
                isItemSelected = false
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Vibrate", R.drawable.ic_vibrate, PrayerEnumType.VIBRATE.getValue(),
                isImageForwardShow = false,
                isItemSelected = false
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Silent", R.drawable.ic_mute_mike, PrayerEnumType.SILENT.getValue(),
                isImageForwardShow = false,
                isItemSelected = false
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Off", R.drawable.ic_off, PrayerEnumType.OFF.getValue(),
                isImageForwardShow = false,
                isItemSelected = false
            )
        )
        prayerSoundList[selectedItemPosition].isItemSelected = true
    }

    private fun setObserver() {
        viewTypeArray.clear()
        for (data in prayerSoundList) {
            viewTypeArray.add(
                RowItemPrayerSound(
                    selectedItemPosition,
                    selectedSoundTonePosition,
                    selectedSoundPosition,

                    data,
                    namazName,
                    this,
                    selectedSound,
                    selectedSoundAdhanName,
                    selectedSoundToneName, this
                )
            )
            adapter.items = viewTypeArray
        }
    }


    private fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.imageViewCopy.setOnClickListener(this)
        binding.backView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.backView.id, binding.imageViewBack.id -> {
                if (isDataForSave) {
                    Log.d("backView",selectedSoundPosition.toString())
                    Log.d("backView",selectedItemPosition.toString())
                    Log.d("backView",selectedSoundTonePosition.toString())
                    listener?.onDataPass(
                        isForNotification = isForNotification, data = CurrentNamazNotificationData(
                            currentNamazName = namazName,
                            soundName = selectedSoundAdhanName,
                            soundToneName = selectedSoundToneName,
                            selectedSoundPosition = selectedSoundPosition,
                            selectedSoundItemPosition = selectedItemPosition,
                            selectedSoundTonePosition = selectedSoundTonePosition,
                            isSoundSelected = isSoundSelected,
                            isForAdhan = isForAdhan,
                            isVibrate = isVibrateSelected,
                            isSilent = isSilentSelected,
                            isOff = isOffSelected,
                            soundAdhan = soundAdhan,
                            soundTone = soundTone,
                        )
                    )
                    dismiss()
                } else {
                    dismiss()
                }


            }

            binding.imageViewCopy.id -> {
                val bottomSheet = CopyBottomSheet()
                bottomSheet.soundName = selectedSoundAdhanName
                bottomSheet.isForNotification = isForNotification
                bottomSheet.soundAdhan = soundAdhan ?: R.raw.adhan_abdul_basit
                bottomSheet.soundTones = soundTone ?: R.raw.adhan_abdul_basit
//todo word for position item sound
                bottomSheet.isSilent = isSilentSelected
                bottomSheet.isForAdhan = isForAdhan
                bottomSheet.isSoundSelected = isSoundSelected
                bottomSheet.isVibrate = isVibrateSelected
                bottomSheet.isOff = isOffSelected

                bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter

    }


    override fun onItemClick(position: Int) {
        for (checked in prayerSoundList) {
            checked.isItemSelected = false
        }
        selectedItemPosition = position
        isDataForSave = true
        when (position) {
            2 -> {
                isVibrateSelected = true
                isSoundSelected = false
                isSilentSelected = false
                isOffSelected = false
            }

            3 -> {
                isSilentSelected = true
                isSoundSelected = false
                isVibrateSelected = false
                isOffSelected = false
            }

            4 -> {

                isOffSelected = true
                isSoundSelected = false
                isVibrateSelected = false
                isSilentSelected = false
            }
        }
        // Select the clicked item
        prayerSoundList[position].isItemSelected = true
        // Notify the adapter about the change in the entire dataset
        adapter.notifyDataSetChanged()
    }

    override fun onSoundSelected(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        position: Int,
        isForNotification: Boolean
    ) {

        if (position == 0) {
            selectedSoundPosition = soundPosition
        } else {
            selectedSoundTonePosition = soundPosition
        }
        selectedItemPosition = position

        for (checked in prayerSoundList) {
            checked.isItemSelected = false
        }
        // Select the clicked item
        prayerSoundList[position].isItemSelected = true
        // Notify the adapter about the change in the entire dataset

        isDataForSave = true
        when (position) {
            0 -> {
                this.selectedSoundAdhanName = soundName
                // this.selectedSoundToneName = soundName2
                prayerSoundList[position].selectedItemAdhanTitle = soundName
                this.soundAdhan = sound
                isSoundSelected = true
                isForAdhan = true
                isVibrateSelected = false
                isSilentSelected = false
                isOffSelected = false
            }

            1 -> {
                this.selectedSoundToneName = soundName
                //this.selectedSoundAdhanName = soundName2
                prayerSoundList[position].selectedItemTonesTitle = soundName
                this.soundTone = sound
                isSoundSelected = true
                isForAdhan = false
                isVibrateSelected = false
                isSilentSelected = false
                isOffSelected = false
            }
        }
        adapter.notifyDataSetChanged()
        setObserver()


    }

    private fun setData() {
        lifecycleScope.launch {
            if (isForNotification) {
                when (namazName) {
                    "Fajr" -> {
                        if (viewModel.getFajrDetail()?.notificationSound != null) {

                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getFajrDetail()?.notificationSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getFajrDetail()?.notificationSound?.soundTone ?: 0
                            }
                            selectedSoundTonePosition = viewModel.getFajrDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getFajrDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getFajrDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getFajrDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getFajrDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Fajr",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Dhuhr" -> {
                        if (viewModel.getDuhrDetail()?.notificationSound != null) {

                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getDuhrDetail()?.notificationSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getDuhrDetail()?.notificationSound?.soundTone ?: 0
                            }

                            selectedSoundTonePosition = viewModel.getDuhrDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getDuhrDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getDuhrDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getDuhrDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getDuhrDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Dhuhr",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Asr" -> {
                        if (viewModel.getAsrDetail()?.notificationSound != null) {

                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getAsrDetail()?.notificationSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getAsrDetail()?.notificationSound?.soundTone ?: 0
                            }

                            selectedSoundTonePosition = viewModel.getAsrDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getAsrDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getAsrDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getAsrDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getAsrDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Asr",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Maghrib" -> {
                        if (viewModel.getMagribDetail()?.notificationSound != null) {

                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getMagribDetail()?.notificationSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getMagribDetail()?.notificationSound?.soundTone ?: 0
                            }
                            selectedSoundTonePosition = viewModel.getMagribDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getMagribDetail()?.notificationSound?.selectedSoundPosition?:0

                            isForAdhan =
                                viewModel.getMagribDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getMagribDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getMagribDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Maghrib",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Isha" -> {
                        if (viewModel.getIshaDetail()?.notificationSound != null) {

                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getIshaDetail()?.notificationSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getIshaDetail()?.notificationSound?.soundTone ?: 0
                            }

                            selectedSoundTonePosition = viewModel.getIshaDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getIshaDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getIshaDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getIshaDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getIshaDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Isha",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    else -> {
                        CurrentNamazNotificationData(
                            currentNamazName = "Fajr",
                            soundName = "Adhan",
                            soundToneName = "Tones",
                            selectedSoundPosition = selectedSoundPosition,
                            selectedSoundItemPosition = selectedItemPosition,
                            selectedSoundTonePosition = selectedSoundTonePosition,
                            isSoundSelected = true,
                            isForAdhan = false,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                        )
                    }
                }
            } else {
                when (namazName) {
                    "Fajr" -> {
                        if (viewModel.getFajrDetail()?.reminderSound != null) {
                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getFajrDetail()?.reminderSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getFajrDetail()?.reminderSound?.soundTone ?: 0
                            }
                            selectedSoundTonePosition = viewModel.getFajrDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getFajrDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getFajrDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getFajrDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getFajrDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Fajr",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Dhuhr" -> {
                        if (viewModel.getDuhrDetail()?.reminderSound != null) {
                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getDuhrDetail()?.reminderSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getDuhrDetail()?.reminderSound?.soundTone ?: 0
                            }
                            selectedSoundTonePosition = viewModel.getDuhrDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getDuhrDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getDuhrDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getDuhrDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getDuhrDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Dhuhr",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Asr" -> {
                        if (viewModel.getAsrDetail()?.reminderSound != null) {
                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getAsrDetail()?.reminderSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getAsrDetail()?.reminderSound?.soundTone ?: 0
                            }

                            selectedSoundTonePosition = viewModel.getAsrDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getAsrDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getAsrDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getAsrDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getAsrDetail()?.reminderSound?.soundToneName ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Asr",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Maghrib" -> {
                        if (viewModel.getMagribDetail()?.reminderSound != null) {
                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getMagribDetail()?.reminderSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getMagribDetail()?.reminderSound?.soundTone ?: 0
                            }

                            selectedSoundTonePosition = viewModel.getMagribDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getMagribDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getMagribDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getMagribDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getMagribDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Maghrib",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Isha" -> {
                        if (viewModel.getIshaDetail()?.reminderSound != null) {
                            if (selectedItemPosition == 0) {
                                selectedSound =
                                    viewModel.getIshaDetail()?.reminderSound?.soundAdhan ?: 0
                            } else if (selectedItemPosition == 1) {
                                selectedSound =
                                    viewModel.getIshaDetail()?.reminderSound?.soundTone ?: 0
                            }
                            selectedSoundTonePosition = viewModel.getIshaDetail()?.notificationSound?.selectedSoundTonePosition?:0
                            selectedSoundPosition = viewModel.getIshaDetail()?.notificationSound?.selectedSoundPosition?:0
                            isForAdhan =
                                viewModel.getIshaDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getIshaDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getIshaDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                currentNamazName = "Isha",
                                soundName = "Adhan",
                                soundToneName = "Tones",
                                selectedSoundPosition = selectedSoundPosition,
                                selectedSoundItemPosition = selectedItemPosition,
                                selectedSoundTonePosition = selectedSoundTonePosition,
                                isSoundSelected = true,
                                isForAdhan = false,
                                isVibrate = false,
                                isSilent = false,
                                isOff = false,
                                soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    else -> {
                        CurrentNamazNotificationData(
                            currentNamazName = "Fajr",
                            soundName = "Adhan",
                            soundToneName = "Tones",
                            selectedSoundPosition = selectedSoundPosition,
                            selectedSoundItemPosition = selectedItemPosition,
                            selectedSoundTonePosition = selectedSoundTonePosition,
                            isSoundSelected = true,
                            isForAdhan = false,
                            isVibrate = false,
                            isSilent = false,
                            isOff = false,
                            soundTone = R.raw.adhan_abdul_basit, soundAdhan = R.raw.adhan_abdul_basit
                        )
                    }
                }
            }

        }
    }


}

interface SoundDataPass {
    fun onDataPass(
        data: CurrentNamazNotificationData, isForNotification: Boolean
    )
}
