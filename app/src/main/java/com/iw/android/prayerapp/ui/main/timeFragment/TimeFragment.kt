package com.iw.android.prayerapp.ui.main.timeFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.databinding.FragmentTimeBinding
import com.iw.android.prayerapp.ui.main.timeFragment.itemView.RowItemTime
import com.iw.android.prayerapp.ui.activities.screens.MainActivity
import com.iw.android.prayerapp.utils.AppConstant
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.TinyDB

class TimeFragment : BaseFragment(R.layout.fragment_time), View.OnClickListener {

    private var _binding: FragmentTimeBinding? = null
    val binding
        get() = _binding!!

    //    private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private lateinit var tinyDB: TinyDB
    private var prayTimeArray = arrayListOf<PrayTime>()

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
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        tinyDB = TinyDB(requireContext())
        getPrayList()
        setRecyclerView()

    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in prayTimeArray) {
            viewTypeArray.add(
                RowItemTime(data, binding.recyclerView)
            )
        }
        adapter.items = viewTypeArray
    }

    private fun getPrayList() {
        currentLatitude =
            if (tinyDB.getDouble(AppConstant.CURRENT_LATITUDE).isNaN() || tinyDB.getDouble(
                    AppConstant.CURRENT_LATITUDE
                ).toString().isEmpty()
            ) {
                0.0
            } else {
                tinyDB.getDouble(AppConstant.CURRENT_LATITUDE)
            }

        currentLongitude =
            if (tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE).isNaN() || tinyDB.getDouble(
                    AppConstant.CURRENT_LONGITUDE
                ).toString().isEmpty()
            ) {
                0.0
            } else {
                tinyDB.getDouble(AppConstant.CURRENT_LONGITUDE)
            }
        val getPrayerTime = GetAdhanDetails.getPrayTime(currentLatitude, currentLongitude)

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Fajr",
                getPrayerTime[0],
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Sunrise",
                "6:58AM",
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Dhuhr",
                getPrayerTime[1],
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Asr",
                getPrayerTime[2],
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Maghrib",
                getPrayerTime[3],
            )
        )
        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Isha",
                getPrayerTime[4],
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Midnight",
                "11:42PM",
            )
        )

        prayTimeArray.add(
            PrayTime(
                R.drawable.ic_mike,
                "Last Third",
                "1:40AM",
            )
        )

    }

    override fun setOnClickListener() {
        binding.islamicHolidayClickView.setOnClickListener(this)
        binding.masjidClickView.setOnClickListener(this)

    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.islamicHolidayClickView.id -> {
                (requireActivity() as MainActivity).replaceFragment(IslamicHolidayFragment())
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

}