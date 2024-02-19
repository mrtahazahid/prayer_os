package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerDetailData
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding
import com.iw.android.prayerapp.ui.main.timeFragment.TimeFragment
import com.iw.android.prayerapp.ui.main.timeFragment.TimeFragmentDirections
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class RowItemTime(
    private val data: PrayTime,
    val recyclerView: RecyclerView,
    val fragment: TimeFragment,
    val viewModel: TimeViewModel
) : ViewType<PrayTime> {
    private var isViewShow = false
    private var isSoundSelectNotification = false
    private var currentMinute = 0
    private var duaReminderTime = 0

    override fun layoutId(): Int {
        return R.layout.row_item_pray_time
    }

    override fun data(): PrayTime {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPrayTimeBinding).also { binding ->
            binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title
            binding.textViewTime.text = data.time
            binding.textViewSetTime.text = data.namazDetail.reminderTime
            binding.textViewNotificationSound.text = data.namazDetail.notificationSound
            binding.textViewReminderSound.text = data.namazDetail.reminderNotificationSound

            binding.imageViewDropDownMenu.setOnClickListener {
                if (!isViewShow) {
                    binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_drop_down)
                    binding.detailViews.visibility = View.VISIBLE
                    recyclerView.smoothScrollToPosition(0)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            binding.textViewTime.context,
                            R.color.yellow_text
                        )
                    )
                    isViewShow = true
                } else {
                    binding.detailViews.visibility = View.GONE
                    binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_forward)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            binding.textViewTime.context,
                            R.color.white
                        )
                    )
                    isViewShow = false
                }
            }

            binding.imageViewAdd.setOnClickListener {
                binding.textViewSetTime.text = incrementMinute()
         setReminderTimeData(currentMinute.toString())
            }

            binding.imageViewRemove.setOnClickListener {
                binding.textViewSetTime.text = decrementMinute()
                setReminderTimeData(currentMinute.toString())
            }

            spinnerDua(binding)
            binding.cardViewDuaTime.setOnClickListener {
                openTimePicker(binding.cardViewDuaTime.context, 12, 0) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("hh:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    binding.textViewDuaTime.text = formattedTime
                    // Use formattedTime as needed
                }
            }

            binding.imageViewDuaMinus.setOnClickListener {
                binding.textViewDuaSetTime.text = decrementDuaMinute()
            }

            binding.imageViewDuaAdd.setOnClickListener {
                binding.textViewDuaSetTime.text = incrementDuaMinute()
            }
            binding.textViewNotificationSound.setOnClickListener {
                isSoundSelectNotification = true
                fragment.findNavController().navigate(
                    TimeFragmentDirections.actionTimeFragmentToSoundFragment(
                        data.title,
                        "Notifications Sound",
                        "false",isSoundSelectNotification.toString()
                    )
                )
            }

            binding.textViewReminderSound.setOnClickListener {
                isSoundSelectNotification = false
                fragment.findNavController().navigate(
                    TimeFragmentDirections.actionTimeFragmentToSoundFragment(
                        data.title,
                        "Reminder Sound",
                        "false",isSoundSelectNotification.toString()
                    )
                )
            }

            fragment.parentFragmentManager.setFragmentResultListener(
                "selected_sound",
                fragment.viewLifecycleOwner
            ) { _, result ->
                val receivedData = result.getString("sound")
                val receivedDataPosition = result.getInt("soundPosition")
                val receivedIsSoundSelectNotification = result.getBoolean("isSoundSelectNotification")
                // Handle the received data as needed
                when (data.title) {
                    "Fajr" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveFajrDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveFajrDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }

                    "Sunrise" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveSunriseDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveSunriseDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                    "Dhuhr" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveDuhrDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveDuhrDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                    "Asr" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveAsrDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveAsrDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                    "Maghrib" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveMagribDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveMagribDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                    "Isha" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveIshaDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveIshaDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                    "Midnight" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveMidNightDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveMidNightDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                    "Last Third" -> {
                        if (receivedIsSoundSelectNotification) {
                            binding.textViewNotificationSound.text = receivedData
                            viewModel.saveLastNightDetail(
                                PrayerDetailData(
                                    notificationSound = receivedData ?: "",
                                    notificationSoundPosition = receivedDataPosition,
                                    reminderTime = data.namazDetail.reminderTime,
                                    reminderNotificationSoundPosition = data.namazDetail.reminderNotificationSoundPosition,
                                    reminderNotificationSound = data.namazDetail.reminderNotificationSound,
                                    duaReminder = data.namazDetail.duaReminder,
                                    duaTime = data.namazDetail.duaTime
                                )
                            )
                        } else {
                            binding.textViewReminderSound.text = receivedData
                            viewModel.saveLastNightDetail(
                                PrayerDetailData(
                                    reminderNotificationSound = receivedData ?: "",
                                    reminderNotificationSoundPosition = receivedDataPosition
                                )
                            )

                        }
                    }
                }

            }
        }
    }

    private fun setReminderTimeData(reminderTime:String) {
        when (data.title) {
            "Fajr" -> {
                viewModel.saveFajrDetail(
                    PrayerDetailData(
                        reminderTime = reminderTime
                    )
                )

            }

            "Sunrise" -> {
                viewModel.saveSunriseDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
            "Dhuhr" -> {
                viewModel.saveDuhrDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
            "Asr" -> {
                viewModel.saveAsrDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
            "Maghrib" -> {
                viewModel.saveMagribDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
            "Isha" -> {
                viewModel.saveIshaDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
            "Midnight" -> {
                viewModel.saveMidNightDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
            "Last Third" -> {
                viewModel.saveLastNightDetail(
                    PrayerDetailData(
                         reminderTime = reminderTime
                    )
                )

            }
        }
    }

    private fun incrementMinute(): String {
        currentMinute++
        return "$currentMinute min"
    }

    private fun decrementMinute(): String {

        return if (currentMinute > 0) {
            currentMinute--
            "$currentMinute min"
        } else {
            "off"
        }

    }

    private fun incrementDuaMinute(): String {
        duaReminderTime++
        return "$duaReminderTime min"
    }

    private fun decrementDuaMinute(): String {

        return if (duaReminderTime > 0) {
            duaReminderTime--
            "$duaReminderTime min"
        } else {
            "off"
        }

    }

    private fun spinnerDua(binding: RowItemPrayTimeBinding) {

        val adapter = ArrayAdapter.createFromResource(
            binding.textViewTime.context,
            R.array.dua,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerDuaReminderSwitch.adapter = adapter
        binding.spinnerDuaReminderSwitch.setSelection(0)

        binding.spinnerDuaReminderSwitch.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (position) {
                    0 -> {
                        binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                        binding.cardViewDuaTime.visibility = View.GONE
                        binding.textViewDuaSetTime.visibility = View.GONE
                    }

                    1 -> {
                        binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                        binding.cardViewDuaTime.visibility = View.VISIBLE
                        binding.textViewDuaSetTime.visibility = View.GONE
                    }

                    2 -> {
                        binding.cardViewDuaAdjustmentTime.visibility = View.VISIBLE
                        binding.cardViewDuaTime.visibility = View.GONE
                        binding.textViewDuaSetTime.visibility = View.VISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
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
