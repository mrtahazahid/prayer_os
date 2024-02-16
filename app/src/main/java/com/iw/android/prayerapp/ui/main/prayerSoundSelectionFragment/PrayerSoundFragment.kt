package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.databinding.FragmentPrayerSoundBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.PrayerEnumType
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView.RowItemPrayerSound
import com.iw.android.prayerapp.utils.TinyDB
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class PrayerSoundFragment : BaseFragment(R.layout.fragment_prayer_sound), View.OnClickListener {

    private var _binding: FragmentPrayerSoundBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    private var isItemClick = true
    var prayerSoundList = arrayListOf<PrayerSoundData>()

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
        _binding = FragmentPrayerSoundBinding.inflate(inflater, container, false)
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

        viewTypeArray.clear()
        for (data in prayerSoundList) {
            viewTypeArray.add(
                RowItemPrayerSound(data,this)
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
        prayerSoundList.add(PrayerSoundData("Adhan",R.drawable.ic_mike,PrayerEnumType.ADHAN.getValue(),
            isImageForwardShow = true,
            isItemSelected = true,
            selectedItemTitle = "adhan"
        ))
        prayerSoundList.add(PrayerSoundData("Tones",R.drawable.ic_mike,PrayerEnumType.TONES.getValue(),
            isImageForwardShow = true,
            isItemSelected = false
        ))
        prayerSoundList.add(PrayerSoundData("Vibrate",R.drawable.ic_mike,PrayerEnumType.VIBRATE.getValue(),
            isImageForwardShow = false,
            isItemSelected = false
        ))
        prayerSoundList.add(PrayerSoundData("Silent",R.drawable.ic_mike,PrayerEnumType.SILENT.getValue(),
            isImageForwardShow = false,
            isItemSelected = false
        ))
        prayerSoundList.add(PrayerSoundData("Off",R.drawable.ic_mike,PrayerEnumType.OFF.getValue(),
            isImageForwardShow = false,
            isItemSelected = false
        ))
    }
}