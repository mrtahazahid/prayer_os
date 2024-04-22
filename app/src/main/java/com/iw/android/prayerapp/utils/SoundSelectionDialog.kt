package com.iw.android.prayerapp.utils

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.databinding.SoundSelectionDialogBinding
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

    private var isItemClick = true
    var namazName = ""
    private var sound: Int? = null
    private var notificationTypPosition: Int = 0
    var selectedSound: Int = 0
    var selectedSoundAdhanName: String = "Adhan"
    var selectedSoundToneName: String = "Tones"
    var selectedPosition: Int = 0
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
        lifecycleScope.launch {
            Log.d("Fajr", viewModel.getFajrDetail().toString())
            Log.d("Fajr", namazName)
            if (isForNotification) {
                when (namazName) {
                    "Fajr" -> {
                        if (viewModel.getFajrDetail()?.notificationSound != null) {
                            selectedSound = viewModel.getFajrDetail()?.notificationSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getFajrDetail()?.notificationSound?.position ?: 0
                            isForAdhan =
                                viewModel.getFajrDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getFajrDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getFajrDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Fajr",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Dhuhr" -> {
                        if (viewModel.getDuhrDetail()?.notificationSound != null) {
                            selectedSound = viewModel.getDuhrDetail()?.notificationSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getDuhrDetail()?.notificationSound?.position ?: 0
                            isForAdhan =
                                viewModel.getDuhrDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getDuhrDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getDuhrDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Dhuhr",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Asr" -> {
                        if (viewModel.getAsrDetail()?.notificationSound != null) {
                            selectedSound = viewModel.getAsrDetail()?.notificationSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getAsrDetail()?.notificationSound?.position ?: 0
                            isForAdhan =
                                viewModel.getAsrDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getAsrDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getAsrDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Asr",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Maghrib" -> {
                        if (viewModel.getMagribDetail()?.notificationSound != null) {
                            selectedSound =
                                viewModel.getMagribDetail()?.notificationSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getMagribDetail()?.notificationSound?.position ?: 0
                            isForAdhan =
                                viewModel.getMagribDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getMagribDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getMagribDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Maghrib",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Isha" -> {
                        if (viewModel.getIshaDetail()?.notificationSound != null) {

                            selectedSound = viewModel.getIshaDetail()?.notificationSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getIshaDetail()?.notificationSound?.position ?: 0
                            isForAdhan =
                                viewModel.getIshaDetail()?.notificationSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getIshaDetail()?.notificationSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getIshaDetail()?.notificationSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Isha",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    else -> {
                        CurrentNamazNotificationData(
                            "Fajr",
                            "Adhan",
                            "Tones",
                            0,
                            false,
                            true,
                            false,
                            false,
                            false,
                            R.raw.adhan_abdul_basit
                        )
                    }
                }
            } else {
                when (namazName) {
                    "Fajr" -> {
                        if (viewModel.getFajrDetail()?.reminderSound != null) {
                            selectedSound = viewModel.getFajrDetail()?.reminderSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getFajrDetail()?.reminderSound?.position ?: 0
                            isForAdhan =
                                viewModel.getFajrDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getFajrDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getFajrDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Fajr",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Dhuhr" -> {
                        if (viewModel.getDuhrDetail()?.reminderSound != null) {
                            selectedSound = viewModel.getDuhrDetail()?.reminderSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getDuhrDetail()?.reminderSound?.position ?: 0
                            isForAdhan =
                                viewModel.getDuhrDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getDuhrDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getDuhrDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Dhuhr",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Asr" -> {
                        if (viewModel.getAsrDetail()?.reminderSound != null) {
                            selectedSound = viewModel.getAsrDetail()?.reminderSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getAsrDetail()?.reminderSound?.position ?: 0
                            isForAdhan =
                                viewModel.getAsrDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getAsrDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getAsrDetail()?.reminderSound?.soundToneName ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Asr",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Maghrib" -> {
                        if (viewModel.getMagribDetail()?.reminderSound != null) {
                            selectedSound =
                                viewModel.getMagribDetail()?.reminderSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getMagribDetail()?.reminderSound?.position ?: 0
                            isForAdhan =
                                viewModel.getMagribDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getMagribDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getMagribDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Maghrib",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    "Isha" -> {
                        if (viewModel.getIshaDetail()?.reminderSound != null) {

                            selectedSound = viewModel.getIshaDetail()?.reminderSound?.sound ?: 0
                            selectedPosition =
                                viewModel.getIshaDetail()?.reminderSound?.position ?: 0
                            isForAdhan =
                                viewModel.getIshaDetail()?.reminderSound?.isForAdhan ?: false
                            selectedSoundAdhanName =
                                viewModel.getIshaDetail()?.reminderSound?.soundName ?: "Adhan"
                            selectedSoundToneName =
                                viewModel.getIshaDetail()?.reminderSound?.soundToneName
                                    ?: "Tones"
                        } else {
                            CurrentNamazNotificationData(
                                "Isha",
                                "Adhan",
                                "Tones",
                                0,
                                false,
                                true,
                                false,
                                false,
                                false,
                                R.raw.adhan_abdul_basit
                            )
                        }
                    }

                    else -> {
                        CurrentNamazNotificationData(
                            "Fajr",
                            "Adhan",
                            "Tones",
                            0,
                            false,
                            true,
                            false,
                            false,
                            false,
                            R.raw.adhan_abdul_basit
                        )
                    }
                }
            }

        }


        binding.textViewTitle.text = "${namazName} Sound"

        setRecyclerView()
        addHolidayList()
    }


    private fun setObserver() {
        viewTypeArray.clear()
        for (data in GetAdhanSound.prayerSoundList) {
            viewTypeArray.add(
                RowItemPrayerSound(
                    data,
                    namazName,
                    this,
                    selectedSound, selectedSoundAdhanName, selectedSoundToneName, this
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
                    listener?.onDataPass(
                        isForNotification = isForNotification, data = CurrentNamazNotificationData(
                            namazName,
                            selectedSoundAdhanName, selectedSoundToneName,
                            notificationTypPosition,
                            isSoundSelected,
                            isForAdhan,
                            isVibrateSelected,
                            isSilentSelected,
                            isOffSelected,
                            sound,
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
                bottomSheet.sound = sound ?: R.raw.adhan_abdul_basit
                bottomSheet.selectedPosition = selectedPosition

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

    private fun addHolidayList() {

    }

    override fun onItemClick(position: Int) {
        for (checked in GetAdhanSound.prayerSoundList) {
            checked.isItemSelected = false
        }
        isDataForSave = true
        notificationTypPosition = position
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
        GetAdhanSound.prayerSoundList[position].isItemSelected = true
        // Notify the adapter about the change in the entire dataset
        adapter.notifyDataSetChanged()
    }

    override fun onSoundSelected(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        position: Int,
        isForNotification: Boolean,
        textView: AppCompatTextView
    ) {
        for (checked in GetAdhanSound.prayerSoundList) {
            checked.isItemSelected = false
        }
        // Select the clicked item
        GetAdhanSound.prayerSoundList[position].isItemSelected = true
        // Notify the adapter about the change in the entire dataset

        isDataForSave = true
        when (position) {
            0 -> {
                this.selectedSoundAdhanName = soundName
                this.selectedSoundToneName = "Tones"
                GetAdhanSound.prayerSoundList[position].selectedItemAdhanTitle = soundName
                this.sound = sound
                isSoundSelected = true
                isForAdhan = true
                isVibrateSelected = false
                isSilentSelected = false
                isOffSelected = false
            }

            1 -> {
                this.selectedSoundToneName = soundName
                this.selectedSoundAdhanName = "Adhan"
                GetAdhanSound.prayerSoundList[position].selectedItemTonesTitle = soundName
                this.sound = sound
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
}

interface SoundDataPass {
    fun onDataPass(
        data: CurrentNamazNotificationData, isForNotification: Boolean
    )
}
