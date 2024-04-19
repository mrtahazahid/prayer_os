package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import android.os.Bundle
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


    //private val viewModel: TimeViewModel by viewModels()
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
        lifecycleScope.launch {
            selectedSound = viewModel.getCurrentNamazNotificationData()?.sound ?: 0
            selectedPosition = viewModel.getCurrentNamazNotificationData()?.position ?: 0
            isForAdhan = viewModel.getCurrentNamazNotificationData()?.isForAdhan ?: false
            selectedSoundAdhanName =
                viewModel.getCurrentNamazNotificationData()?.soundName ?: "Adhan"
            selectedSoundToneName =
                viewModel.getCurrentNamazNotificationData()?.soundToneName ?: "Tones"
        }


        binding.textViewTitle.text ="${args.title.split(Regex("\\s|:"))[0]} Sound"

        setRecyclerView()
        addHolidayList()
    }

    override fun setObserver() {

        viewTypeArray.clear()
        prayerSoundList[selectedPosition].isItemSelected = true
        for (data in prayerSoundList) {
            if(isForAdhan){
                viewTypeArray.add(
                    RowItemPrayerSound(
                        data,
                        args.title,
                        this,
                        selectedSound, selectedSoundAdhanName, this

                    )
                )
            }else{
                viewTypeArray.add(
                    RowItemPrayerSound(
                        data,
                        args.title,
                        this,
                        selectedSound, selectedSoundToneName, this

                    )
                )
            }


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
            binding.backView.id,  binding.imageViewBack.id -> {
                if (isDataForSave) {
                    binding.progress.show()
                    lifecycleScope.launch {
                        viewModel.saveCurrentNamazNotificationData(
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
                bottomSheet.show(parentFragmentManager, bottomSheet.tag)
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
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onSoundSelected(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        position: Int,
        isForNotification: Boolean
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
                this.sound = sound
                isSoundSelected = true
                isForAdhan = false
                isVibrateSelected = false
                isSilentSelected = false
                isOffSelected = false
            }
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()


    }
}