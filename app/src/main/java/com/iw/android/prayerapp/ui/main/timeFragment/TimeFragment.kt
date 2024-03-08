package com.iw.android.prayerapp.ui.main.timeFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.databinding.FragmentTimeBinding
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.RowItemTime
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.utils.AppConstant
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.TinyDB
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimeFragment : BaseFragment(R.layout.fragment_time), View.OnClickListener {

    private var _binding: FragmentTimeBinding? = null
    val binding
        get() = _binding!!

    private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var dateOffset = 0

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private lateinit var tinyDB: TinyDB


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
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {


        binding.textViewDateTitle.text = getFormattedDate(dateOffset)
        tinyDB = TinyDB(requireContext())
        setRecyclerView()

    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.prayTimeArray) {
            viewTypeArray.add(
                RowItemTime(data, binding.recyclerView, this, viewModel)
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

    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.stopScroll()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.textViewTitle.id-> lifecycleScope.launch{
                Log.d("NamazDetailFajr",viewModel.getFajrDetail().toString())
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
        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openGoogleMapsNearbyPlaces(latitude: Double, longitude: Double) {
        val gmmIntentUri =
            Uri.parse("geo:$latitude,$longitude?q=mosque")

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
        viewModel.getPrayList()

        setObserver()

        val dateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(targetDate)
    }


}