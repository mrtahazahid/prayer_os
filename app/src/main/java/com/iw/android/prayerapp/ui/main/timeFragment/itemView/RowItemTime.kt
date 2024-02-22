package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import android.app.TimePickerDialog
import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerDetailData
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding
import com.iw.android.prayerapp.ui.main.soundFragment.OnDataSelected
import com.iw.android.prayerapp.ui.main.soundFragment.SoundDialog
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.ui.main.timeFragment.TimeFragment
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar

class RowItemTime(
    private val data: PrayTime,
    val recyclerView: RecyclerView,
    val fragment: TimeFragment,
    val viewModel: TimeViewModel
) : ViewType<PrayTime>, OnDataSelected {
    private var isViewShow = false
    private var prayerDetailData: PrayerDetailData? = null
    private var currentMinute = 0
    private var duaReminderTime = 0
    private lateinit var _binding: RowItemPrayTimeBinding

    override fun layoutId(): Int {
        return R.layout.row_item_pray_time
    }

    override fun data(): PrayTime {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPrayTimeBinding).also { binding ->
            prayerDetailData = data.namazDetail
            _binding = binding
            binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title
            binding.textViewTime.text = data.time
            binding.textViewSetTime.text = if (data.namazDetail.reminderTime != "off") {
                "${data.namazDetail.reminderTime} mins"
            } else {
                data.namazDetail.reminderTime
            }

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
                prayerDetailData?.reminderTime = currentMinute.toString()
                savePrayerDetailData()
            }

            binding.imageViewRemove.setOnClickListener {
                binding.textViewSetTime.text = decrementMinute()
                prayerDetailData?.reminderTime = currentMinute.toString()
                savePrayerDetailData()
            }


            binding.cardViewDuaTime.setOnClickListener {
                openTimePicker(binding.cardViewDuaTime.context, 12, 0) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("hh:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    binding.textViewDuaTime.text = formattedTime
                    prayerDetailData?.duaTime = formattedTime
                    savePrayerDetailData()
                }
            }

            binding.imageViewDuaMinus.setOnClickListener {
                binding.textViewDuaSetTime.text = decrementDuaMinute()
                prayerDetailData?.duaReminder = duaReminderTime.toString()
                savePrayerDetailData()
            }

            binding.imageViewDuaAdd.setOnClickListener {
                binding.textViewDuaSetTime.text = incrementDuaMinute()
                prayerDetailData?.duaReminder = duaReminderTime.toString()
                savePrayerDetailData()
            }
            binding.textViewNotificationSound.setOnClickListener {
                openSoundDialogFragment("Notification Sound", data.title, true)
            }

            binding.textViewReminderSound.setOnClickListener {
                openSoundDialogFragment("Reminder Sound", data.title, false)
            }
            spinnerDua(binding)
        }
    }

    private fun savePrayerDetailData() {
        when (data.title) {
            "Fajr" -> {
                viewModel.saveFajrDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Sunrise" -> {
                viewModel.saveSunriseDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Dhuhr" -> {
                viewModel.saveDuhrDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Asr" -> {
                viewModel.saveAsrDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Maghrib" -> {
                viewModel.saveMagribDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Isha" -> {
                viewModel.saveIshaDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Midnight" -> {
                viewModel.saveMidNightDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
                    )
                )

            }

            "Last Third" -> {
                viewModel.saveLastNightDetail(
                    PrayerDetailData(
                        notificationSound = prayerDetailData?.notificationSound!!,
                        notificationSoundPosition = prayerDetailData?.notificationSoundPosition!!,
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound!!,
                        reminderNotificationSoundPosition = prayerDetailData?.reminderNotificationSoundPosition!!,
                        reminderTime = prayerDetailData?.reminderTime!!,
                        duaReminder = prayerDetailData?.duaReminder!!,
                        duaTime = prayerDetailData?.duaTime!!,
                        duaType = prayerDetailData?.duaType!!
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


        when (data.namazDetail.duaType) {
            DuaTypeEnum.OFF.getValue() -> {
                binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                binding.cardViewDuaTime.visibility = View.GONE
                binding.textViewDuaSetTime.visibility = View.GONE
                binding.spinnerDuaReminderSwitch.setSelection(0)
            }

            DuaTypeEnum.MINUTES.getValue() -> {
                binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                binding.cardViewDuaTime.visibility = View.VISIBLE
                binding.textViewDuaSetTime.visibility = View.GONE
                binding.textViewDuaSetTime.text = "${prayerDetailData?.duaReminder} mins"
                binding.spinnerDuaReminderSwitch.setSelection(2)

            }

            DuaTypeEnum.TIME.getValue() -> {
                binding.cardViewDuaAdjustmentTime.visibility = View.VISIBLE
                binding.cardViewDuaTime.visibility = View.GONE
                binding.textViewDuaSetTime.visibility = View.VISIBLE
                binding.textViewDuaTime.text = prayerDetailData?.duaTime

                binding.spinnerDuaReminderSwitch.setSelection(1)

            }
        }


        binding.spinnerDuaReminderSwitch.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
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
                prayerDetailData?.duaType = selectedItem
                savePrayerDetailData()
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
        soundDialog.show(fragment.childFragmentManager, "SoundDialogFragment")
    }

    override fun onDataPassed(
        soundName: String,
        soundPosition: Int,
        isSoundForNotification: Boolean
    ) {
        when (data.title) {
            "Fajr" -> {
                if (isSoundForNotification) {
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition
                    _binding.textViewNotificationSound.text = soundName
                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition
                }
            }

            "Sunrise" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition

                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition
                }
            }

            "Dhuhr" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition
                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition

                }
            }

            "Asr" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition
                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition

                }
            }

            "Maghrib" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition
                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition
                }
            }

            "Isha" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition

                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition
                }
            }

            "Midnight" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition
                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition
                }
            }

            "Last Third" -> {
                if (isSoundForNotification) {
                    _binding.textViewNotificationSound.text = soundName
                    prayerDetailData?.notificationSound = soundName
                    prayerDetailData?.notificationSoundPosition = soundPosition
                } else {
                    _binding.textViewReminderSound.text = soundName
                    prayerDetailData?.reminderNotificationSound = soundName
                    prayerDetailData?.reminderNotificationSoundPosition = soundPosition
                }
            }

        }
        savePrayerDetailData()
    }
}