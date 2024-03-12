package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import android.net.Uri
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
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.databinding.FragmentPrayerSoundBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.OnClick
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.PrayerEnumType
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.RowItemPrayerSound
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.SoundViewModel
import com.iw.android.prayerapp.ui.main.settingFragment.SettingViewModel
import com.iw.android.prayerapp.utils.CopyBottomSheet
import com.iw.android.prayerapp.utils.GetAdhanSound.prayerSoundList
import com.iw.android.prayerapp.utils.TinyDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class PrayerSoundFragment : BaseFragment(R.layout.fragment_prayer_sound), View.OnClickListener,
    OnClick {

    private var _binding: FragmentPrayerSoundBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SoundViewModel by viewModels()

    private val args by navArgs<PrayerSoundFragmentArgs>()

    private var isItemClick = true
    private var soundName = ""
    private var sound: Int? = null
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
        when (args.title) {
            "Fajr" -> {
                binding.textViewTitle.text = "${args.title} Sound"
            }

            "Dhuhr" -> {
                binding.textViewTitle.text = "${args.title} Sound"
            }

            "Asr" -> {
                binding.textViewTitle.text = "${args.title} Sound"
            }

            "Maghrib" -> {
                binding.textViewTitle.text = "${args.title} Sound"
            }

            "Isha" -> {
                binding.textViewTitle.text = "${args.title} Sound"
            }
        }
        setRecyclerView()
        addHolidayList()
    }

    override fun setObserver() {

        viewTypeArray.clear()
        for (data in prayerSoundList) {
            viewTypeArray.add(
                RowItemPrayerSound(data, args.title, this, this)
            )
        }
        adapter.items = viewTypeArray
    }

    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.imageViewCopy.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewBack.id -> {
                Log.d("soundName",soundName)
                Log.d("isDataForSave",isDataForSave.toString())
                if(isDataForSave){
                    binding.progress.show()
                    lifecycleScope.launch {
                        viewModel.saveCurrentNamazNotificationData(CurrentNamazNotificationData(args.title,soundName,isSoundSelected,isVibrateSelected,isSilentSelected,isOffSelected,sound))
                        delay(3000)
                        Log.d("soundName",viewModel.getCurrentNamazNotificationData().toString())
                        binding.progress.hide()
                        findNavController().popBackStack()
                    }

                }else{
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
        when(position){
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

    override fun onSoundSelected(soundName: String, soundPosition: Int, sound: Int?, position: Int) {
        for (checked in prayerSoundList) {
            checked.isItemSelected = false
        }

        // Select the clicked item
        prayerSoundList[position].isItemSelected = true
        // Notify the adapter about the change in the entire dataset

        isDataForSave = true
        when (position) {
            0,1 -> {
                this.soundName = soundName
                this.sound = sound
                isSoundSelected = true
                isVibrateSelected = false
                isSilentSelected = false
                isOffSelected = false
            }
        }
        binding.recyclerView.adapter?.notifyDataSetChanged()


    }
}