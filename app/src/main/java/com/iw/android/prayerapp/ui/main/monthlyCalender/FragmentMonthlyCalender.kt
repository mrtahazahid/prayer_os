package com.iw.android.prayerapp.ui.main.monthlyCalender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerTimes
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentMonthlyCalendarBinding
import com.iw.android.prayerapp.extension.converterForMonthly
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import com.iw.android.prayerapp.ui.main.monthlyCalender.itemView.RowItemMonthlyCalender
import com.iw.android.prayerapp.ui.main.timeFragment.TimeViewModel
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.Helper
import com.iw.android.prayerapp.utils.Helper.generateDatesAsDateObjects
import com.iw.android.prayerapp.utils.Helper.generateDayAndWeekInfo
import java.time.format.TextStyle
import java.util.Locale

class FragmentMonthlyCalender : BaseFragment(R.layout.fragment_monthly_calendar),
    View.OnClickListener {

    private lateinit var wholeYearFajarTimeArray: ArrayList<String>
    private lateinit var wholeYearZoharTimeArray: ArrayList<String>
    private lateinit var wholeYearAsarTimeArray: ArrayList<String>
    private lateinit var wholeYearMaghribTimeArray: ArrayList<String>
    private lateinit var wholeYearIshaTimeArray: ArrayList<String>
    private lateinit var wholeYearSunriseTimeArray: ArrayList<String>
    private lateinit var wholeYearMonthArray: ArrayList<String>
    private lateinit var wholeYearHijriArray: ArrayList<String>
    private lateinit var wholeYearDateArray: ArrayList<String>
    private lateinit var wholeYearDayArray: ArrayList<String>
    private lateinit var wholeYearHijriNameArray: ArrayList<String>


    private lateinit var getPrayerTime: PrayerTimes
    private var _binding: FragmentMonthlyCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MonthlyViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    val onBoardingViewModel: OnBoardingViewModel by viewModels()
    private val timeViewModel: TimeViewModel by viewModels()

    val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthlyCalendarBinding.inflate(inflater, container, false)
        wholeYearFajarTimeArray = ArrayList()
        wholeYearZoharTimeArray = ArrayList()
        wholeYearAsarTimeArray = ArrayList()
        wholeYearMaghribTimeArray = ArrayList()
        wholeYearIshaTimeArray = ArrayList()
        wholeYearSunriseTimeArray = ArrayList()
        wholeYearMonthArray = ArrayList()
        wholeYearHijriArray = ArrayList()
        wholeYearDateArray = ArrayList()
        wholeYearDayArray = ArrayList()
        wholeYearHijriNameArray = ArrayList()

        getWholeYearFajarTime()
        getWholeYearZoharTime()
        getWholeYearAsarTime()
        getWholeYearMaghribTime()
        getWholeYearIshaTime()
        getWholeYearSunriseTime()

        wholeYearHijriArray.add("0")

        val allDateInfo = generateDayAndWeekInfo()
        allDateInfo.forEach {
            wholeYearMonthArray.add(it.previousMonth.getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
            wholeYearDateArray.add(it.dayOfMonth.toString())
            wholeYearDayArray.add(it.dayOfWeek)
            wholeYearHijriNameArray.add(it.hijriMonth)
            wholeYearHijriArray.add(it.hijriDayOfMonth.toString())
        }
        return binding.root
    }

    private fun getWholeYearFajarTime(){
        for (i in generateDatesAsDateObjects()){
            getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                timeViewModel.userLatLong?.latitude ?: 0.0,
                timeViewModel.userLatLong?.longitude ?: 0.0,
                onBoardingViewModel.method
                    ?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
                i
            )
            wholeYearFajarTimeArray.add(converterForMonthly(getPrayerTime.fajr.toEpochMilliseconds()))
        }
    }

    private fun getWholeYearZoharTime(){
        for (i in generateDatesAsDateObjects()){
            getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                timeViewModel.userLatLong?.latitude ?: 0.0,
                timeViewModel.userLatLong?.longitude ?: 0.0,
                onBoardingViewModel.method
                    ?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
                i
            )
            wholeYearZoharTimeArray.add(converterForMonthly(getPrayerTime.dhuhr.toEpochMilliseconds()))
        }
    }

    private fun getWholeYearAsarTime(){
        for (i in generateDatesAsDateObjects()){
            getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                timeViewModel.userLatLong?.latitude ?: 0.0,
                timeViewModel.userLatLong?.longitude ?: 0.0,
                onBoardingViewModel.method
                    ?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
                i
            )
            wholeYearAsarTimeArray.add(converterForMonthly(getPrayerTime.asr.toEpochMilliseconds()))
        }
    }

    private fun getWholeYearMaghribTime(){
        for (i in generateDatesAsDateObjects()){
            getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                timeViewModel.userLatLong?.latitude ?: 0.0,
                timeViewModel.userLatLong?.longitude ?: 0.0,
                onBoardingViewModel.method
                    ?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
                i
            )
            wholeYearMaghribTimeArray.add(converterForMonthly(getPrayerTime.maghrib.toEpochMilliseconds()))
        }
    }

    private fun getWholeYearIshaTime(){
        for (i in generateDatesAsDateObjects()){
            getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                timeViewModel.userLatLong?.latitude ?: 0.0,
                timeViewModel.userLatLong?.longitude ?: 0.0,
                onBoardingViewModel.method
                    ?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
                i
            )
            wholeYearIshaTimeArray.add(converterForMonthly(getPrayerTime.isha.toEpochMilliseconds()))
        }
    }

    private fun getWholeYearSunriseTime(){
        for (i in generateDatesAsDateObjects()){
            getPrayerTime = GetAdhanDetails.getPrayTimeInLong(
                timeViewModel.userLatLong?.latitude ?: 0.0,
                timeViewModel.userLatLong?.longitude ?: 0.0,
                onBoardingViewModel.method
                    ?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
                i
            )
            wholeYearSunriseTimeArray.add(converterForMonthly(getPrayerTime.sunrise.toEpochMilliseconds()))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        setRecyclerView()
    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.monthlyList) {
            viewTypeArray.add(
                RowItemMonthlyCalender(data)
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
                findNavController().popBackStack()
            }
        }
    }

    private fun setRecyclerView() {
        val monthlyAdapter = MonthlyCalenderAdapter(
            wholeYearMonthArray,
            wholeYearDayArray,
            wholeYearDateArray,
            wholeYearSunriseTimeArray,
            wholeYearFajarTimeArray,
            wholeYearZoharTimeArray,
            wholeYearAsarTimeArray,
            wholeYearMaghribTimeArray,
            wholeYearIshaTimeArray,
            wholeYearHijriArray,
            wholeYearHijriNameArray
        )
        binding.recyclerView.adapter = monthlyAdapter
    }

}
