package com.iw.android.prayerapp.ui.main.monthlyCalender

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentMonthlyCalendarBinding
import com.iw.android.prayerapp.ui.main.monthlyCalender.itemView.RowItemMonthlyCalender
import com.iw.android.prayerapp.utils.PaginationScrollListener

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
        binding.textViewTitle.text = getString(R.string.monthly)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= 35) {
            WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
            ViewCompat.setOnApplyWindowInsetsListener(binding.mainView) { v: View, insets: WindowInsetsCompat ->
                val systemBars: Insets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(0, 0, 0, systemBars.bottom)
                insets
            }
        }

        initialize()
        setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        setRecyclerView()
        setOnBackPressedListener()
    }

    override fun setObserver() {
        viewModel.pagedData.observe(viewLifecycleOwner) { pagedList ->
            val previousSize = viewTypeArray.size
            val newItems = pagedList.drop(previousSize)

            for (data in newItems) {
                viewTypeArray.add(RowItemMonthlyCalender(data))
            }

            adapter.items = viewTypeArray
            viewModel.isLoading.value = false
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progress.show()
            } else {
                binding.progress.hide()
            }
        }
    }


    override fun setOnClickListener() {
        binding.backView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.backView.id -> {
                findNavController().popBackStack()
            }
        }
    }

    private fun setRecyclerView() {

        binding.recyclerView.adapter = adapter
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager
        binding.recyclerView.addOnScrollListener(object :
            PaginationScrollListener(layoutManager) {
            override fun onScrolled(dy: Int) {
            }

            override fun loadMoreItems() {
                viewModel.loadNextPage()
            }
        })
    }

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            })
    }

}

