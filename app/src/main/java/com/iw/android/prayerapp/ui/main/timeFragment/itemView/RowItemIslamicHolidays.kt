package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.IslamicHolidayResponse
import com.iw.android.prayerapp.databinding.RowItemIslamicHolidaysBinding

class RowItemIslamicHolidays(
    private val data: IslamicHolidayResponse

) : ViewType<IslamicHolidayResponse> {


    override fun layoutId(): Int {
        return R.layout.row_item_islamic_holidays
    }

    override fun data(): IslamicHolidayResponse {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemIslamicHolidaysBinding).also { binding ->
            binding.view4.visibility = if (data.title == "Eid al-Fitr") View.GONE else View.VISIBLE
            val color = if (data.title == "New Year") ContextCompat.getColorStateList(
                binding.textViewTitle.context,
                R.color.orange
            ) else ContextCompat.getColorStateList(binding.textViewTitle.context, R.color.white)
            binding.textViewTitle.setTextColor(color)
            binding.textViewTitle.text = data.title
            binding.textViewIslamicDate.text = data.islamicDayTitle
            binding.textViewDate.text = data.dayTitle

        }
    }
}
