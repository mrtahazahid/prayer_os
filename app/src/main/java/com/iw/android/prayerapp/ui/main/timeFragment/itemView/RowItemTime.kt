package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding
import com.iw.android.prayerapp.extension.CustomDialog
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import com.iw.android.prayerapp.utils.SoundDataPass
import com.iw.android.prayerapp.utils.SoundSelectionDialog
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class RowItemTime(
    private val data: PrayTime,
    val recyclerView: RecyclerView,
    val activity: FragmentActivity,
    val viewModel: TimeViewModel
) : ViewType<PrayTime>, SoundDataPass, View.OnClickListener {
    private var isViewShow = false
    private var prayerDetailData: NotificationData? = null
    private var reminderTimeMinutes = 0
    private var secondReminderTimeMinutes = 0
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
            prayerDetailData = data.namazDetail
            initialize()
            setOnClickListener()

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            _binding.imageViewNotificationHelp.id -> {
                CustomDialog(
                    _binding.imageViewNotificationHelp.context,
                    "Notification sound",
                    "The notification sound to play when \n the time has been reached."
                ).show()
            }

            _binding.imageViewReminderHelp.id -> {
                CustomDialog(
                    _binding.imageViewReminderHelp.context,
                    "Reminder sound",
                    "The notification sound to play when \n the time approaches, or a snooze reminder was set."
                ).show()
            }

            _binding.imageViewSecondReminderTimeHelp.id -> {
                CustomDialog(
                    _binding.imageViewReminderHelp.context,
                    "Second reminder",
                    "The number of mintues to remind you \n to finish sahoor before Fajr starts."
                ).show()
            }

            _binding.imageViewReminderTimeHelp.id -> {
                CustomDialog(
                    _binding.imageViewReminderTimeHelp.context,
                    "Reminder time",
                    "The number of minutes to remind you\n before Fajr starts, usually for Qiyam-ul-\n layl or sahoor."
                ).show()
            }


            _binding.imageViewDuaHelp.id -> {
                CustomDialog(
                    _binding.imageViewDuaHelp.context,
                    "Duha",
                    "The time or number of minutes to \n remind you to pray duha after sunrise."
                ).show()
            }

            _binding.mainView.id -> {
                if (!isViewShow) {
                    _binding.imageViewDropDownMenu.setImageDrawable(ContextCompat.getDrawable(_binding.textViewTime.context,R.drawable.ic_down))
                    _binding.detailViews.visibility = View.VISIBLE
                    recyclerView.smoothScrollToPosition(0)
                    _binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            _binding.textViewTime.context,
                            R.color.yellow_text
                        )
                    )
                    isViewShow = true
                } else {
                    _binding.detailViews.visibility = View.GONE
                    _binding.imageViewDropDownMenu.setImageDrawable(ContextCompat.getDrawable(_binding.textViewTime.context,R.drawable.ic_forward))
                    _binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            _binding.textViewTime.context,
                            R.color.white
                        )
                    )
                    isViewShow = false
                }
            }

            _binding.imageViewDropDownMenu.id -> {
                if (!isViewShow) {
                    _binding.imageViewDropDownMenu.setImageDrawable(ContextCompat.getDrawable(_binding.textViewTime.context,R.drawable.ic_down))
                    _binding.detailViews.visibility = View.VISIBLE
                    recyclerView.smoothScrollToPosition(0)
                    _binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            _binding.textViewTime.context,
                            R.color.yellow_text
                        )
                    )
                    isViewShow = true
                } else {
                    _binding.detailViews.visibility = View.GONE
                    _binding.imageViewDropDownMenu.setImageDrawable(ContextCompat.getDrawable(_binding.textViewTime.context,R.drawable.ic_forward))
                    _binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            _binding.textViewTime.context,
                            R.color.white
                        )
                    )
                    isViewShow = false
                }
            }

            _binding.imageViewAdd.id -> {
                _binding.textViewSetTime.text = incrementReminderTimeMinutes()
                prayerDetailData?.reminderTimeMinutes = _binding.textViewSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.reminderTime =
                        subtractMinutesFromTime(data.time, reminderTimeMinutes)
                }
                savePrayerDetailData()
            }

            _binding.imageViewRemove.id -> {
                _binding.textViewSetTime.text = decrementReminderTimeMinutes()
                prayerDetailData?.reminderTimeMinutes = _binding.textViewSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.reminderTime =
                        subtractMinutesFromTime(data.time, reminderTimeMinutes)
                }
                savePrayerDetailData()
            }

            _binding.imageViewSecondReminderAdd.id -> {
                _binding.textViewSecondReminderSetTime.text = incrementSecondReminderTimeMinutes()
                prayerDetailData?.secondReminderTimeMinutes =
                    _binding.textViewSecondReminderSetTime.text.toString()
                if (_binding.textViewSecondReminderSetTime.text.toString() != "off" && _binding.textViewSecondReminderSetTime.text.toString() != "0 min") {
                    prayerDetailData?.secondReminderTime =
                        subtractMinutesFromTime(data.time, secondReminderTimeMinutes)
                }
                savePrayerDetailData()
            }

            _binding.imageViewSecondReminderRemove.id -> {
                _binding.textViewSecondReminderSetTime.text = decrementSecondReminderTimeMinutes()
                prayerDetailData?.secondReminderTimeMinutes =
                    _binding.textViewSecondReminderSetTime.text.toString()
                if (_binding.textViewSecondReminderSetTime.text.toString() != "off" && _binding.textViewSecondReminderSetTime.text.toString() != "0 min") {
                    prayerDetailData?.secondReminderTime =
                        subtractMinutesFromTime(data.time, secondReminderTimeMinutes)
                }
                savePrayerDetailData()
            }

            _binding.imageViewDuaMinus.id -> {
                _binding.textViewDuaSetTime.text = decrementDuaReminderMinutes()
                prayerDetailData?.duaReminderMinutes = _binding.textViewDuaSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.duaTime =
                        addMinutesToTime(data.time, duaReminderTimeMinutes)
                } else {
                    prayerDetailData?.duaType = DuaTypeEnum.OFF.getValue()
                }
                savePrayerDetailData()
            }

            _binding.imageViewDuaAdd.id -> {
                _binding.textViewDuaSetTime.text = incrementDuaReminderMinutes()
                prayerDetailData?.duaReminderMinutes = _binding.textViewDuaSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.duaTime =
                        addMinutesToTime(data.time, duaReminderTimeMinutes)
                } else {
                    prayerDetailData?.duaType = DuaTypeEnum.OFF.getValue()
                }
                savePrayerDetailData()
            }

            _binding.cardViewDuaTime.id -> {
                openTimePicker(_binding.cardViewDuaTime.context, 12, 0) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("h:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    _binding.textViewDuaTime.text = formattedTime
                    prayerDetailData?.duaTime = formattedTime
                    savePrayerDetailData()
                }
            }

            _binding.textViewNotificationSound.id -> {
                openSoundDialogFragment(data.title, true)
            }

            _binding.textViewReminderSound.id -> {
                openSoundDialogFragment(data.title, false)
            }
        }
    }

    private fun initialize() {

        when (data.title) {
            "Sunrise" -> {
                _binding.group.visibility = View.VISIBLE
                _binding.groupFajr.visibility = View.GONE
            }

            "Fajr" -> {
                _binding.group.visibility = View.GONE
                _binding.groupFajr.visibility = View.VISIBLE
            }

            else -> {
                _binding.group.visibility = View.GONE
                _binding.groupFajr.visibility = View.GONE
            }
        }
        _binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
        _binding.view5.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE

        val color = if (data.isCurrentNamaz) ContextCompat.getColorStateList(
            _binding.textViewTime.context,
            R.color.yellow_text
        ) else ContextCompat.getColorStateList(
            _binding.textViewTime.context,
            R.color.text_color_gray
        )

        _binding.textViewTime.setTextColor(color)

        _binding.imageView.setImageResource(data.image)
        _binding.textViewTitle.text = data.title
        _binding.textViewTime.text = data.time
        //  _binding.textViewNotificationSound.text = data.namazDetail.notificationSound?.soundName
        // _binding.textViewReminderSound.text = data.namazDetail.reminderSound?.soundName
        _binding.textViewSetTime.text = data.namazDetail.reminderTimeMinutes
        _binding.textViewSecondReminderSetTime.text = data.namazDetail.secondReminderTimeMinutes
        _binding.textViewNotificationSound.text =
            if (data.namazDetail.notificationSound?.isForAdhan == true) data.namazDetail.notificationSound?.soundName
                ?: "Adhan" else data.namazDetail.notificationSound?.soundToneName ?: "Adhan"
        if (data.namazDetail.notificationSound?.isSoundSelected == false) {
            _binding.textViewNotificationSound.text =
                if (data.namazDetail.notificationSound?.isForAdhan == true) data.namazDetail.notificationSound?.soundName
                    ?: "Adhan" else data.namazDetail.notificationSound?.soundToneName ?: "Adhan"
        } else {
            if (data.namazDetail.notificationSound?.isSilent == true) {
                _binding.textViewNotificationSound.text = "Silent"
            }
            if (data.namazDetail.notificationSound?.isVibrate == true) {
                _binding.textViewNotificationSound.text = "Vibrate"
            }
            if (data.namazDetail.notificationSound?.isOff == true) {
                _binding.textViewNotificationSound.text = "Off"
            }
        }
        _binding.textViewReminderSound.text =
            if (data.namazDetail.reminderSound?.isForAdhan == true) data.namazDetail.reminderSound?.soundName
                ?: "Tone" else data.namazDetail.reminderSound?.soundToneName ?: "Tone"
        if (data.namazDetail.reminderSound?.isSoundSelected == false) {
            _binding.textViewReminderSound.text =
                if (data.namazDetail.reminderSound?.isForAdhan == true) data.namazDetail.reminderSound?.soundName
                    ?: "Tone" else data.namazDetail.reminderSound?.soundToneName ?: "Tone"
        } else {

            if (data.namazDetail.reminderSound?.isSilent == true) {
                _binding.textViewReminderSound.text = "Silent"
            }
            if (data.namazDetail.reminderSound?.isVibrate == true) {
                _binding.textViewReminderSound.text = "Vibrate"
            }
            if (data.namazDetail.reminderSound?.isOff == true) {
                _binding.textViewReminderSound.text = "Off"
            }
        }


        _binding.textViewDuaSetTime.text = data.namazDetail.duaReminderMinutes
        spinnerDua()
    }

    private fun setOnClickListener() {
        _binding.imageViewNotificationHelp.setOnClickListener(this)
        _binding.imageViewReminderHelp.setOnClickListener(this)
        _binding.imageViewReminderTimeHelp.setOnClickListener(this)
        _binding.imageViewDuaHelp.setOnClickListener(this)
        _binding.mainView.setOnClickListener(this)
        _binding.imageViewDropDownMenu.setOnClickListener(this)
        _binding.imageViewAdd.setOnClickListener(this)
        _binding.imageViewSecondReminderAdd.setOnClickListener(this)
        _binding.imageViewSecondReminderRemove.setOnClickListener(this)
        _binding.imageViewRemove.setOnClickListener(this)
        _binding.imageViewDuaMinus.setOnClickListener(this)
        _binding.imageViewDuaAdd.setOnClickListener(this)
        _binding.imageViewSecondReminderTimeHelp.setOnClickListener(this)
        _binding.cardViewDuaTime.setOnClickListener(this)
        _binding.textViewNotificationSound.setOnClickListener(this)
        _binding.textViewReminderSound.setOnClickListener(this)

    }


    private fun savePrayerDetailData() {
        val saveData = NotificationData(
            namazName = data.title,
            namazTime = data.time,
            notificationSound = prayerDetailData?.notificationSound,
            reminderSound = prayerDetailData?.reminderSound,
            reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes ?: "off",
            reminderTime = prayerDetailData?.reminderTime ?: "12:00 AM",
            secondReminderTimeMinutes = prayerDetailData?.reminderTimeMinutes ?: "off",
            secondReminderTime = prayerDetailData?.reminderTime ?: "12:00 AM",
            duaReminderMinutes = prayerDetailData?.duaReminderMinutes ?: "off",
            duaTime = prayerDetailData?.duaTime ?: "12:00 AM",
            duaType = prayerDetailData?.duaType ?: "off",
            createdDate = getCurrentDate(),
        )
        
        when (data.title) {
            "Fajr" -> viewModel.saveFajrDetail(saveData)

            "Sunrise" -> viewModel.saveSunriseDetail(saveData)

            "Dhuhr" -> viewModel.saveDuhrDetail(saveData)

            "Asr" -> viewModel.saveAsrDetail(saveData)

            "Maghrib" -> viewModel.saveMagribDetail(saveData)

            "Isha" -> viewModel.saveIshaDetail(saveData)

            "Midnight" -> viewModel.saveMidNightDetail(saveData)

            "Last Third" -> viewModel.saveLastNightDetail(saveData)
        }
    }


    private fun openTimePicker(
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
            R.style.DialogTheme, // Apply the custom theme here
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                onTimeSetListener(hourOfDay, minute)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false // Set to true for 24-hour format, false for 12-hour format with AM/PM
        )

        // Customize the TimePickerDialog
        timePickerDialog.setTitle("Duha Time")

        timePickerDialog.setOnShowListener {
            // Get the button instances
            val cancelButton = timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            val okButton = timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE)

            // Set the text color using your custom color
            cancelButton.setTextColor(ContextCompat.getColor(context, R.color.app_green))
            okButton.setTextColor(ContextCompat.getColor(context, R.color.app_green))
        }
        timePickerDialog.show()
    }

    private fun openSoundDialogFragment(
        namazName: String,
        isForNotification: Boolean
    ) {
        val soundDialog = SoundSelectionDialog()
        soundDialog.listener = this
        soundDialog.namazName = namazName
        if (isForNotification) {
            soundDialog.selectedSound =
                data.namazDetail.notificationSound?.sound ?: R.raw.adhan_abdul_basit
            soundDialog.selectedSoundAdhanName =
                data.namazDetail.notificationSound?.soundName ?: "Adhan"
            soundDialog.selectedSoundToneName =
                data.namazDetail.notificationSound?.soundToneName ?: "Tone"
            soundDialog.selectedPosition = data.namazDetail.notificationSound?.sound ?: 0
            soundDialog.isForAdhan = data.namazDetail.notificationSound?.isForAdhan ?: false
            soundDialog.isVibrateSelected = data.namazDetail.notificationSound?.isVibrate ?: false
            soundDialog.isOffSelected = data.namazDetail.notificationSound?.isOff ?: false
            soundDialog.isSoundSelected =
                data.namazDetail.notificationSound?.isSoundSelected ?: false
            soundDialog.isSilentSelected = data.namazDetail.notificationSound?.isSilent ?: false
        } else {
            soundDialog.selectedSound =
                data.namazDetail.reminderSound?.sound ?: R.raw.adhan_abdul_basit
            soundDialog.selectedSoundAdhanName =
                data.namazDetail.reminderSound?.soundName ?: "Adhan"
            soundDialog.selectedSoundToneName =
                data.namazDetail.reminderSound?.soundToneName ?: "Tone"
            soundDialog.selectedPosition = data.namazDetail.reminderSound?.sound ?: 0
            soundDialog.isForAdhan = data.namazDetail.reminderSound?.isForAdhan ?: false
            soundDialog.isVibrateSelected = data.namazDetail.reminderSound?.isVibrate ?: false
            soundDialog.isOffSelected = data.namazDetail.reminderSound?.isOff ?: false
            soundDialog.isSoundSelected = data.namazDetail.reminderSound?.isSoundSelected ?: false
            soundDialog.isSilentSelected = data.namazDetail.reminderSound?.isSilent ?: false
        }


        if (isForNotification) {
            soundDialog.selectedSound = data.namazDetail.notificationSound?.position ?: 0
        } else {
            soundDialog.selectedSound = data.namazDetail.reminderSound?.position ?: 0
        }
        soundDialog.isForNotification = isForNotification
        soundDialog.show(activity.supportFragmentManager, "SoundDialogFragment")
    }

    private fun spinnerDua() {

        val adapter = ArrayAdapter.createFromResource(
            _binding.textViewTime.context,
            R.array.dua,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        _binding.spinnerDuaReminderSwitch.adapter = adapter


        when (data.namazDetail.duaType) {
            DuaTypeEnum.OFF.getValue() -> {
                _binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                _binding.cardViewDuaTime.visibility = View.GONE
                _binding.textViewDuaSetTime.visibility = View.GONE
                _binding.spinnerDuaReminderSwitch.setSelection(0)
            }

            DuaTypeEnum.MINUTES.getValue() -> {
                _binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                _binding.cardViewDuaTime.visibility = View.VISIBLE
                _binding.textViewDuaSetTime.visibility = View.GONE
                _binding.textViewDuaSetTime.text = "${prayerDetailData?.duaReminderMinutes} mins"
                _binding.spinnerDuaReminderSwitch.setSelection(2)

            }

            DuaTypeEnum.TIME.getValue() -> {
                _binding.cardViewDuaAdjustmentTime.visibility = View.VISIBLE
                _binding.cardViewDuaTime.visibility = View.GONE
                _binding.textViewDuaSetTime.visibility = View.VISIBLE
                _binding.textViewDuaTime.text = prayerDetailData?.duaTime
                _binding.spinnerDuaReminderSwitch.setSelection(1)

            }
        }


        _binding.spinnerDuaReminderSwitch.onItemSelectedListener = object :
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
                        _binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                        _binding.cardViewDuaTime.visibility = View.GONE
                        _binding.textViewDuaSetTime.visibility = View.GONE
                    }

                    1 -> {
                        _binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                        _binding.cardViewDuaTime.visibility = View.VISIBLE
                        _binding.textViewDuaSetTime.visibility = View.GONE
                    }

                    2 -> {
                        _binding.cardViewDuaAdjustmentTime.visibility = View.VISIBLE
                        _binding.cardViewDuaTime.visibility = View.GONE
                        _binding.textViewDuaSetTime.visibility = View.VISIBLE
                    }
                }
                prayerDetailData?.duaType = selectedItem
                savePrayerDetailData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


    fun getCurrentDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy") // Customize the format as needed
        return currentDate.format(formatter)
    }

    fun isDateToday(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val formattedCurrentDate = dateFormat.format(currentDate)

        return formattedCurrentDate == dateString
    }

    fun subtractMinutesFromTime(currentTime: String, minutesToSubtract: Int): String {
        // Parse the current time string
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
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

    private fun incrementReminderTimeMinutes(): String {
        reminderTimeMinutes++
        return "$reminderTimeMinutes min"
    }

    private fun decrementReminderTimeMinutes(): String {
        reminderTimeMinutes--
        return if (reminderTimeMinutes > 0) {
            "$reminderTimeMinutes min"
        } else {
            "off"
        }
    }


    private fun incrementSecondReminderTimeMinutes(): String {
        secondReminderTimeMinutes++
        return "$secondReminderTimeMinutes min"
    }

    private fun decrementSecondReminderTimeMinutes(): String {
        secondReminderTimeMinutes--
        return if (secondReminderTimeMinutes > 0) {

            "$secondReminderTimeMinutes min"
        } else {
            "off"
        }
    }

    private fun incrementDuaReminderMinutes(): String {
        duaReminderTimeMinutes++
        return "$duaReminderTimeMinutes min"
    }

    private fun decrementDuaReminderMinutes(): String {
        duaReminderTimeMinutes--
        return if (duaReminderTimeMinutes > 0) {
            "$duaReminderTimeMinutes min"
        } else {
            "off"
        }
    }

    override fun onDataPass(data: CurrentNamazNotificationData, isForNotification: Boolean) {
        if (isForNotification) {
            prayerDetailData?.notificationSound = data
            if (data.isSoundSelected) {
                _binding.textViewNotificationSound.text =
                    if (data.isForAdhan) data.soundName else data.soundToneName
            } else {
                data.soundName = "Adhan"
                data.soundToneName = "Tones"
                if (data.isSilent) {
                    _binding.textViewNotificationSound.text = "Silent"
                }
                if (data.isVibrate) {
                    _binding.textViewNotificationSound.text = "Vibrate"
                }
                if (data.isOff) {
                    _binding.textViewNotificationSound.text = "Off"
                }
            }

        } else {
            prayerDetailData?.reminderSound = data
            if (data.isSoundSelected) {
                _binding.textViewReminderSound.text =
                    if (data.isForAdhan) data.soundName else data.soundToneName
            } else {
                data.soundName = "Adhan"
                data.soundToneName = "Tones"
                if (data.isSilent) {
                    _binding.textViewReminderSound.text = "Silent"
                }
                if (data.isVibrate) {
                    _binding.textViewReminderSound.text = "Vibrate"
                }
                if (data.isOff) {
                    _binding.textViewReminderSound.text = "Off"
                }
            }
        }
        savePrayerDetailData()
    }


}
