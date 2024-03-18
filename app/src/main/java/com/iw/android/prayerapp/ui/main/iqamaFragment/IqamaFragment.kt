package com.iw.android.prayerapp.ui.main.iqamaFragment

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.data.response.IqamaDisplaySetting
import com.iw.android.prayerapp.data.response.IqamaNotificationData
import com.iw.android.prayerapp.data.response.JummuahData
import com.iw.android.prayerapp.databinding.FragmentIqamaBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.iqamaFragment.itemView.RowItemIqama
import com.iw.android.prayerapp.ui.main.soundFragment.OnDataSelected
import com.iw.android.prayerapp.ui.main.soundFragment.SoundDialog
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class IqamaFragment : BaseFragment(R.layout.fragment_iqama), View.OnClickListener, OnDataSelected {

    private var _binding: FragmentIqamaBinding? = null
    val binding
        get() = _binding!!

    private val viewModel: IqamaViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    private var isDisplayDetailShow = false
    private var isTimerCountDown = false
    private var isShowInList = false
    private var isNotificationDetailShow = false
    private var isJummuahDetailShow = false
    private var khutbaReminderTime = 0
    private var reminderTimeCount = 0
    private var khutbaTime = "12:00 AM"
    private var reminderTime = "off"
    private var soundName = ""
    private var isEnabled = false
    private var notificationReminderSound = 0
    private var notificationReminderTime = "off"
    private var notificationUpdateInterval = "None"
    private var displaySetting: IqamaDisplaySetting? = null

    val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIqamaBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        setRecyclerView()
        spinnerUpdateInterval()
        if (binding.switchEnabled.isChecked) {
            binding.textViewNone.gone()
            binding.cardViewIqamaTime.show()

        } else {
            binding.textViewNone.show()
            binding.cardViewIqamaTime.gone()
        }
        binding.imageViewAdd.isClickable = binding.switchEnabled.isChecked
        binding.imageViewRemove.isClickable = binding.switchEnabled.isChecked

        lifecycleScope.launch {
            viewModel.getJummuahSetting().let {
                khutbaTime = it?.khutbaTime ?: "12:00 AM"
                reminderTime = it?.reminderTime ?: "off"
                isEnabled = it?.isEnabled ?: false



                if (it?.isEnabled == true) {
                    binding.jummahDetail.show()
                    binding.textViewNone.gone()
                    binding.cardViewIqamaTime.show()
                    binding.textViewIqamaTime.text = it.khutbaTime
                    binding.textViewSetTime.text = it.reminderTime
                    binding.switchEnabled.isChecked = it.isEnabled
                    binding.textViewType.gone()
                    binding.imageViewJumuah.setImageResource(R.drawable.ic_drop_down)
                    if (reminderTime != "off" && !reminderTime.isNullOrBlank()) {
                        khutbaReminderTime = extractMinutes(it!!.reminderTime)

                    }
                } else {

                    binding.jummahDetail.gone()
                    binding.textViewType.show()
                    binding.imageViewJumuah.setImageResource(R.drawable.ic_forward)
                }
            }

            viewModel.getIqamaNotificationSetting().let {
                binding.textViewNotificationReminderSound.text = it?.soundName
                notificationReminderTime = it?.reminderTime ?: "off"
                notificationReminderSound = it?.reminderSound ?: 0
                soundName = it?.soundName ?: "Tone"
                binding.textViewNotificationSetTime.text = it?.reminderTime
                if (reminderTime != "off" && !reminderTime.isNullOrBlank()) {
                    reminderTimeCount = extractMinutes(it!!.reminderTime)
                }
            }

            viewModel.getIqamaDisplaySetting().let {
                binding.switchCountDown.isChecked = it?.isTimeCountDownTimer ?: false
                binding.switchShowList.isChecked = it?.isShowInList ?: false
                isTimerCountDown = it?.isTimeCountDownTimer ?: false
                isShowInList = it?.isShowInList ?: false
            }
        }


    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.iqamaList) {
            viewTypeArray.add(
                RowItemIqama(data, binding.recyclerView, this, viewModel)
            )
        }
        adapter.items = viewTypeArray
    }


    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.textViewTitle.setOnClickListener(this)
        binding.imageViewDisplay.setOnClickListener(this)
        binding.imageViewAdd.setOnClickListener(this)
        binding.imageViewRemove.setOnClickListener(this)
        binding.imageViewNotificationAdd.setOnClickListener(this)
        binding.imageViewNotificationRemove.setOnClickListener(this)
        binding.imageViewNotification.setOnClickListener(this)
        binding.imageViewJumuah.setOnClickListener(this)
        binding.textViewNotificationReminderSound.setOnClickListener(this)
        binding.cardViewIqamaTime.setOnClickListener(this)
        binding.textViewReset.setOnClickListener(this)
        binding.switchEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.textViewNone.gone()
                binding.cardViewIqamaTime.show()
                binding.textViewType.gone()
            } else {
                binding.textViewNone.show()
                binding.cardViewIqamaTime.gone()
                binding.textViewType.show()
                khutbaReminderTime = 0
                binding.textViewSetTime.text = "off"
            }
            isEnabled = isChecked

            binding.imageViewAdd.isClickable = binding.switchEnabled.isChecked
            binding.imageViewRemove.isClickable = binding.switchEnabled.isChecked
            saveJummahData()
        }

        binding.switchCountDown.setOnCheckedChangeListener { buttonView, isChecked ->
            isTimerCountDown = isChecked
            saveDisplaySetting()
        }

        binding.switchShowList.setOnCheckedChangeListener { buttonView, isChecked ->
            isShowInList = isChecked
            saveDisplaySetting()
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.textViewNotificationReminderSound.id -> {
                openSoundDialogFragment("Reminder Sound", "Setting", false)
            }

            binding.imageViewNotificationAdd.id -> {
                binding.textViewNotificationSetTime.text = incrementNotificationCountMinute()
                notificationReminderTime = binding.textViewNotificationSetTime.text.toString()
                saveNotificationSetting()
            }

            binding.imageViewNotificationRemove.id -> {
                binding.textViewNotificationSetTime.text = decrementNotificationCountMinute()
                notificationReminderTime = binding.textViewNotificationSetTime.text.toString()
                saveNotificationSetting()
            }

            binding.cardViewIqamaTime.id -> {
                openTimePicker(binding.cardViewIqamaTime.context, 12, 0) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("hh:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    binding.textViewIqamaTime.text = formattedTime
                    khutbaTime = formattedTime
                    saveJummahData()
                    // prayerDetailData?.duaTime = formattedTime
                    // savePrayerDetailData()
                }
            }

            binding.textViewReset.id -> {
                binding.progress.show()
                lifecycleScope.launch {
                    viewModel.saveIqamaFajrDetail(
                        IqamaData(
                            "Fajr","",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    viewModel.saveIqamaDhuhrDetail(
                        IqamaData(
                            "Dhuhr","",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    viewModel.saveIqamaAsrDetail(IqamaData("Asr", "",DuaTypeEnum.OFF.getValue(), null))
                    viewModel.saveIqamaMaghribDetail(
                        IqamaData(
                            "Maghrib","",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    viewModel.saveIqamaIshaDetail(
                        IqamaData(
                            "Isha","",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                }
                binding.progress.hide()
                viewModel.iqamaList.clear()
                viewModel.addList()
                setObserver()

            }

            binding.imageViewJumuah.id -> {
                if (!isJummuahDetailShow) {
                    binding.jummahDetail.show()
                    binding.textViewType.gone()
                    isJummuahDetailShow = true
                    binding.imageViewJumuah.setImageResource(R.drawable.ic_drop_down)
                } else {
                    binding.jummahDetail.gone()
                    binding.textViewType.show()
                    isJummuahDetailShow = false
                    binding.imageViewJumuah.setImageResource(R.drawable.ic_forward)
                }
                if (binding.switchEnabled.isChecked) {
                    binding.textViewType.gone()
                }else{
                    binding.textViewType.show()
                }
            }


            binding.imageViewBack.id -> {
                findNavController().popBackStack()
            }

            binding.imageViewDisplay.id -> {
                if (!isDisplayDetailShow) {
                    binding.displayDetailView.show()
                    isDisplayDetailShow = true
                    binding.imageViewDisplay.setImageResource(R.drawable.ic_drop_down)
                } else {
                    binding.displayDetailView.gone()
                    isDisplayDetailShow = false
                    binding.imageViewDisplay.setImageResource(R.drawable.ic_forward)
                }
            }

            binding.imageViewNotification.id -> {
                if (!isNotificationDetailShow) {
                    binding.detailViews.show()
                    isNotificationDetailShow = true
                    binding.imageViewNotification.setImageResource(R.drawable.ic_drop_down)
                } else {
                    binding.detailViews.gone()
                    isNotificationDetailShow = false
                    binding.imageViewNotification.setImageResource(R.drawable.ic_forward)
                }
            }

            binding.imageViewAdd.id -> {
                binding.textViewSetTime.text = incrementDuaMinute()
                reminderTime = binding.textViewSetTime.text.toString()
                saveJummahData()
            }

            binding.imageViewRemove.id -> {
                binding.textViewSetTime.text = decrementDuaMinute()
                reminderTime = binding.textViewSetTime.text.toString()
                saveJummahData()
            }

        }

    }

    private fun saveJummahData() = lifecycleScope.launch {
        viewModel.saveJummuahSetting(JummuahData(khutbaTime, reminderTime, isEnabled))
    }


    private fun saveDisplaySetting() = lifecycleScope.launch {
        viewModel.saveIqamaDisplaySetting(IqamaDisplaySetting(isTimerCountDown, isShowInList))
    }


    private fun saveNotificationSetting() = lifecycleScope.launch {
        viewModel.saveIqamaNotificationSetting(
            IqamaNotificationData(
                notificationReminderTime,
                notificationReminderSound,
                soundName,
                notificationUpdateInterval
            )
        )
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.stopScroll()
    }

    private fun incrementDuaMinute(): String {
        khutbaReminderTime++
        return "$khutbaReminderTime min"
    }

    private fun decrementDuaMinute(): String {

        return if (khutbaReminderTime > 0) {
            khutbaReminderTime--
            "$khutbaReminderTime min"
        } else {
            "off"
        }
    }

    private fun incrementNotificationCountMinute(): String {
        reminderTimeCount++
        return "$reminderTimeCount min"
    }

    private fun decrementNotificationCountMinute(): String {
        return if (reminderTimeCount > 0) {
            reminderTimeCount--
            "$reminderTimeCount min"
        } else {
            "off"
        }
    }


    fun openTimePicker(
        context: Context,
        initialHour: Int,
        initialMinute: Int,
        onTimeSetListener: (hourOfDay: Int, minute: Int) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, initialHour)
        calendar.set(Calendar.MINUTE, initialMinute)

        val timePickerDialog = TimePickerDialog(
            context,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                onTimeSetListener(hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Set to true for 24-hour format, false for 12-hour format with AM/PM
        )

        // Customize the TimePickerDialog
        timePickerDialog.setTitle("Duha Time")
        timePickerDialog.show()
    }

    private fun spinnerUpdateInterval() {

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.iqama_update_interval,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIqamaReminderSwitch1.adapter = adapter

        lifecycleScope.launch {
            when (viewModel.getIqamaNotificationSetting()?.updateInterval) {
                IqamaTypeEnum.NONE.getValue() -> {
                    binding.spinnerIqamaReminderSwitch1.setSelection(0)
                }

                IqamaTypeEnum.BY_WEEKLY.getValue() -> {
                    binding.spinnerIqamaReminderSwitch1.setSelection(1)

                }

                IqamaTypeEnum.BY_MONTHLY.getValue() -> {
                    binding.spinnerIqamaReminderSwitch1.setSelection(2)

                }


                IqamaTypeEnum.BY_YEARLY.getValue() -> {
                    binding.spinnerIqamaReminderSwitch1.setSelection(3)
                }
            }
        }


        binding.spinnerIqamaReminderSwitch1.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> {
                        notificationUpdateInterval = IqamaTypeEnum.NONE.getValue()
                    }

                    1 -> {
                        notificationUpdateInterval = IqamaTypeEnum.BY_WEEKLY.getValue()
                    }

                    2 -> {
                        notificationUpdateInterval = IqamaTypeEnum.BY_MONTHLY.getValue()
                    }

                    3 -> {
                        notificationUpdateInterval = IqamaTypeEnum.BY_YEARLY.getValue()
                    }
                }
                saveNotificationSetting()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun extractMinutes(input: String): Int {
        val regex = """(\d+)\s+min""".toRegex()
        val matchResult = regex.find(input)
        return matchResult?.groupValues?.get(1)?.toInt() ?: 0
    }

    private fun openSoundDialogFragment(
        title: String,
        subTitle: String,
        isForNotification: Boolean
    ) {
        val soundDialog = SoundDialog()
        soundDialog.listener = this
        soundDialog.title = title
        soundDialog.subTitle = subTitle
        soundDialog.isForNotification = isForNotification
        soundDialog.show(childFragmentManager, "SoundDialogFragment")
    }

    override fun onDataPassed(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        isSoundForNotification: Boolean
    ) {
        binding.textViewNotificationReminderSound.text = soundName
        this.soundName = soundName
        notificationReminderSound = sound ?: 0
        saveNotificationSetting()
    }
}