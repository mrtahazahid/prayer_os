package com.iw.android.prayerapp.fragments.timeFragment

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.adapter.GenericListAdapter
import com.iw.android.prayerapp.adapter.OnItemClickListener
import com.iw.android.prayerapp.adapter.ViewType
import com.iw.android.prayerapp.data.PrayTime
import com.iw.android.prayerapp.databinding.FragmentTimeBinding
import com.iw.android.prayerapp.fragments.timeFragment.itemView.RowItemTime
import com.iw.android.prayerapp.utils.AppConstant
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.TinyDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimeFragment : Fragment() {

    private var _binding: FragmentTimeBinding? = null
    val binding
        get() = _binding!!
//    private val viewModel: TimeViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private lateinit var tinyDB: TinyDB
    var prayTimeArray = arrayListOf<PrayTime>()


    private var isItemClick = true

    //    private var dialogBinding: PostDialogBinding? = null
//    private var dialogExitBinding: ExitDialogBinding? = null
    private var dialog: AlertDialog? = null

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
    }

    private fun initialize() {
        tinyDB = TinyDB(requireContext())
        getPrayList()
        setRecyclerView()
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

    private fun setOnClickListener() {

    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

}