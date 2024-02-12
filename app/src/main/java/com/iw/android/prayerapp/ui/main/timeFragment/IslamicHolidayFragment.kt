package com.iw.android.prayerapp.ui.main.timeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.IslamicHolidayResponse
import com.iw.android.prayerapp.databinding.FragmentIslamicHolidayBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.RowItemIslamicHolidays
import com.iw.android.prayerapp.ui.activities.screens.MainActivity
import com.iw.android.prayerapp.utils.TinyDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class IslamicHolidayFragment : BaseFragment(R.layout.fragment_islamic_holiday), View.OnClickListener {

    private var _binding: FragmentIslamicHolidayBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    private var isItemClick = true
    private var islamicHolidayArray = arrayListOf<IslamicHolidayResponse>()

    //private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
                lifecycleScope.launch {
                    if (isItemClick) {
                        isItemClick = false
                        item.data()?.let { data ->

                        }
                    }
                    delay(1000)
                    isItemClick = true
                }
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIslamicHolidayBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)

        tinyDB = TinyDB(context)
        (requireActivity() as MainActivity).hideBottomSheet()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        setRecyclerView()
        addHolidayList()
    }

    override fun setObserver() {
        binding.textViewDate.text = getCurrentDateFormatted()
        binding.textViewIslamicDate.text = getCurrentIslamicDate()
        viewTypeArray.clear()
        for (data in islamicHolidayArray) {
            viewTypeArray.add(
                RowItemIslamicHolidays(data)
            )
        }
        adapter.items = viewTypeArray
    }

    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewBack.id -> {
                (requireActivity() as MainActivity).replaceFragment(TimeFragment())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter

    }

    private fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        return dateFormat.format(calendar.time)
    }

    private fun getCurrentIslamicDate(): String {
        val currentDate = Date()
        val islamicCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"))
        islamicCalendar.time = currentDate

        val day = islamicCalendar.get(Calendar.DAY_OF_MONTH)
        val month = islamicCalendar.get(Calendar.MONTH) + 1 // Months are 0-indexed in Calendar
        val year = islamicCalendar.get(Calendar.YEAR)

        return "$day ${getIslamicMonthName(month)} $year"
    }

    private fun getIslamicMonthName(month: Int): String {
        val islamicMonths = listOf(
            "Muharram", "Safar", "Rabi' al-Awwal", "Rabi' al-Thani",
            "Jumada al-Awwal", "Jumada al-Thani", "Rajab", "Sha'ban",
            "Ramadan", "Shawwal", "Dhu al-Qi'dah", "Dhu al-Hijjah"
        )

        return islamicMonths[month - 1]
    }

    private fun addHolidayList() {
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Isra' & Mi'raj",
                "27 Rajab 1445",
                "8 February 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Ramadan",
                "1 Ramadan 1445",
                "11 March 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Eid al-Fitr",
                "2 Shawwal 1445",
                "10 April 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Hajj",
                "8 Dhu'l-Hijjah 1445",
                "14 June 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Day of Arafah",
                "9 Dhu'l-Hijjah 1445",
                "15 June 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Eid al-Adha",
                "10 Dhu'l-Hijjah 1445",
                "16 June 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "New Year",
                "1 Muharram 1446",
                "7 July 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Ashura",
                "10 Muharram 1446",
                "16 July 2024"
            )
        )
    }
}