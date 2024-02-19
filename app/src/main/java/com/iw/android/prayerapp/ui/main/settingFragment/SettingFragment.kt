package com.iw.android.prayerapp.ui.main.settingFragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentSettingBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails.getTimeZoneAndCity
import com.iw.android.prayerapp.utils.TinyDB
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class SettingFragment : BaseFragment(R.layout.fragment_setting), View.OnClickListener {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels()


    private var isCalViewShow = false
    private var isLocViewShow = false
    private var geofence = 75

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        lifecycleScope.launch { geofence = viewModel.getGeofenceRadius() }
        binding.textViewGeofenceRadius.text = "$geofence Kilometers"

        binding.textViewCoordinatesPoint.text = getFormattedCoordinates(
            viewModel.getUserLatLong?.latitude ?: 0.0,
            viewModel.getUserLatLong?.longitude ?: 0.0
        )
        val location = getTimeZoneAndCity(
            requireContext(), viewModel.getUserLatLong?.latitude ?: 0.0,
            viewModel.getUserLatLong?.longitude ?: 0.0
        )
        binding.textViewCityName.text = location?.city
        binding.textViewCityTimeZoneName.text = location?.timeZone
        binding.switchAutomatic.isChecked = viewModel.getAutomaticLocation
    }

    override fun setObserver() {
    }

    override fun setOnClickListener() {
        binding.imageViewCalculationArrow.setOnClickListener(this)
        binding.imageViewLocationArrowButton.setOnClickListener(this)
        binding.imageViewAddGeofence.setOnClickListener(this)
        binding.imageViewMinusGeofence.setOnClickListener(this)
        binding.switchAutomatic.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setLocationAutomaticValue(isChecked)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewAddGeofence.id -> {
                binding.textViewGeofenceRadius.text = incrementGeofence(5)
                viewModel.setGeofenceRadius(geofence)
            }

            binding.imageViewMinusGeofence.id -> {
                binding.textViewGeofenceRadius.text = decrementGeofence(5)
                viewModel.setGeofenceRadius(geofence)
            }

            binding.imageViewCalculationArrow.id -> {
                if (!isCalViewShow) {
                    binding.imageViewCalculationArrow.setImageResource(R.drawable.ic_drop_down)
                    binding.calculationMainDetailViews.visibility = View.VISIBLE
                    isCalViewShow = true
                    spinnerMethod()
                    spinnerElevation()
                    spinnerJurisprudence()
                } else {
                    binding.imageViewCalculationArrow.setImageResource(R.drawable.ic_forward)
                    binding.calculationMainDetailViews.visibility = View.GONE
                    isCalViewShow = false
                }
            }

            binding.imageViewLocationArrowButton.id -> {
                if (!isLocViewShow) {
                    binding.imageViewLocationArrowButton.setImageResource(R.drawable.ic_drop_down)
                    binding.locationDetailViews.visibility = View.VISIBLE
                    isLocViewShow = true
                } else {
                    binding.imageViewLocationArrowButton.setImageResource(R.drawable.ic_forward)
                    binding.locationDetailViews.visibility = View.GONE
                    isLocViewShow = false
                }

            }
        }
    }

    private fun spinnerElevation() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.elevation,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerElevation.adapter = adapter
        binding.spinnerElevation.setSelection(viewModel.getSavedPrayerElevationRule.toInt())

        binding.spinnerElevation.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch { viewModel.savePrayerElevation(position.toString()) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun spinnerJurisprudence() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.jurisprudence,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJurisprudence.adapter = adapter
        binding.spinnerJurisprudence.setSelection(viewModel.getSavedPrayerJurisprudence.toInt())

        binding.spinnerJurisprudence.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch { viewModel.savePrayerJurisprudence(position.toString()) }
                binding.textViewJurisprudenceDes.visibility = if(position== 0) View.GONE else View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun spinnerMethod() {

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.methods,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMethod.adapter = adapter
        binding.spinnerMethod.setSelection(viewModel.getSavedPrayerMethod.toInt())

        binding.spinnerMethod.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch { viewModel.savePrayerMethod(position.toString()) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getFormattedCoordinates(latitude: Double, longitude: Double): String {
        val decimalFormat = DecimalFormat("#.##")
        val formattedLatitude = decimalFormat.format(latitude)
        val formattedLongitude = decimalFormat.format(longitude)

        return "$formattedLatitude°, $formattedLongitude°"
    }

    private fun incrementGeofence(incrementBy: Int): String {
        geofence = (geofence + incrementBy).coerceIn(
            75,
            105
        ) // Ensure the value is within the specified range
        return "$geofence Kilometers"
    }

    private fun decrementGeofence(decrementBy: Int): String {
        geofence = (geofence - decrementBy).coerceIn(
            75,
            105
        ) // Ensure the value is within the specified range
        return "$geofence Kilometers"
    }
}