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
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding
import com.iw.android.prayerapp.ui.main.soundFragment.OnDataSelected
import com.iw.android.prayerapp.ui.main.soundFragment.SoundDialog
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.ui.main.timeFragment.TimeFragment
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RowItemTime(
    private val data: PrayTime,
    val recyclerView: RecyclerView,
    val fragment: TimeFragment,
    val viewModel: TimeViewModel
) : ViewType<PrayTime>, OnDataSelected {
    private var isViewShow = false
    private var prayerDetailData: NotificationData? = null
    private var reminderTimeMinutes = 0
    private var duaReminderTimeMinutes = 0
    private lateinit var _binding: RowItemPrayTimeBinding

    override fun layoutId(): Int {
        return R.layout.row_item_pray_time
    }

    override fun data(): PrayTime {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPrayTimeBinding).also { binding ->
            _binding = binding
            binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
            prayerDetailData = data.namazDetail

            val color = if (data.isCurrentNamaz) ContextCompat.getColorStateList(
                binding.textViewTime.context,
                R.color.yellow_text
            ) else ContextCompat.getColorStateList(
                binding.textViewTime.context,
                R.color.text_color_gray
            )

            binding.textViewTime.setTextColor(color)

            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title
            binding.textViewTime.text = data.time
            binding.textViewNotificationSound.text = data.namazDetail.notificationSound
            binding.textViewReminderSound.text = data.namazDetail.reminderNotificationSound
            binding.textViewSetTime.text = data.namazDetail.reminderTimeMinutes
            binding.textViewDuaSetTime.text = data.namazDetail.duaReminderMinutes

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
                binding.textViewSetTime.text = incrementReminderTimeMinutes()
                prayerDetailData?.reminderTimeMinutes = binding.textViewSetTime.text.toString()
                if (binding.textViewSetTime.text.toString() != "off" && binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.reminderTime = subtractMinutesFromTime(data.time, reminderTimeMinutes)
                }
                savePrayerDetailData()
            }

            binding.imageViewRemove.setOnClickListener {
                binding.textViewSetTime.text = decrementReminderTimeMinutes()
                prayerDetailData?.reminderTimeMinutes = binding.textViewSetTime.text.toString()
                if (binding.textViewSetTime.text.toString() != "off" && binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.reminderTime =
                        subtractMinutesFromTime(data.time, reminderTimeMinutes)
                }
                savePrayerDetailData()
            }

            binding.imageViewDuaMinus.setOnClickListener {
                binding.textViewDuaSetTime.text = decrementDuaReminderMinutes()
                prayerDetailData?.duaReminderMinutes = binding.textViewDuaSetTime.text.toString()
                if (binding.textViewSetTime.text.toString() != "off" && binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.duaTime =
                        addMinutesToTime(data.time, duaReminderTimeMinutes)
                } else {
                    prayerDetailData?.duaType = DuaTypeEnum.OFF.getValue()
                }
                savePrayerDetailData()
            }

            binding.imageViewDuaAdd.setOnClickListener {
                binding.textViewDuaSetTime.text = incrementDuaReminderMinutes()
                prayerDetailData?.duaReminderMinutes = binding.textViewDuaSetTime.text.toString()
                if (binding.textViewSetTime.text.toString() != "off" && binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.duaTime =
                        addMinutesToTime(data.time, duaReminderTimeMinutes)
                } else {
                    prayerDetailData?.duaType = DuaTypeEnum.OFF.getValue()
                }
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
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                    )
                )

            }

            "Sunrise" -> {
                viewModel.saveSunriseDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                    )
                )

            }

            "Dhuhr" -> {
                viewModel.saveDuhrDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"

                        )
                )

            }

            "Asr" -> {
                viewModel.saveAsrDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                        )
                )

            }

            "Maghrib" -> {
                viewModel.saveMagribDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                    )
                )

            }

            "Isha" -> {
                viewModel.saveIshaDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                    )
                )

            }

            "Midnight" -> {
                viewModel.saveMidNightDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                    )
                )

            }

            "Last Third" -> {
                viewModel.saveLastNightDetail(
                    NotificationData(
                        namazName = data.title,
                        namazTime = data.time,
                        notificationSound = prayerDetailData?.notificationSound?:"Tones",
                        duaReminderMinutes = prayerDetailData?.duaReminderMinutes?:"off",
                        reminderNotificationSound = prayerDetailData?.reminderNotificationSound?:"Tones",
                        duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
                        sound = prayerDetailData?.sound?:0,
                        duaType = prayerDetailData?.duaType?:"off",
                        isForNotification = prayerDetailData?.isForNotification ?: true,
                        createdDate = getCurrentDate(),
                        reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes?: "off",
                        reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM"
                    )
                )

            }
        }
    }

    private fun incrementReminderTimeMinutes(): String {
        reminderTimeMinutes++
        return "$reminderTimeMinutes min"
    }

    private fun decrementReminderTimeMinutes(): String {

        return if (reminderTimeMinutes > 0) {
            reminderTimeMinutes--
            "$reminderTimeMinutes min"
        } else {
            "off"
        }

    }

    private fun incrementDuaReminderMinutes(): String {
        duaReminderTimeMinutes++
        return "$duaReminderTimeMinutes min"
    }

    private fun decrementDuaReminderMinutes(): String {

        return if (duaReminderTimeMinutes > 0) {
            duaReminderTimeMinutes--
            "$duaReminderTimeMinutes min"
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
                binding.textViewDuaSetTime.text = "${prayerDetailData?.duaReminderMinutes} mins"
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
        if (isForNotification) {
            soundDialog.selectedSound = data.namazDetail.sound ?: 0
        } else {
            soundDialog.selectedSound = data.namazDetail.reminderSound ?: 0
        }
        soundDialog.show(fragment.childFragmentManager, "SoundDialogFragment")
    }

    override fun onDataPassed(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        isSoundForNotification: Boolean
    ) {
        if (isSoundForNotification) {
            prayerDetailData?.notificationSound = soundName
            prayerDetailData?.sound = sound
            _binding.textViewNotificationSound.text = soundName
        } else {
            _binding.textViewReminderSound.text = soundName
            prayerDetailData?.reminderNotificationSound = soundName
            prayerDetailData?.reminderSound = sound
        }
        prayerDetailData?.isForNotification = isSoundForNotification

        savePrayerDetailData()
    }

    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy") // Customize the format as needed
        return currentDate.format(formatter)
    }

    fun subtractMinutesFromTime(currentTime: String, minutesToSubtract: Int): String {
        // Parse the current time string
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val parsedTime = LocalTime.parse(currentTime, formatter)

        // Subtract minutes from the parsed time
        val resultTime = parsedTime.minusMinutes(minutesToSubtract.toLong())

        // Format the result time back to "hh:mm a" format
        return resultTime.format(formatter)
    }

    fun addMinutesToTime(currentTime: String, minutesToAdd: Int): String {
        // Parse the current time string
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        val parsedTime = LocalTime.parse(currentTime, formatter)

        // Add minutes to the parsed time
        val resultTime = parsedTime.plusMinutes(minutesToAdd.toLong())

        // Format the result time back to "hh:mm a" format
        return resultTime.format(formatter)
    }
}
