package com.iw.android.prayerapp.ui.main.monthlyCalender.itemView

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MonthlyPrayerDay
import com.iw.android.prayerapp.databinding.RowItemMonthlyCalenderBinding

class RowItemMonthlyCalender(private val data: MonthlyPrayerDay) :
    ViewType<MonthlyPrayerDay> {
    override fun layoutId(): Int {
        return R.layout.row_item_monthly_calender
    }

    override fun data(): MonthlyPrayerDay {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemMonthlyCalenderBinding).also { binding ->

            binding.textViewFajr.text = data.fajar
            binding.textViewDHR.text = data.zohar
            binding.textViewASR.text = data.asar
            binding.textViewMGB.text = data.maghrib
            binding.textViewISH.text = data.isha
            binding.textViewDate.text = data.date
            binding.textViewDay.text = data.day
            binding.textViewMonth.text = data.month
            binding.textViewSHK.text = data.sunrise
            binding.textViewHijri.text = data.hijri
            binding.textViewHijriName.text = data.hijriName

            val isFirstItem = position == 0
            val isHijriIsOne = data.hijri == "1"
            val isMonthIsOne = data.date == "1"
            val isDayFridayOrRamadan = data.day.equals("Fri", ignoreCase = true) || data.hijriName == "Ram."

            // Typeface
            val typeface = if (isFirstItem) Typeface.BOLD else Typeface.NORMAL
            listOf(
                binding.textViewFajr, binding.textViewDHR, binding.textViewASR, binding.textViewMGB, binding.textViewISH,
                binding.textViewDate, binding.textViewDay, binding.textViewMonth, binding.textViewSHK, binding.textViewHijri, binding.textViewHijriName
            ).forEach { it.setTypeface(null, typeface) }

            // Colors
            val context = binding.textViewASR.context
            val white = ContextCompat.getColor(context, R.color.white)
            val grey = ContextCompat.getColor(context, R.color.app_grey)
            val yellow = ContextCompat.getColor(context, R.color.yellow_text)
            val green = ContextCompat.getColor(context, R.color.app_green)

            when {
                isDayFridayOrRamadan -> {
                    // Green text for Friday or Ramadan
                    listOf(
                        binding.textViewFajr, binding.textViewDHR, binding.textViewASR, binding.textViewMGB, binding.textViewISH,
                        binding.textViewDate, binding.textViewDay, binding.textViewMonth, binding.textViewSHK,
                        binding.textViewHijri, binding.textViewHijriName
                    ).forEach { it.setTextColor(green) }
                }

                isHijriIsOne -> {
                    // Yellow for hijri date = 1
                    binding.textViewHijri.setTextColor(yellow)
                    binding.textViewHijriName.setTextColor(yellow)

                    // Reset others
                    listOf(
                        binding.textViewFajr, binding.textViewDHR, binding.textViewASR, binding.textViewMGB, binding.textViewISH, binding.textViewSHK
                    ).forEach { it.setTextColor(grey) }

                    listOf(binding.textViewDate, binding.textViewDay, binding.textViewMonth).forEach { it.setTextColor(white) }
                }

                isMonthIsOne -> {
                    // Yellow for month date = 1
                    binding.textViewMonth.setTextColor(yellow)
                    binding.textViewDate.setTextColor(yellow)

                    // Reset others
                    listOf(
                        binding.textViewFajr, binding.textViewDHR, binding.textViewASR, binding.textViewMGB, binding.textViewISH, binding.textViewSHK
                    ).forEach { it.setTextColor(grey) }

                    listOf(binding.textViewDay, binding.textViewHijri, binding.textViewHijriName).forEach { it.setTextColor(white) }
                }

                else -> {
                    // Default coloring
                    listOf(binding.textViewFajr, binding.textViewDHR, binding.textViewASR, binding.textViewMGB, binding.textViewISH, binding.textViewSHK)
                        .forEach { it.setTextColor(grey) }

                    listOf(binding.textViewDate, binding.textViewDay, binding.textViewMonth, binding.textViewHijri, binding.textViewHijriName)
                        .forEach { it.setTextColor(white) }
                }
            }
        }
    }

}