package com.iw.android.prayerapp.utils.datePicker

import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.widget.TimePicker
import androidx.core.content.ContextCompat
import com.iw.android.prayerapp.R
import java.util.Calendar

 fun openTimePicker(
    context: Context,
    onTimeSetListener: (hourOfDay: Int, minute: Int) -> Unit

) {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 12)
    calendar.set(Calendar.MINUTE, 0)
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