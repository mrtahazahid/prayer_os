package com.iw.android.prayerapp.ui.main.notificationList.itemView

import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.databinding.RowItemNotificationListBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RowItemNotificationList(private val data: NotificationData) : ViewType<NotificationData> {
    override fun layoutId(): Int {
        return R.layout.row_item_notification_list
    }

    override fun data(): NotificationData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemNotificationListBinding).also { binding ->
            Log.d("data",data.toString())
                if (data.reminderTime == "") {
                    binding.textViewTime.text = "${formatDate(data.createdDate)} at ${data.namazTime}"
                    binding.textViewTitle.text = "${data.namazName} at ${data.namazTime}"
                    binding.secondView.visibility = View.GONE
                } else {
                    binding.secondView.visibility = View.VISIBLE
                    binding.textViewReminderTitle.text = if (data.reminderTimeMinutes != "off") {
                        "${data.namazName} in ${data.reminderTimeMinutes}"
                    } else {
                        "${data.namazName} at ${data.reminderTime}"
                    }

                    binding.textViewReminderTime.text =
                        "${formatDate(data.createdDate)} at ${data.reminderTime}"
                }
                binding.textViewTime.text = "${formatDate(data.createdDate)} at ${data.namazTime}"
                binding.textViewTitle.text = "${data.namazName} at ${data.namazTime}"
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormats = listOf(
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()),   // e.g. 23 Jun 2025
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())   // e.g. 23 June 2025
        )

        val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault()) // e.g. 23 June

        // Use current date if input is blank
        if (inputDate.isBlank()) {
            return outputFormat.format(Date())
        }

        for (format in inputFormats) {
            try {
                val date = format.parse(inputDate)
                if (date != null) {
                    return outputFormat.format(date)
                }
            } catch (e: ParseException) {
                // Try next format
            }
        }

        // If parsing fails, fallback to today's date
        return outputFormat.format(Date())
    }

}