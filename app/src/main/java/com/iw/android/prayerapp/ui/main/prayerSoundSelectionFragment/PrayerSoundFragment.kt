package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
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
import com.iw.android.prayerapp.databinding.FragmentPrayerSoundBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.OnClick
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.RowItemPrayerSound
import com.iw.android.prayerapp.utils.CopyBottomSheet
import com.iw.android.prayerapp.utils.GetAdhanSound.prayerSoundList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PrayerSoundFragment : BaseFragment(R.layout.fragment_prayer_sound), View.OnClickListener,
    OnClick {

    private var _binding: FragmentPrayerSoundBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SoundViewModel by viewModels()

    private val args by navArgs<PrayerSoundFragmentArgs>()

    private var isItemClick = true
    private var sound: Int? = null
    private var notificationTypPosition: Int = 0
    private var selectedSound: Int = 0
    private var selectedSoundAdhanName: String = "Adhan"
    private var selectedSoundToneName: String = "Tones"
    private var selectedPosition: Int = 0
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
      //  setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        lifecycleScope.launch {
            when (args.title.split(Regex("\\s|:"))[0]) {
                "Fajr" -> {
                    if (viewModel.getFajrCurrentNamazNotificationData() != null) {
                        selectedSound = viewModel.getFajrCurrentNamazNotificationData()?.sound ?: 0
                        selectedPosition =
                            viewModel.getFajrCurrentNamazNotificationData()?.position ?: 0
                        isForAdhan =
                            viewModel.getFajrCurrentNamazNotificationData()?.isForAdhan ?: false
                        selectedSoundAdhanName =
                            viewModel.getFajrCurrentNamazNotificationData()?.soundName ?: "Adhan"
                        selectedSoundToneName =
                            viewModel.getFajrCurrentNamazNotificationData()?.soundToneName
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
                    if (viewModel.getDhuhrCurrentNamazNotificationData() != null) {
                        selectedSound = viewModel.getDhuhrCurrentNamazNotificationData()?.sound ?: 0
                        selectedPosition =
                            viewModel.getDhuhrCurrentNamazNotificationData()?.position ?: 0
                        isForAdhan =
                            viewModel.getDhuhrCurrentNamazNotificationData()?.isForAdhan ?: false
                        selectedSoundAdhanName =
                            viewModel.getDhuhrCurrentNamazNotificationData()?.soundName ?: "Adhan"
                        selectedSoundToneName =
                            viewModel.getDhuhrCurrentNamazNotificationData()?.soundToneName
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
                    if (viewModel.getAsrCurrentNamazNotificationData() != null) {
                        selectedSound = viewModel.getAsrCurrentNamazNotificationData()?.sound ?: 0
                        selectedPosition =
                            viewModel.getAsrCurrentNamazNotificationData()?.position ?: 0
                        isForAdhan =
                            viewModel.getAsrCurrentNamazNotificationData()?.isForAdhan ?: false
                        selectedSoundAdhanName =
                            viewModel.getAsrCurrentNamazNotificationData()?.soundName ?: "Adhan"
                        selectedSoundToneName =
                            viewModel.getAsrCurrentNamazNotificationData()?.soundToneName ?: "Tones"
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
                    if (viewModel.getMaghribCurrentNamazNotificationData() != null) {
                        selectedSound =
                            viewModel.getMaghribCurrentNamazNotificationData()?.sound ?: 0
                        selectedPosition =
                            viewModel.getMaghribCurrentNamazNotificationData()?.position ?: 0
                        isForAdhan =
                            viewModel.getMaghribCurrentNamazNotificationData()?.isForAdhan ?: false
                        selectedSoundAdhanName =
                            viewModel.getMaghribCurrentNamazNotificationData()?.soundName ?: "Adhan"
                        selectedSoundToneName =
                            viewModel.getMaghribCurrentNamazNotificationData()?.soundToneName
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
                    if (viewModel.getIshaCurrentNamazNotificationData() != null) {

                        selectedSound = viewModel.getIshaCurrentNamazNotificationData()?.sound ?: 0
                        selectedPosition =
                            viewModel.getIshaCurrentNamazNotificationData()?.position ?: 0
                        isForAdhan =
                            viewModel.getIshaCurrentNamazNotificationData()?.isForAdhan ?: false
                        selectedSoundAdhanName =
                            viewModel.getIshaCurrentNamazNotificationData()?.soundName ?: "Adhan"
                        selectedSoundToneName =
                            viewModel.getIshaCurrentNamazNotificationData()?.soundToneName
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
            Log.d("isis",viewModel.getIshaCurrentNamazNotificationData().toString())

setObserver()
        }


        binding.textViewTitle.text = "${args.title.split(Regex("\\s|:"))[0]} Sound"

        setRecyclerView()
        addHolidayList()
    }

    override fun setObserver() {

        viewTypeArray.clear()
        for (data in prayerSoundList) {
            viewTypeArray.add(
                RowItemPrayerSound(
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
                    binding.progress.show()
                    lifecycleScope.launch {
                        when (args.title.split(Regex("\\s|:"))[0]) {
                            "Fajr" -> {
                                viewModel.saveFajrCurrentNamazNotificationData(
                                    CurrentNamazNotificationData(
                                        args.title,
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
                            }

                            "Dhuhr" -> {
                                viewModel.saveDhuhrCurrentNamazNotificationData(
                                    CurrentNamazNotificationData(
                                        args.title,
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
                            }

                            "Asr" -> {
                                viewModel.saveAsrCurrentNamazNotificationData(
                                    CurrentNamazNotificationData(
                                        args.title,
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
                            }

                            "Maghrib" -> {
                                viewModel.saveMaghribCurrentNamazNotificationData(
                                    CurrentNamazNotificationData(
                                        args.title,
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
                            }

                            "Isha" -> {
                                viewModel.saveIshaCurrentNamazNotificationData(
                                    CurrentNamazNotificationData(
                                        args.title,
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
        for (checked in prayerSoundList) {
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
        prayerSoundList[position].isItemSelected = true
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
                this.selectedSoundToneName = "Tones"
                prayerSoundList[position].selectedItemAdhanTitle = soundName
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
                prayerSoundList[position].selectedItemTonesTitle = soundName
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