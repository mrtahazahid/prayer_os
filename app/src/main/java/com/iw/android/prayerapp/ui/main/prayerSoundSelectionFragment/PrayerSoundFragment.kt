package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.databinding.FragmentPrayerSoundBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.OnClick
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.RowItemPrayerSound
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import com.iw.android.prayerapp.utils.CopyBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PrayerSoundFragment : BaseFragment(R.layout.fragment_prayer_sound), View.OnClickListener,
    OnClick {

    private var _binding: FragmentPrayerSoundBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SoundViewModel by viewModels()
    private val viewModelTime: TimeViewModel by viewModels()
    var prayerSoundList = arrayListOf<PrayerSoundData>()
    private val args by navArgs<PrayerSoundFragmentArgs>()

    private var isItemClick = true
    private var soundAdhan: Int? = null
    private var soundTones: Int? = null
    private var selectedSoundPosition: Int = 0
    private var selectedSoundTonePosition: Int = 0
    private var selectedItemPosition: Int = 0
    private var selectedSound: Int = 0
    private var selectedSoundAdhanName: String = "Adhan"
    private var selectedSoundToneName: String = "Tones"
    private var isForAdhan = false
    private var isVibrateSelected = false
    private var isOffSelected = false
    private var isSoundSelected = false
    private var isSilentSelected = false
    private var isDataForSave = false

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrayerSoundBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        setData()
        addPrayerSoundList()

        Log.d("fds", selectedSoundPosition.toString())
        binding.textViewTitle.text = "${args.title.split(Regex("\\s|:"))[0]} Sound"

        setRecyclerView()
    }


    override fun setObserver() {

        viewTypeArray.clear()
        Log.d("setObserver", selectedSoundPosition.toString())
        for (data in prayerSoundList) {
            viewTypeArray.add(
                RowItemPrayerSound(
                    selectedItemPosition,
                    selectedSoundTonePosition,
                    selectedSoundPosition,

                    data,
                    args.title,
                    this,
                    selectedSound, selectedSoundAdhanName, selectedSoundToneName, this
                )
            )
            adapter.items = viewTypeArray
        }
    }

    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.imageViewCopy.setOnClickListener(this)
        binding.backView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.backView.id, binding.imageViewBack.id -> {
                if (isDataForSave) {
                    val namazTime = viewModelTime.getPrayerTime(
                        viewModelTime.userLatLong?.latitude ?: 0.0,
                        viewModelTime.userLatLong?.longitude ?: 0.0
                    )
                    binding.progress.show()
                    lifecycleScope.launch {
                        Log.d("saveData", selectedSoundPosition.toString())
                        val savingData = CurrentNamazNotificationData(
                            args.title,
                            selectedSoundAdhanName,
                            selectedSoundToneName,
                            selectedSoundPosition,
                            selectedSoundTonePosition,
                            selectedItemPosition,

                            isSoundSelected,
                            isForAdhan,
                            isVibrateSelected,
                            isSilentSelected,
                            isOffSelected,
                            if (selectedItemPosition == 0) soundAdhan else soundTones,
                        )
                        when (args.title.split(Regex("\\s|:"))[0]) {

                            "Fajr" -> {
                                val fajrData = viewModel.getFajrDetail()
                                if (fajrData == null) {
                                    val saveData = NotificationData(
                                        namazName = "Fajr",
                                        namazTime = namazTime[0],
                                        notificationSound = savingData,
                                        reminderSound = null,
                                        reminderTimeMinutes = "off",
                                        reminderTime = "12:00 AM",
                                        secondReminderTimeMinutes = "off",
                                        secondReminderTime = "12:00 AM",
                                        duaReminderMinutes = "off",
                                        duaTime = "12:00 AM",
                                        duaType = "off",
                                    )
                                    viewModel.saveFajrDetail(saveData)
                                } else {
                                    fajrData.notificationSound = savingData
                                    viewModel.saveFajrDetail(fajrData)
                                }


                            }

                            "Dhuhr" -> {
                                val dhuhrData = viewModel.getDuhrDetail()

                                if (dhuhrData == null) {
                                    val saveData = NotificationData(
                                        namazName = "Dhuhr",
                                        namazTime = namazTime[2],
                                        notificationSound = savingData,
                                        reminderSound = null,
                                        reminderTimeMinutes = "off",
                                        reminderTime = "12:00 AM",
                                        secondReminderTimeMinutes = "off",
                                        secondReminderTime = "12:00 AM",
                                        duaReminderMinutes = "off",
                                        duaTime = "12:00 AM",
                                        duaType = "off",
                                    )
                                    viewModel.saveDuhrDetail(saveData)
                                } else {
                                    dhuhrData.notificationSound = savingData
                                    viewModel.saveDuhrDetail(dhuhrData)
                                }

                            }

                            "Asr" -> {
                                val asrData = viewModel.getAsrDetail()


                                if (asrData == null) {
                                    val saveData = NotificationData(
                                        namazName = "Asr",
                                        namazTime = namazTime[3],
                                        notificationSound = savingData,
                                        reminderSound = null,
                                        reminderTimeMinutes = "off",
                                        reminderTime = "12:00 AM",
                                        secondReminderTimeMinutes = "off",
                                        secondReminderTime = "12:00 AM",
                                        duaReminderMinutes = "off",
                                        duaTime = "12:00 AM",
                                        duaType = "off",
                                    )
                                    viewModel.saveAsrDetail(saveData)
                                } else {
                                    asrData.notificationSound = savingData
                                    viewModel.saveAsrDetail(asrData)
                                }
                            }

                            "Maghrib" -> {
                                val maghribData = viewModel.getAsrDetail()

                                if (maghribData == null) {
                                    val saveData = NotificationData(
                                        namazName = "Maghrib",
                                        namazTime = namazTime[4],
                                        notificationSound = savingData,
                                        reminderSound = null,
                                        reminderTimeMinutes = "off",
                                        reminderTime = "12:00 AM",
                                        secondReminderTimeMinutes = "off",
                                        secondReminderTime = "12:00 AM",
                                        duaReminderMinutes = "off",
                                        duaTime = "12:00 AM",
                                        duaType = "off",
                                    )
                                    viewModel.saveMagribDetail(saveData)
                                } else {
                                    maghribData.notificationSound = savingData
                                    viewModel.saveMagribDetail(maghribData)
                                }


                            }

                            "Isha" -> {
                                val ishaData = viewModel.getIshaDetail()


                                if (ishaData == null) {
                                    val saveData = NotificationData(
                                        namazName = "Isha",
                                        namazTime = namazTime[5],
                                        notificationSound = savingData,
                                        reminderSound = null,
                                        reminderTimeMinutes = "off",
                                        reminderTime = "12:00 AM",
                                        secondReminderTimeMinutes = "off",
                                        secondReminderTime = "12:00 AM",
                                        duaReminderMinutes = "off",
                                        duaTime = "12:00 AM",
                                        duaType = "off",
                                    )
                                    viewModel.saveIshaDetail(saveData)
                                } else {
                                    ishaData.notificationSound = savingData
                                    viewModel.saveIshaDetail(ishaData)
                                }


                            }
                        }
                        delay(3000)
                        binding.progress.hide()
                        findNavController().popBackStack()
                    }

                } else {
                    findNavController().popBackStack()
                }


            }

            binding.imageViewCopy.id -> {
                val bottomSheet = CopyBottomSheet()
                bottomSheet.soundName = selectedSoundAdhanName
                bottomSheet.soundAdhan = soundAdhan ?: R.raw.adhan_abdul_basit
                bottomSheet.soundTones = soundTones ?: R.raw.adhan_abdul_basit

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
        isDataForSave = true
        selectedItemPosition = position
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
            Log.d("afterSelectionSound", selectedSoundPosition.toString())
        } else {
            selectedSoundTonePosition = soundPosition
            Log.d("afterSelectionTone", selectedSoundTonePosition.toString())
        }

        Log.d("onSoundSelected", selectedSoundPosition.toString())
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
                //this.selectedSoundToneName = soundName2
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
                //  this.selectedSoundAdhanName = soundName2
                prayerSoundList[position].selectedItemTonesTitle = soundName
                this.soundTones = sound
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
            when (args.title.split(Regex("\\s|:"))[0]) {
                "Fajr" -> {
                    val fajr = viewModel.getFajrDetail()?.notificationSound
                    if (fajr != null) {
                        selectedSound = if (fajr.selectedSoundItemPosition == 0) fajr.soundAdhan
                            ?: R.raw.adhan_abdul_basit else soundTones ?: R.raw.adhan_abdul_basit
                        selectedSoundPosition = fajr.selectedSoundPosition ?: 0
                        Log.d("selectedSoundPosition", "setData:${fajr.selectedSoundPosition} ")
                        selectedSoundTonePosition = fajr.selectedSoundTonePosition ?: 0
                        selectedItemPosition = fajr.selectedSoundItemPosition ?: 0
                        isForAdhan = fajr.isForAdhan
                        selectedSoundAdhanName = fajr.soundName
                        selectedSoundToneName = fajr.soundToneName

                    } else {
                        CurrentNamazNotificationData(
                            "Fajr",
                            "Adhan",
                            "Tones",
                            null,
                            null,
                            null,
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
                    val dhuhr = viewModel.getDuhrDetail()?.notificationSound
                    if (dhuhr != null) {
                        selectedSound = if (dhuhr.selectedSoundItemPosition == 0) dhuhr.soundAdhan
                            ?: R.raw.adhan_abdul_basit else soundTones ?: R.raw.adhan_abdul_basit
                        selectedSoundPosition = dhuhr.selectedSoundPosition ?: 0
                        selectedSoundTonePosition = dhuhr.selectedSoundTonePosition ?: 0
                        selectedItemPosition = dhuhr.selectedSoundItemPosition ?: 0
                        isForAdhan = dhuhr.isForAdhan
                        selectedSoundAdhanName = dhuhr.soundName
                        selectedSoundToneName = dhuhr.soundToneName
                    } else {
                        CurrentNamazNotificationData(
                            "Dhuhr",
                            "Adhan",
                            "Tones",
                            null, null,
                            null,
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
                    val asr = viewModel.getAsrDetail()?.notificationSound
                    if (asr != null) {
                        selectedSound = if (asr.selectedSoundItemPosition == 0) asr.soundAdhan
                            ?: R.raw.adhan_abdul_basit else soundTones ?: R.raw.adhan_abdul_basit
                        selectedSoundPosition = asr.selectedSoundPosition ?: 0
                        selectedSoundTonePosition = asr.selectedSoundTonePosition ?: 0
                        selectedItemPosition = asr.selectedSoundItemPosition ?: 0
                        isForAdhan = asr.isForAdhan
                        selectedSoundAdhanName = asr.soundName
                        selectedSoundToneName = asr.soundToneName
                    } else {
                        CurrentNamazNotificationData(
                            "Asr",
                            "Adhan",
                            "Tones",
                            null,
                            null,
                            null,
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
                    val maghrib = viewModel.getMagribDetail()?.notificationSound
                    if (maghrib != null) {
                        selectedSound =
                            if (maghrib.selectedSoundItemPosition == 0) maghrib.soundAdhan
                                ?: R.raw.adhan_abdul_basit else soundTones
                                ?: R.raw.adhan_abdul_basit
                        selectedSoundPosition = maghrib.selectedSoundPosition ?: 0
                        selectedSoundTonePosition = maghrib.selectedSoundTonePosition ?: 0
                        selectedItemPosition = maghrib.selectedSoundItemPosition ?: 0
                        isForAdhan = maghrib.isForAdhan
                        selectedSoundAdhanName = maghrib.soundName
                        selectedSoundToneName = maghrib.soundToneName
                    } else {
                        CurrentNamazNotificationData(
                            "Maghrib",
                            "Adhan",
                            "Tones",
                            null,
                            null,
                            null,
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
                    val isha = viewModel.getIshaDetail()?.notificationSound
                    if (isha != null) {

                        selectedSound = if (isha.selectedSoundItemPosition == 0) isha.soundAdhan
                            ?: R.raw.adhan_abdul_basit else soundTones ?: R.raw.adhan_abdul_basit
                        selectedSoundPosition = isha.selectedSoundPosition ?: 0
                        selectedSoundTonePosition = isha.selectedSoundTonePosition ?: 0
                        selectedItemPosition = isha.selectedSoundItemPosition ?: 0
                        isForAdhan = isha.isForAdhan
                        selectedSoundAdhanName = isha.soundName
                        selectedSoundToneName = isha.soundToneName
                    } else {
                        CurrentNamazNotificationData(
                            "Isha",
                            "Adhan",
                            "Tones", null,
                            null,
                            null,
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
                        null,
                        null,
                        null,
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
        Log.d("selectedItemPosition", selectedItemPosition.toString())
        prayerSoundList[selectedItemPosition].isItemSelected = true
    }
}