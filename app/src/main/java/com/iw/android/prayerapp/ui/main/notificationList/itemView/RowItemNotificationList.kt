package com.iw.android.prayerapp.ui.main.notificationList.itemView

import android.util.Log
import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.databinding.RowItemNotificationListBinding
import java.text.SimpleDateFormat
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
            Log.d("caad",formatDate(data.createdDate))
            if (data.isForNotificationList) {
                binding.textViewTime.text = "${formatDate(data.createdDate)} at ${data.namazTime}"
                binding.textViewTitle.text = "${data.namazName} at ${data.namazTime}"
                binding.secondView.visibility = View.GONE


            } else {
                if (data.reminderTime == "") {
                    binding.secondView.visibility = View.GONE

                    binding.textViewReminderTime.text = "${formatDate(data.createdDate)} at ${data.namazTime}"
                } else {
                    binding.secondView.visibility = View.VISIBLE
                    binding.textViewReminderTitle.text = if (data.reminderTimeMinutes != "off") {
                        "${data.namazName} in ${data.reminderTimeMinutes}"
                    } else {
                        data.namazName
                    }

                    binding.textViewReminderTime.text =
                        "${formatDate(data.createdDate)} at ${data.reminderTime}"
                }
                binding.textViewTime.text = "${formatDate(data.createdDate)} at ${data.namazTime}"
                binding.textViewTitle.text = "${data.namazName} at ${data.namazTime}"

            }


        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}