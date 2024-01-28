package com.iw.android.prayerapp.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentFourthOnboardingBinding
import com.iw.android.prayerapp.screens.MainActivity

class FourthOnboarding : Fragment() {

    private var _binding: FragmentFourthOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFourthOnboardingBinding.inflate(inflater, container, false)

        spinnerMethod()
        spinnerJurisprudence()
        spinnerElevation()

        binding.btnEnableNotification.setOnClickListener {
            findNavController().navigate(R.id.action_fourthOnboarding_to_fifthOnboarding)
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_fourthOnboarding_to_fifthOnboarding)
        }

        binding.skip.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        return binding.root
    }

    private fun spinnerElevation() {
        val lst = arrayListOf(
            "Automatic",
            "Night Middle",
            "One/Seventh",
            "Angle-based"
        )

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner, lst)

        binding.spElevationRule.adapter = adapter

        binding.spElevationRule.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun spinnerJurisprudence() {
        val lst = arrayListOf(
            "Standard",
            "Hanafi"
        )

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner, lst)

        binding.spJurisprudence.adapter = adapter

        binding.spJurisprudence.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    private fun spinnerMethod() {
        val lst = arrayListOf(
            "Muslim World League",
            "Islamic Society of N.America (ISNA",
            "Moonsighting Committee",
            "Egyptian General Authority of Survey",
            "Algerian Ministry of Awqaf and Religious Affairs",
            "Tunisian Ministry of Religious Affairs",
            "London Unified Prayer Timetable",
            "Umm Al-Quran University",
            "Umm Al-Qura University, Makkah",
            "Authority of Dubai - UAE",
            "Authority of Kuwait",
            "Authority of Qatar"
        )

        val adapter = ArrayAdapter(requireContext(), R.layout.custom_spinner, lst)

        binding.spMethod.adapter = adapter

        binding.spMethod.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}