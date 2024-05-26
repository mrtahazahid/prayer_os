package com.iw.android.prayerapp.ui.main.timeFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.base.response.LocationResponse
import com.iw.android.prayerapp.databinding.FragmentTimeBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.RowItemTime
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.MapDialog
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeFragment : BaseFragment(R.layout.fragment_time), View.OnClickListener,
    MapDialog.MapDialogListener {

    private var _binding: FragmentTimeBinding? = null
    val binding
        get() = _binding!!

    private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var dateOffset = 0

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var isDialogOpen = false


    val adapter by lazy {
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
        lifecycleScope.launch {
            viewModel.getPrayList(
                viewModel.userLatLong?.latitude ?: 0.0,
                viewModel.userLatLong?.longitude ?: 0.0
            )
        }

        currentLatitude = viewModel.userLatLong?.latitude ?: 0.0
        currentLongitude = viewModel.userLatLong?.longitude ?: 0.0

        val location = GetAdhanDetails.getTimeZoneAndCity(
            requireContext(), currentLatitude,
            currentLongitude
        )
        binding.textViewTitle.text = location?.city ?: "City"

        binding.textViewDateTitle.text = getFormattedDate(dateOffset)


    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.prayTimeArray) {
            viewTypeArray.add(
                RowItemTime(data, binding.recyclerView, requireActivity(), viewModel)
            )
        }
        adapter.items = viewTypeArray
    }


    override fun setOnClickListener() {
        binding.islamicHolidayClickView.setOnClickListener(this)
        binding.masjidClickView.setOnClickListener(this)
        binding.imageViewBack.setOnClickListener(this)
        binding.imageViewForward.setOnClickListener(this)
        binding.textViewTitle.setOnClickListener(this)
        binding.monthlyClickView.setOnClickListener(this)

    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.stopScroll()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.textViewTitle.id -> {
                if (!isDialogOpen) {
                    isDialogOpen = true
                    openLocationDialog()

                } else {
                    isDialogOpen = false
                    viewModel.prayTimeArray.clear()
                    lifecycleScope.launch {
                        viewModel.getPrayList(
                            currentLatitude,
                            currentLongitude
                        )
                    }
                    binding.imageViewTitle.gone()
                    binding.textViewTitleJuri.gone()
                    val location = GetAdhanDetails.getTimeZoneAndCity(
                        requireContext(), currentLatitude,
                        currentLongitude
                    )

                    binding.textViewTitle.text = location?.city ?: "City"
                    setObserver()
                }


            }

            binding.imageViewForward.id -> {
                dateOffset++
                binding.textViewDateTitle.text = getFormattedDate(dateOffset)
            }

            binding.imageViewBack.id -> {
                dateOffset--
                binding.textViewDateTitle.text = getFormattedDate(dateOffset)
            }

            binding.islamicHolidayClickView.id -> {
                findNavController().navigate(TimeFragmentDirections.actionTimeFragmentToIslamicHolidayFragment2())
            }

            binding.masjidClickView.id -> {
                openGoogleMapsNearbyPlaces(currentLatitude, currentLongitude)
            }

            binding.monthlyClickView.id -> {
                Toast.makeText(
                    binding.monthlyClickView.context,
                    "Work in process",
                    Toast.LENGTH_SHORT
                ).show()
//                findNavController().navigate(TimeFragmentDirections.actionTimeFragmentToFragmentMonthlyCalender())
            }

        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGoogleMapsNearbyPlaces(latitude: Double, longitude: Double) {
        val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=mosque")

        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        }
    }


    fun getFormattedDate(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)
        val targetDate: Date = calendar.time
        viewModel.selectedPrayerDate = targetDate
        viewModel.prayTimeArray.clear()
        lifecycleScope.launch {
            viewModel.getPrayList(
                viewModel.userLatLong?.latitude ?: 0.0,
                viewModel.userLatLong?.longitude ?: 0.0
            )
        }


        setObserver()

        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(targetDate)
    }

    private fun openLocationDialog() {
        val locationDialog = MapDialog()
        locationDialog.listener = this
        lifecycleScope.launch {
            locationDialog.recentLocationList = viewModel.getRecentLocationData()
            Log.d("lsit", viewModel.getRecentLocationData().toString())
        }
        locationDialog.show(requireActivity().supportFragmentManager, "SoundDialogFragment")
    }

    override fun onDataPassed(data: LocationResponse) {
        viewModel.prayTimeArray.clear()
        lifecycleScope.launch {
            viewModel.getPrayList(
                data.lat,
                data.long
            )
        }
        val location = GetAdhanDetails.getTimeZoneAndCity(
            requireContext(), data.lat,
            data.long
        )
        binding.textViewTitle.text = location?.city ?: "City"

        binding.imageViewTitle.show()
        val duaArray: Array<String> = resources.getStringArray(R.array.methods)
        binding.textViewTitleJuri.text =
            duaArray[viewModel.getSavedPrayerJurisprudence.toInt()]
        binding.textViewTitleJuri.show()

        setObserver()
    }
}