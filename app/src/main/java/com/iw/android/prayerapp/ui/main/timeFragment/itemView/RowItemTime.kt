package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import android.annotation.SuppressLint
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
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
import com.iw.android.prayerapp.utils.anim.hideDetailView
import com.iw.android.prayerapp.utils.anim.showDetailView
import com.iw.android.prayerapp.utils.datePicker.openTimePicker
import com.iw.android.prayerapp.utils.sound.SoundDataPass
import com.iw.android.prayerapp.utils.sound.SoundSelectionDialog
import com.iw.android.prayerapp.utils.time.addMinutesToTime
import com.iw.android.prayerapp.utils.time.extractNumberFromString
import com.iw.android.prayerapp.utils.time.subtractMinutesFromTime
import java.text.SimpleDateFormat
import java.util.Calendar

class RowItemTime(
    private val data: PrayTime,
    val recyclerView: RecyclerView,
    val activity: FragmentActivity,
    val listener: OnTimeDataSave
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
            _binding.imageView.setImageResource(data.image)
            setIconByDataType()
            reminderTimeMinutes = extractNumberFromString(data.namazDetail.reminderTimeMinutes)
            prayerDetailData = data.namazDetail
            initialize()
            setOnClickListener()

        }
    }

    @SuppressLint("SimpleDateFormat")
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
                isViewShow = if (!isViewShow) {
                    showDetailView(_binding.detailViews, _binding.imageViewDropDownMenu)
                    //toggleDropDown(_binding.detailViews, true)
                    recyclerView.smoothScrollToPosition(0)
                    true
                } else {
                    hideDetailView(_binding.detailViews, _binding.imageViewDropDownMenu)
                    false
                }
            }

            _binding.imageViewDropDownMenu.id -> {
                isViewShow = if (!isViewShow) {
                    showDetailView(_binding.detailViews, _binding.imageViewDropDownMenu)
                    recyclerView.smoothScrollToPosition(0)
                    true
                } else {
                    hideDetailView(_binding.detailViews, _binding.imageViewDropDownMenu)
                    false
                }
            }

            _binding.imageViewAdd.id -> {
                _binding.textViewSetTime.text = incrementReminderTimeMinutes()
                prayerDetailData?.reminderTimeMinutes = _binding.textViewSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.reminderTime =
                        subtractMinutesFromTime(data.time, reminderTimeMinutes)
                }
                setIconByDataType()
                listener.onSave(prayerDetailData,this.data.title,this.data.time)
            }

            _binding.imageViewRemove.id -> {
                _binding.textViewSetTime.text = decrementReminderTimeMinutes()
                prayerDetailData?.reminderTimeMinutes = _binding.textViewSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.reminderTime =
                        subtractMinutesFromTime(data.time, reminderTimeMinutes)
                }
                setIconByDataType()
                listener.onSave(prayerDetailData,this.data.title,this.data.time)
            }

            _binding.imageViewSecondReminderAdd.id -> {
                _binding.textViewSecondReminderSetTime.text =
                    incrementSecondReminderTimeMinutes()
                prayerDetailData?.secondReminderTimeMinutes =
                    _binding.textViewSecondReminderSetTime.text.toString()
                if (_binding.textViewSecondReminderSetTime.text.toString() != "off" && _binding.textViewSecondReminderSetTime.text.toString() != "0 min") {
                    prayerDetailData?.secondReminderTime =
                        subtractMinutesFromTime(data.time, secondReminderTimeMinutes)
                }
                listener.onSave(prayerDetailData,this.data.title,this.data.time)
            }

            _binding.imageViewSecondReminderRemove.id -> {
                _binding.textViewSecondReminderSetTime.text =
                    decrementSecondReminderTimeMinutes()
                prayerDetailData?.secondReminderTimeMinutes =
                    _binding.textViewSecondReminderSetTime.text.toString()
                if (_binding.textViewSecondReminderSetTime.text.toString() != "off" && _binding.textViewSecondReminderSetTime.text.toString() != "0 min") {
                    prayerDetailData?.secondReminderTime =
                        subtractMinutesFromTime(data.time, secondReminderTimeMinutes)
                }
                listener.onSave(prayerDetailData,this.data.title,this.data.time)
            }

            _binding.imageViewDuaMinus.id -> {
                _binding.textViewDuaSetTime.text = decrementDuaReminderMinutes()
                prayerDetailData?.duaReminderMinutes =
                    _binding.textViewDuaSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.duaTime =
                        addMinutesToTime(data.time, duaReminderTimeMinutes)
                } else {
                    prayerDetailData?.duaType = DuaTypeEnum.OFF.getValue()
                }
                listener.onSave(prayerDetailData,this.data.title,this.data.time)
            }

            _binding.imageViewDuaAdd.id -> {
                _binding.textViewDuaSetTime.text = incrementDuaReminderMinutes()
                prayerDetailData?.duaReminderMinutes =
                    _binding.textViewDuaSetTime.text.toString()
                if (_binding.textViewSetTime.text.toString() != "off" && _binding.textViewSetTime.text.toString() != "0 min") {
                    prayerDetailData?.duaTime =
                        addMinutesToTime(data.time, duaReminderTimeMinutes)
                } else {
                    prayerDetailData?.duaType = DuaTypeEnum.OFF.getValue()
                }
                listener.onSave(prayerDetailData,this.data.title,this.data.time)
            }

            _binding.cardViewDuaTime.id -> {
                openTimePicker(_binding.cardViewDuaTime.context ) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("h:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    _binding.textViewDuaTime.text = formattedTime
                    prayerDetailData?.duaTime = formattedTime
                    listener.onSave(prayerDetailData,this.data.title,this.data.time)
                }
            }

            _binding.notificationSelectionView.id -> {
                openSoundDialogFragment(data.title, true)
            }

            _binding.reminderSelectionView.id -> {
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
        _binding.textViewTitle.text = data.title
        _binding.textViewTime.text = data.time
        _binding.textViewSetTime.text = data.namazDetail.reminderTimeMinutes
        _binding.textViewSecondReminderSetTime.text = data.namazDetail.secondReminderTimeMinutes
        _binding.textViewNotificationSound.text =
            if (data.namazDetail.notificationSound?.isForAdhan == true) data.namazDetail.notificationSound?.soundName
            else data.namazDetail.notificationSound?.soundToneName
        _binding.textViewReminderSound.text = data.namazDetail.reminderSound?.soundToneName

        if (data.namazDetail.notificationSound?.isSoundSelected == false) {
            setSoundTextToNonSoundSelect(
                data.namazDetail.notificationSound,
                _binding.textViewNotificationSound
            )
        }

        if (data.namazDetail.reminderSound?.isSoundSelected == false) {
            setSoundTextToNonSoundSelect(
                data.namazDetail.reminderSound,
                _binding.textViewReminderSound
            )
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
        _binding.notificationSelectionView.setOnClickListener(this)
        _binding.reminderSelectionView.setOnClickListener(this)
    }


    private fun openSoundDialogFragment(
        namazName: String,
        isForNotification: Boolean
    ) {
        val soundDialog = SoundSelectionDialog()
        soundDialog.listener = this
        soundDialog.namazName = namazName
        if (isForNotification) {
            soundDialog.selectedSoundAdhanName =
                data.namazDetail.notificationSound?.soundName ?: "Adhan"
            soundDialog.selectedSoundToneName =
                data.namazDetail.notificationSound?.soundToneName ?: "Tone"
            soundDialog.isForAdhan = data.namazDetail.notificationSound?.isForAdhan ?: false
            soundDialog.isVibrateSelected =
                data.namazDetail.notificationSound?.isVibrate ?: false
            soundDialog.isOffSelected = data.namazDetail.notificationSound?.isOff ?: false
            soundDialog.selectedItemPosition =
                data.namazDetail.notificationSound?.selectedSoundItemPosition ?: 0
            soundDialog.selectedSoundPosition =
                data.namazDetail.notificationSound?.selectedSoundPosition ?: 0
            soundDialog.selectedSoundTonePosition =
                data.namazDetail.notificationSound?.selectedSoundTonePosition ?: 0
            soundDialog.isSoundSelected =
                data.namazDetail.notificationSound?.isSoundSelected ?: false
            soundDialog.isSilentSelected = data.namazDetail.notificationSound?.isSilent ?: false
            if (data.namazDetail.notificationSound?.selectedSoundItemPosition == 0) {
                soundDialog.selectedSound = data.namazDetail.notificationSound?.soundAdhan ?: 0
            } else {
                soundDialog.selectedSound = data.namazDetail.notificationSound?.soundTone ?: 0
            }
        } else {
            soundDialog.selectedSoundAdhanName =
                data.namazDetail.reminderSound?.soundName ?: "Adhan"
            soundDialog.selectedSoundToneName =
                data.namazDetail.reminderSound?.soundToneName ?: "Tone"
            soundDialog.isForAdhan = data.namazDetail.reminderSound?.isForAdhan ?: false
            soundDialog.selectedItemPosition =
                data.namazDetail.reminderSound?.selectedSoundItemPosition ?: 0
            soundDialog.isVibrateSelected = data.namazDetail.reminderSound?.isVibrate ?: false
            soundDialog.selectedSoundPosition =
                data.namazDetail.reminderSound?.selectedSoundPosition ?: 0
            soundDialog.selectedSoundTonePosition =
                data.namazDetail.reminderSound?.selectedSoundTonePosition ?: 0
            soundDialog.isOffSelected = data.namazDetail.reminderSound?.isOff ?: false
            soundDialog.isSoundSelected =
                data.namazDetail.reminderSound?.isSoundSelected ?: false
            soundDialog.isSilentSelected = data.namazDetail.reminderSound?.isSilent ?: false
            if (data.namazDetail.notificationSound?.selectedSoundItemPosition == 0) {
                soundDialog.selectedSound = data.namazDetail.reminderSound?.soundAdhan ?: 0
            } else {
                soundDialog.selectedSound = data.namazDetail.reminderSound?.soundTone ?: 0
            }

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
                val setDuaTime =  "${prayerDetailData?.duaReminderMinutes} mins"
                _binding.cardViewDuaAdjustmentTime.visibility = View.GONE
                _binding.cardViewDuaTime.visibility = View.VISIBLE
                _binding.textViewDuaSetTime.visibility = View.GONE
                _binding.textViewDuaSetTime.text = setDuaTime
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
                listener.onSave(prayerDetailData,data.title,data.time)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun incrementReminderTimeMinutes(): String {
        reminderTimeMinutes++
        return "$reminderTimeMinutes min"
    }

    private fun decrementReminderTimeMinutes(): String {
        return if (reminderTimeMinutes > 1) {
            reminderTimeMinutes--
            "$reminderTimeMinutes min"
        } else {
            reminderTimeMinutes = 0
            "off"
        }
    }


    private fun incrementSecondReminderTimeMinutes(): String {
        secondReminderTimeMinutes++
        return "$secondReminderTimeMinutes min"
    }

    private fun decrementSecondReminderTimeMinutes(): String {
        return if (secondReminderTimeMinutes > 1) {
            secondReminderTimeMinutes--
            "$secondReminderTimeMinutes min"
        } else {
            secondReminderTimeMinutes = 0
            "off"
        }
    }

    private fun incrementDuaReminderMinutes(): String {
        duaReminderTimeMinutes++
        return "$duaReminderTimeMinutes min"
    }

    private fun decrementDuaReminderMinutes(): String {
        return if (duaReminderTimeMinutes > 1) {
            duaReminderTimeMinutes--
            "$duaReminderTimeMinutes min"
        } else {
            duaReminderTimeMinutes = 0
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
                setSoundTextToNonSoundSelect(data,_binding.textViewNotificationSound)
            }

        } else {
            prayerDetailData?.reminderSound = data
            if (data.isSoundSelected) {
                _binding.textViewReminderSound.text =
                    if (data.isForAdhan) data.soundName else data.soundToneName
            } else {
                data.soundName = "Adhan"
                data.soundToneName = "Tones"
                setSoundTextToNonSoundSelect(data,_binding.textViewReminderSound)

            }
        }
        setIconByDataType()
        listener.onSave(prayerDetailData,this.data.title,this.data.time)
    }

    private fun setSoundTextToNonSoundSelect(
        data: CurrentNamazNotificationData?,
        textView: TextView
    ) {
        if (data?.isSilent == true) {
            textView.text = textView.context.getString(R.string.silent)
        }
        if (data?.isVibrate == true) {
            textView.text = textView.context.getString(R.string.vibrate)
        }
        if (data?.isOff == true) {
            textView.text = textView.context.getString(R.string.off)
        }
    }
    private fun setIconByDataType() {
        if (data.title == "Midnight" || data.title == "Last Third") {
            if (data.namazDetail.notificationSound != null) {
                if (data.namazDetail.notificationSound?.selectedSoundItemPosition == 1 || data.namazDetail.notificationSound?.selectedSoundItemPosition == 0) {
                    _binding.imageView.setImageResource(R.drawable.ic_mike)
                }

                if (data.namazDetail.notificationSound!!.isSilent) {
                    _binding.imageView.setImageResource(R.drawable.ic_mute_mike)
                }

                if (data.namazDetail.notificationSound!!.isOff) {
                    _binding.imageView.setImageResource(R.drawable.ic_off)
                }

                if (data.namazDetail.notificationSound!!.isVibrate) {
                    _binding.imageView.setImageResource(R.drawable.ic_vibrate)
                }


            } else if (data.namazDetail.reminderSound != null) {
                if (data.namazDetail.reminderSound?.selectedSoundItemPosition == 1 || data.namazDetail.reminderSound?.selectedSoundItemPosition == 0) {
                    _binding.imageView.setImageResource(R.drawable.ic_mike)
                }

                if (data.namazDetail.reminderSound!!.isSilent) {
                    _binding.imageView.setImageResource(R.drawable.ic_mute_mike)
                }

                if (data.namazDetail.reminderSound!!.isOff) {
                    _binding.imageView.setImageResource(R.drawable.ic_off)
                }

                if (data.namazDetail.reminderSound!!.isVibrate) {
                    _binding.imageView.setImageResource(R.drawable.ic_vibrate)
                }

            } else {
                _binding.imageView.setImageResource(R.drawable.ic_notification_mute)
            }
        }
    }

}

fun interface OnTimeDataSave{
    fun onSave(data:NotificationData?,namazName:String,namazTime:String)
}
