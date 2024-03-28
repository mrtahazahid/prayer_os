package com.iw.android.prayerapp.ui.main.notificationList.itemView

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
            binding.textViewTime.text = data.namazTime
        }
    }
}