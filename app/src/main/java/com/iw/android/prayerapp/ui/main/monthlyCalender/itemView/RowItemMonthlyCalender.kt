package com.iw.android.prayerapp.ui.main.monthlyCalender.itemView

import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MonthlyCalenderData
import com.iw.android.prayerapp.databinding.RowItemMonthlyCalenderBinding

class RowItemMonthlyCalender(private val data: MonthlyCalenderData) :
    ViewType<MonthlyCalenderData> {
    override fun layoutId(): Int {
        return R.layout.row_item_monthly_calender
    }

    override fun data(): MonthlyCalenderData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemMonthlyCalenderBinding).also { binding ->
            binding.textViewDate.text = data.date
            binding.textViewDay.text = data.day
            binding.textViewFajr.text = data.fjr
            binding.textViewSHK.text = data.shk
            binding.textViewDHR.text = data.dhr
            binding.textViewASR.text = data.asr
            binding.textViewMGB.text = data.mgb
            binding.textViewISH.text = data.ish
            binding.textViewHijri.text = data.hijri
        }
    }
}