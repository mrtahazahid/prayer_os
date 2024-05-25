package com.iw.android.prayerapp.ui.main.iqamaFragment.itemView

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.data.response.IqamaTime
import com.iw.android.prayerapp.databinding.RowItemIqamaBinding
import com.iw.android.prayerapp.ui.main.iqamaFragment.IqamaViewModel
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RowItemIqama(
    private val data: IqamaData,
    val recyclerView: RecyclerView,
    val fragment: Fragment,
    val viewModel: IqamaViewModel
) : ViewType<IqamaData> {
    private var iqamaReminderTime = 0
    override fun layoutId(): Int {
        return R.layout.row_item_iqama
    }

    override fun data(): IqamaData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemIqamaBinding).also { binding ->
            binding.view4.visibility = if (data.namazName == "Isha") View.GONE else View.VISIBLE

            when (data.iqamaType) {
                DuaTypeEnum.OFF.getValue() -> {
                    binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                    binding.cardViewIqamaTime.visibility = View.GONE
                    binding.textViewIqamaSetTime.visibility = View.GONE
                }

                DuaTypeEnum.TIME.getValue() -> {
                    binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                    binding.cardViewIqamaTime.visibility = View.VISIBLE
                    binding.textViewIqamaSetTime.visibility = View.GONE
                }

                DuaTypeEnum.MINUTES.getValue() -> {
                    binding.cardViewIqamaAdjustmentTime.visibility = View.VISIBLE
                    binding.cardViewIqamaTime.visibility = View.GONE
                    binding.textViewIqamaSetTime.visibility = View.VISIBLE
                }
            }


            binding.textViewIqamaReminder.text = data.namazName
            binding.textViewIqamaTime.text = data.iqamaTime?.iqamaTime ?: "12:00 AM"
            binding.textViewIqamaSetTime.text = data.iqamaTime?.iqamaMinutes ?: "off"
            binding.imageViewIqamaAdd.setOnClickListener {
                binding.textViewIqamaSetTime.text = incrementDuaMinute()
                data.iqamaTime = IqamaTime(
                    "12:00 AM",
                    binding.textViewIqamaSetTime.text.toString(),
                    addMinutesToTime(data.namazTime, iqamaReminderTime)
                )

                savePrayerDetailData()
            }
            binding.imageViewIqamaMinus.setOnClickListener {
                binding.textViewIqamaSetTime.text = decrementDuaMinute()
                data.iqamaTime = IqamaTime("12:00 AM", binding.textViewIqamaSetTime.text.toString(),
                    addMinutesToTime(data.namazTime, iqamaReminderTime))
                savePrayerDetailData()
            }

            binding.cardViewIqamaTime.setOnClickListener {
                openTimePicker(binding.cardViewIqamaTime.context, 12, 0) { hourOfDay, minute ->
                    // Handle the selected time (hourOfDay and minute)
                    val formattedTime =
                        SimpleDateFormat("h:mm a").format(Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                        }.time)
                    binding.textViewIqamaTime.text = formattedTime
                    data.iqamaTime?.iqamaTime = formattedTime
                    data.iqamaTime = IqamaTime(formattedTime, "off")
                    savePrayerDetailData()
                }
            }
            spinnerDua(binding)
        }
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

    private fun spinnerDua(binding: RowItemIqamaBinding) {

        val adapter = ArrayAdapter.createFromResource(
            binding.textViewIqamaReminder.context,
            R.array.dua,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerIqamaReminderSwitch.adapter = adapter


        when (data.iqamaType) {
            DuaTypeEnum.OFF.getValue() -> {
                binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                binding.cardViewIqamaTime.visibility = View.GONE
                binding.textViewIqamaSetTime.visibility = View.GONE
                binding.spinnerIqamaReminderSwitch.setSelection(0)
            }

            DuaTypeEnum.TIME.getValue() -> {
                binding.cardViewIqamaAdjustmentTime.visibility = View.VISIBLE
                binding.cardViewIqamaTime.visibility = View.GONE
                binding.textViewIqamaSetTime.visibility = View.VISIBLE
                binding.textViewIqamaTime.text = data.iqamaTime?.iqamaTime
                binding.spinnerIqamaReminderSwitch.setSelection(1)

            }

            DuaTypeEnum.MINUTES.getValue() -> {
                binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                binding.cardViewIqamaTime.visibility = View.VISIBLE
                binding.textViewIqamaSetTime.visibility = View.GONE
                binding.textViewIqamaSetTime.text = "${data.iqamaTime?.iqamaMinutes} mins"
                binding.spinnerIqamaReminderSwitch.setSelection(2)

            }


        }

        binding.spinnerIqamaReminderSwitch.onItemSelectedListener = object :
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
                        binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                        binding.cardViewIqamaTime.visibility = View.GONE
                        binding.textViewIqamaSetTime.visibility = View.GONE
                    }

                    1 -> {
                        binding.cardViewIqamaAdjustmentTime.visibility = View.GONE
                        binding.cardViewIqamaTime.visibility = View.VISIBLE
                        binding.textViewIqamaSetTime.visibility = View.GONE
                    }

                    2 -> {
                        binding.cardViewIqamaAdjustmentTime.visibility = View.VISIBLE
                        binding.cardViewIqamaTime.visibility = View.GONE
                        binding.textViewIqamaSetTime.visibility = View.VISIBLE
                    }
                }
                data.iqamaType = selectedItem
                savePrayerDetailData()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun incrementDuaMinute(): String {
        iqamaReminderTime++
        return "$iqamaReminderTime min"
    }

    private fun decrementDuaMinute(): String {

        return if (iqamaReminderTime > 0) {
            iqamaReminderTime--
            "$iqamaReminderTime min"
        } else {
            "off"
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


    private fun savePrayerDetailData() = fragment.lifecycleScope.launch {
        when (data.namazName) {
            "Fajr" -> {
                viewModel.saveIqamaFajrDetail(
                    IqamaData(
                        namazName = data.namazName,
                        iqamaType = data.iqamaType,
                        iqamaTime = data.iqamaTime,
                        namazTime = data.namazTime

                    )
                )
            }


            "Dhuhr" -> {
                viewModel.saveIqamaDhuhrDetail(
                    IqamaData(
                        namazName = data.namazName,
                        iqamaType = data.iqamaType,
                        iqamaTime = data.iqamaTime,
                        namazTime = data.namazTime

                    )
                )

            }

            "Asr" -> {
                viewModel.saveIqamaAsrDetail(
                    IqamaData(
                        namazName = data.namazName,
                        iqamaType = data.iqamaType,
                        iqamaTime = data.iqamaTime,
                        namazTime = data.namazTime

                    )
                )

            }

            "Maghrib" -> {
                viewModel.saveIqamaMaghribDetail(
                    IqamaData(
                        namazName = data.namazName,
                        iqamaType = data.iqamaType,
                        iqamaTime = data.iqamaTime,
                        namazTime = data.namazTime

                    )
                )

            }

            "Isha" -> {
                viewModel.saveIqamaIshaDetail(
                    IqamaData(
                        namazName = data.namazName,
                        iqamaType = data.iqamaType,
                        iqamaTime = data.iqamaTime,
                        namazTime = data.namazTime

                    )
                )

            }

        }
    }

}