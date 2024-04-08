package com.iw.android.prayerapp.ui.main.notificationList.itemView

import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.databinding.RowItemNotificationListBinding

class RowItemNotificationList(private val data: NotificationData) : ViewType<NotificationData> {
    override fun layoutId(): Int {
        return R.layout.row_item_notification_list
    }

    override fun data(): NotificationData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemNotificationListBinding).also { binding ->
            if (data.isForNotificationList) {
                binding.textViewTime.text = "${data.createdDate} at ${data.namazTime}"
                binding.textViewTitle.text = data.namazName
                binding.secondView.visibility = View.GONE
            } else {
                if (data.reminderTime == "") {
                    binding.secondView.visibility = View.GONE
                    binding.textViewReminderTime.text = "${data.createdDate} at ${data.namazTime}"
                } else {
                    binding.secondView.visibility = View.VISIBLE
                    binding.textViewReminderTitle.text = if (data.reminderTimeMinutes != "off") {
                        "${data.namazName} in ${data.reminderTimeMinutes}"
                    } else {
                        data.namazName
                    }

                    binding.textViewReminderTime.text =
                        "${data.createdDate} at ${data.reminderTime}"
                }
                binding.textViewTime.text = "${data.createdDate} at ${data.namazTime}"
                binding.textViewTitle.text = "${data.namazName} at ${data.namazTime}"

            }


        }
    }
}