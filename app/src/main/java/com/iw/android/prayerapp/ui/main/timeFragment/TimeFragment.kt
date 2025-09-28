package com.iw.android.prayerapp.ui.main.timeFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.base.response.LocationResponse
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.databinding.FragmentTimeBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.OnTimeDataSave
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.RowItemTime
import com.iw.android.prayerapp.utils.dateFormat.formattedDateForTimeScreen
import com.iw.android.prayerapp.utils.map.MapDialog
import com.iw.android.prayerapp.utils.map.openGoogleMapsNearbyPlaces
import com.iw.android.prayerapp.utils.map.openLocationDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeFragment : BaseFragment(R.layout.fragment_time), View.OnClickListener,
    MapDialog.MapDialogListener, OnTimeDataSave {

    private var _binding: FragmentTimeBinding? = null
    val binding
        get() = _binding!!

    private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var dateOffset = 0
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var city = ""
    private var isDialogOpen = false

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimeBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setOnClickListener()
        setObserver()
    }

    override fun initialize() {

        setRecyclerView()
        setOnBackPressedListener()
        binding.textViewDateTitle.text = formattedDateForTimeScreen(dateOffset)
    }

    override fun setObserver() {

        viewModel.prayTimeArray.observe(viewLifecycleOwner) { pagedList ->
            if (pagedList.isNotEmpty()) {
                Log.d("pagData","${pagedList[0]}")
                viewTypeArray.clear()
                for (data in pagedList) {
                    viewTypeArray.add(
                        RowItemTime(data, binding.recyclerView, requireActivity(), this)
                    )
                }
                adapter.items = viewTypeArray
            }
        }

        viewModel.userLocation.observe(viewLifecycleOwner) { location ->
            currentLatitude = location?.latitude ?: 0.0
            currentLongitude = location?.longitude ?: 0.0
            viewModel.fetchTimeZoneAndCity(requireContext(),currentLatitude,currentLongitude)


        }

        viewModel.location.observe(viewLifecycleOwner) { location ->
            city = location?.city ?: "City"
            binding.textViewTitle.text = location?.city ?: "City"
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
        binding.islamicHolidayClickView.setOnClickListener(this)
        binding.masjidClickView.setOnClickListener(this)
        binding.imageViewBack.setOnClickListener(this)
        binding.viewBack.setOnClickListener(this)
        binding.viewForward.setOnClickListener(this)
        binding.imageViewForward.setOnClickListener(this)
        binding.textViewTitle.setOnClickListener(this)
        binding.monthlyClickView.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.textViewTitle.id -> {
                if (!isDialogOpen) {
                    isDialogOpen = true
                    Log.d("list",viewModel.recentLocationList.toString())
                    openLocationDialog(viewModel.recentLocationList, requireActivity(), this)

                } else {
                    isDialogOpen = false
                    viewModel.clearPrayerTimes()
                    lifecycleScope.launch(Dispatchers.IO) {
                        viewModel.getPrayList(
                            currentLatitude,
                            currentLongitude
                        )
                    }
                    binding.imageViewTitle.gone()
                    binding.textViewTitleJuri.gone()

                    viewModel.fetchTimeZoneAndCity(requireContext(),currentLatitude,currentLongitude)

                }


            }

            binding.viewForward.id, binding.imageViewForward.id -> {
                dateOffset++
                binding.textViewDateTitle.text = getFormattedDate(dateOffset)
            }

            binding.viewBack.id,  binding.imageViewBack.id -> {
                dateOffset--
                binding.textViewDateTitle.text = getFormattedDate(dateOffset)
            }

            binding.islamicHolidayClickView.id -> {
                findNavController().navigate(TimeFragmentDirections.actionTimeFragmentToIslamicHolidayFragment2())
            }

            binding.masjidClickView.id -> {
                openGoogleMapsNearbyPlaces(currentLatitude, currentLongitude, requireActivity())
            }

            binding.monthlyClickView.id -> {
                findNavController().navigate(TimeFragmentDirections.actionTimeFragmentToFragmentMonthlyCalender())
            }

        }

    }


    override fun onDataPassed(data: LocationResponse) {
        viewModel.clearPrayerTimes()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getPrayList(
                data.lat,
                data.long
            )
            viewModel.saveRecentLocationData(data)
            viewModel.getRecentLocationDataFromDB()
        }

        viewModel.fetchTimeZoneAndCity(requireContext(),data.lat,data.long)
        binding.imageViewTitle.show()
        val duaArray: Array<String> = resources.getStringArray(R.array.methods)
        val position =
            if (viewModel.selectedJurisprudenceFromDB.isNullOrEmpty()) 0 else viewModel.selectedJurisprudenceFromDB.toInt()
        binding.textViewTitleJuri.text = duaArray[position]
        binding.textViewTitleJuri.show()

    }

    private fun getFormattedDate(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val targetDate: Date = calendar.time
        viewModel.selectedPrayerDate = targetDate
        viewModel.clearPrayerTimes()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.getPrayList(
                currentLatitude,
                currentLongitude
            )
        }

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(targetDate)
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.stopScroll()
    }

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                }
            })
    }

    override fun onSave(data: NotificationData?, namazName: String, namazTime: String) {
        viewModel.savePrayerDetailData(data,namazName,namazTime)
    }
}