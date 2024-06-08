package com.iw.android.prayerapp.ui.main.timeFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.IslamicHolidayResponse
import com.iw.android.prayerapp.databinding.FragmentIslamicHolidayBinding
import com.iw.android.prayerapp.extension.getCurrentDateFormatted
import com.iw.android.prayerapp.extension.getIslamicDate
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.RowItemIslamicHolidays


class IslamicHolidayFragment : BaseFragment(R.layout.fragment_islamic_holiday), View.OnClickListener {

    private var _binding: FragmentIslamicHolidayBinding? = null
    private val binding get() = _binding!!


    private var islamicHolidayArray = arrayListOf<IslamicHolidayResponse>()

    //private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIslamicHolidayBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)

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
        binding.textViewIslamicDate.text = getIslamicDate()
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
        binding.textViewDateTitle.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.textViewDateTitle.id,  binding.imageViewBack.id -> {
                findNavController().popBackStack()
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




    private fun addHolidayList() {
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Hajj",
                "8 Dhu'l-Hijjah 1445",
                "14 june 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Day of Arafah",
                "9 Dhu'l-Hijjah 1445",
                "15 june 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Eid al-Adha",
                "10 Dhu'l-Hijjah 1445",
                "16 june 2024"
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
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Isra' & Mi'raj",
                "27 Rajab 1446",
                "27 Jaanuary 2025"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Ramadan",
                "1 Ramadan 1446",
                "1 March 2025"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Eid al-Fitr",
                "1 Shawwal 1446",
                "30 March 2024"
            )
        )

    }
}