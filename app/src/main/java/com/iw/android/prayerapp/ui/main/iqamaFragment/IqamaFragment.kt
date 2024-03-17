package com.iw.android.prayerapp.ui.main.iqamaFragment

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.iw.android.prayerapp.databinding.FragmentIqamaBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.iqamaFragment.itemView.RowItemIqama
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class IqamaFragment : BaseFragment(R.layout.fragment_iqama), View.OnClickListener {

    private var _binding: FragmentIqamaBinding? = null
    val binding
        get() = _binding!!

    private val viewModel: IqamaViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    private var isDisplayDetailShow = false
    private var isNotificationDetailShow = false
    private var isJummuahDetailShow = false
    private var khutbaReminderTime = 0

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
        if (binding.switchEnabled.isChecked) {
            binding.textViewNone.gone()
            binding.cardViewIqamaTime.show()

        } else {
            binding.textViewNone.show()
            binding.cardViewIqamaTime.gone()
        }
        binding.imageViewAdd.isClickable = binding.switchEnabled.isChecked
        binding.imageViewRemove.isClickable = binding.switchEnabled.isChecked

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
        binding.imageViewNotification.setOnClickListener(this)
        binding.imageViewJumuah.setOnClickListener(this)
        binding.cardViewIqamaTime.setOnClickListener(this)
        binding.textViewReset.setOnClickListener(this)
        binding.switchEnabled.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.textViewNone.gone()
                binding.cardViewIqamaTime.show()
            } else {
                binding.textViewNone.show()
                binding.cardViewIqamaTime.gone()
            }

            binding.imageViewAdd.isClickable = binding.switchEnabled.isChecked
            binding.imageViewRemove.isClickable = binding.switchEnabled.isChecked
        }

    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.cardViewIqamaTime.id -> {
                openTimePicker(binding.cardViewIqamaTime.context, 12, 0) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("hh:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    binding.textViewIqamaTime.text = formattedTime
                    // prayerDetailData?.duaTime = formattedTime
                    // savePrayerDetailData()
                }
            }

            binding.textViewReset.id -> {
                binding.progress.show()
                lifecycleScope.launch {
                    viewModel.saveIqamaFajrDetail(
                        IqamaData(
                            "Fajr",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    viewModel.saveIqamaDhuhrDetail(
                        IqamaData(
                            "Dhuhr",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    viewModel.saveIqamaAsrDetail(IqamaData("Asr", DuaTypeEnum.OFF.getValue(), null))
                    viewModel.saveIqamaMaghribDetail(
                        IqamaData(
                            "Maghrib",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    viewModel.saveIqamaIshaDetail(
                        IqamaData(
                            "Isha",
                            DuaTypeEnum.OFF.getValue(),
                            null
                        )
                    )
                    delay(3000)
                    binding.progress.hide()
                    showToast("Iqama setting reset successfully")
                    findNavController().popBackStack()

                }

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
            }

            binding.imageViewRemove.id -> {
                binding.textViewSetTime.text = decrementDuaMinute()
            }

        }

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


}