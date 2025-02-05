package com.iw.android.prayerapp.ui.main.monthlyCalender

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R

class MonthlyCalenderAdapter(
    private val wholeYearMonthArray: ArrayList<String>,
    private val wholeYearDayArray: ArrayList<String>,
    private val wholeYearDateArray: ArrayList<String>,
    private val wholeYearSunriseTimeArray: ArrayList<String>,
    private val wholeYearFajarTimeArray: ArrayList<String>,
    private val wholeYearZoharTimeArray: ArrayList<String>,
    private val wholeYearAsarTimeArray: ArrayList<String>,
    private val wholeYearMaghribTimeArray: ArrayList<String>,
    private val wholeYearIshaTimeArray: ArrayList<String>,
    private val wholeYearHijriArray: ArrayList<String>,
    private val wholeYearHijriNameArray: ArrayList<String>
) : RecyclerView.Adapter<MonthlyCalenderAdapter.MonthlyCalenderViewHolder>() {

    class MonthlyCalenderViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
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
        val border: View = itemView.findViewById(R.id.view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthlyCalenderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item_monthly_calender, parent, false)
        return MonthlyCalenderViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MonthlyCalenderViewHolder,
        position: Int
    ) {

        val monthData = wholeYearMonthArray[position]
        val dayData = wholeYearDayArray[position]
        val dateData = wholeYearDateArray[position]
        val sunriseData = wholeYearSunriseTimeArray[position]
        val fajarData = wholeYearFajarTimeArray[position]
        val zoharData = wholeYearZoharTimeArray[position]
        val asarData = wholeYearAsarTimeArray[position]
        val maghribData = wholeYearMaghribTimeArray[position]
        val ishaData = wholeYearIshaTimeArray[position]
        val hijri = wholeYearHijriArray[position]
        val hijriName = wholeYearHijriNameArray[position]
        holder.fajar.text = fajarData
        holder.zohar.text = zoharData
        holder.asar.text = asarData
        holder.maghrib.text = maghribData
        holder.isha.text = ishaData
        holder.date.text = dateData
        holder.day.text = dayData
        holder.month.text = monthData
        holder.sunrise.text = sunriseData
        holder.hijri.text =  if(hijri=="0") subtractOneFromString(wholeYearHijriArray[position+1]) else hijri
        holder.hijriName.text = hijriName
    }

    fun subtractOneFromString(numberString: String): String {
        // Convert the string to an integer, subtract 1, then convert back to a string
        return (numberString.toInt() - 1).toString()
    }
    override fun getItemCount(): Int {
       return wholeYearFajarTimeArray.size
    }
}