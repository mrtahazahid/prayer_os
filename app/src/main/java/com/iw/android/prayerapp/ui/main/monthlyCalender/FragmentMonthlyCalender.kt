package com.iw.android.prayerapp.ui.main.monthlyCalender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentMonthlyCalendarBinding
import com.iw.android.prayerapp.ui.main.monthlyCalender.itemView.RowItemMonthlyCalender

class FragmentMonthlyCalender : BaseFragment(R.layout.fragment_monthly_calendar),
    View.OnClickListener {


    private var _binding: FragmentMonthlyCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MonthlyViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

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
        binding.recyclerView.adapter = adapter
    }

}
