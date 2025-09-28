package com.iw.android.prayerapp.ui.main.monthlyCalender

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.data.response.MonthlyPrayerDay

class MonthlyCalenderAdapter(
    private val data: MutableList<MonthlyPrayerDay>,
) : RecyclerView.Adapter<MonthlyCalenderAdapter.MonthlyCalenderViewHolder>() {

    class MonthlyCalenderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val month: AppCompatTextView = itemView.findViewById(R.id.textViewMonth)
        val date: AppCompatTextView = itemView.findViewById(R.id.textViewDate)
        val day: AppCompatTextView = itemView.findViewById(R.id.textViewDay)
        val fajar: AppCompatTextView = itemView.findViewById(R.id.textViewFajr)
        val zohar: AppCompatTextView = itemView.findViewById(R.id.textViewDHR)
        val asar: AppCompatTextView = itemView.findViewById(R.id.textViewASR)
        val maghrib: AppCompatTextView = itemView.findViewById(R.id.textViewMGB)
        val isha: AppCompatTextView = itemView.findViewById(R.id.textViewISH)
        val hijri: AppCompatTextView = itemView.findViewById(R.id.textViewHijri)
        val hijriName: AppCompatTextView = itemView.findViewById(R.id.textViewHijriName)
        val sunrise: AppCompatTextView = itemView.findViewById(R.id.textViewSHK)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthlyCalenderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_item_monthly_calender, parent, false)
        return MonthlyCalenderViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthlyCalenderViewHolder, position: Int) {
        val item = data[position]

        // Set basic texts
        holder.fajar.text = item.fajar
        holder.zohar.text = item.zohar
        holder.asar.text = item.asar
        holder.maghrib.text = item.maghrib
        holder.isha.text = item.isha
        holder.date.text = item.date
        holder.day.text = item.day
        holder.month.text = item.month
        holder.sunrise.text = item.sunrise
        holder.hijri.text = item.hijri
        holder.hijriName.text = item.hijriName

        val isFirstItem = position == 0
        val isHijriIsOne = item.hijri == "1"
        val isMonthIsOne = item.date == "1"
        val isDayFridayOrRamadan = item.day.equals("Fri", ignoreCase = true) || item.hijriName == "Ram."

        // Typeface
        val typeface = if (isFirstItem) Typeface.BOLD else Typeface.NORMAL
        listOf(
            holder.fajar, holder.zohar, holder.asar, holder.maghrib, holder.isha,
            holder.date, holder.day, holder.month, holder.sunrise, holder.hijri, holder.hijriName
        ).forEach { it.setTypeface(null, typeface) }

        // Colors
        val context = holder.itemView.context
        val white = ContextCompat.getColor(context, R.color.white)
        val grey = ContextCompat.getColor(context, R.color.app_grey)
        val yellow = ContextCompat.getColor(context, R.color.yellow_text)
        val green = ContextCompat.getColor(context, R.color.app_green)

        when {
            isDayFridayOrRamadan -> {
                // Green text for Friday or Ramadan
                listOf(
                    holder.fajar, holder.zohar, holder.asar, holder.maghrib, holder.isha,
                    holder.date, holder.day, holder.month, holder.sunrise,
                    holder.hijri, holder.hijriName
                ).forEach { it.setTextColor(green) }
            }

            isHijriIsOne -> {
                // Yellow for hijri date = 1
                holder.hijri.setTextColor(yellow)
                holder.hijriName.setTextColor(yellow)

                // Reset others
                listOf(
                    holder.fajar, holder.zohar, holder.asar, holder.maghrib, holder.isha, holder.sunrise
                ).forEach { it.setTextColor(grey) }

                listOf(holder.date, holder.day, holder.month).forEach { it.setTextColor(white) }
            }

            isMonthIsOne -> {
                // Yellow for month date = 1
                holder.month.setTextColor(yellow)
                holder.date.setTextColor(yellow)

                // Reset others
                listOf(
                    holder.fajar, holder.zohar, holder.asar, holder.maghrib, holder.isha, holder.sunrise
                ).forEach { it.setTextColor(grey) }

                listOf(holder.day, holder.hijri, holder.hijriName).forEach { it.setTextColor(white) }
            }

            else -> {
                // Default coloring
                listOf(holder.fajar, holder.zohar, holder.asar, holder.maghrib, holder.isha, holder.sunrise)
                    .forEach { it.setTextColor(grey) }

                listOf(holder.date, holder.day, holder.month, holder.hijri, holder.hijriName)
                    .forEach { it.setTextColor(white) }
            }
        }
    }


    override fun getItemCount() = data.size

}