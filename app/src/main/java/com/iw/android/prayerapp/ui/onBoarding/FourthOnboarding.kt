package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentFourthOnboardingBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import com.iw.android.prayerapp.utils.GetAdhanDetails
import kotlinx.coroutines.launch
import java.util.Date

class FourthOnboarding : BaseFragment(R.layout.fragment_fourth_onboarding) {

    private var _binding: FragmentFourthOnboardingBinding? = null
    private val binding get() = _binding!!

    val viewModel: OnBoardingViewModel by viewModels()

    var lat = 0.0
    var long = 0.0
    private var isMethodSelected = false
    private var isJurisprudenceSelected = false
    private var isElevationSelected = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFourthOnboardingBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        val args = arguments
        lat = args?.getDouble("lat") ?: 0.0
        long = args?.getDouble("long") ?: 0.0
        setPrayerTime()
        setOnBackPressedListener()
        spinnerMethod()
        spinnerJurisprudence()
        spinnerElevation()
    }

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            lifecycleScope.launch {
                if (!isElevationSelected) {

                    viewModel.savePrayerElevation(
                        0.toString()
                    )

                }
                if (!isJurisprudenceSelected) {
                    isJurisprudenceSelected = true

                    viewModel.savePrayerJurisprudence(
                        0.toString()
                    )

                }
                if (!isMethodSelected) {

                    viewModel.savePrayerMethod(
                        0.toString()
                    )

                }
                requireActivity().runOnUiThread {
                    findNavController().navigate(R.id.action_fourthOnboarding_to_seventhOnboarding)
                }

            }
        }

        binding.notNow.setOnClickListener {
            requireActivity().startActivity(
                Intent(requireContext(), MainActivity::class.java)
            )
            requireActivity().finish()

        }

        binding.skip.setOnClickListener {
            requireActivity().startActivity(
                Intent(requireContext(), MainActivity::class.java)
            )
            requireActivity().finish()
        }

    }

    private fun spinnerElevation() {
        lifecycleScope.launch {
            viewModel.savePrayerElevation(
                0.toString()
            )
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.elevation,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerElevation.adapter = adapter


        binding.spinnerElevation.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                isElevationSelected = true
                lifecycleScope.launch {
                    viewModel.savePrayerElevation(
                        position.toString()
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun spinnerJurisprudence() {
        lifecycleScope.launch {
            viewModel.savePrayerJurisprudence(
                0.toString()
            )
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.jurisprudence,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJurisprudence.adapter = adapter


        binding.spinnerJurisprudence.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                isJurisprudenceSelected = true
                lifecycleScope.launch {
                    viewModel.savePrayerJurisprudence(
                        position.toString()
                    )
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun spinnerMethod() {
        lifecycleScope.launch {
            viewModel.savePrayerMethod(
                0.toString()
            )
        }
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.methods,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMethod.adapter = adapter


        binding.spinnerMethod.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                isMethodSelected = true
                lifecycleScope.launch {
                    viewModel.savePrayerMethod(
                        position.toString()
                    )
                }


            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action\
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setPrayerTime() {
        val getPrayerTime = GetAdhanDetails.getPrayTime(
            lat, long, viewModel.method?: CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = Madhab.SHAFI),
            Date()
        )

        binding.textViewFajrTime.text = getPrayerTime[0]
        binding.textViewSunriseTime.text = getPrayerTime[1]
        binding.textViewDuhrTime.text = getPrayerTime[2]
        binding.textViewAsrTime.text = getPrayerTime[3]
        binding.textViewMagribTime.text = getPrayerTime[4]
        binding.textViewIshaTime.text = getPrayerTime[5]
    }


    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!(requireActivity() as OnBoardingActivity).data.isNullOrEmpty() && (requireActivity() as OnBoardingActivity).data != "null") {
                        requireActivity().finish()
                    }
                }
            })
    }


}