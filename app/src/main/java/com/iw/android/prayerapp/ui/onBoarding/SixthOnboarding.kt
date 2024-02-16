package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentSixthOnboardingBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity

class SixthOnboarding : BaseFragment(R.layout.fragment_sixth_onboarding) {

    private var _binding: FragmentSixthOnboardingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSixthOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }


    override fun initialize() {
        setOnBackPressedListener()
    }

    override fun setObserver() {

    }

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            findNavController().navigate(R.id.action_sixthOnboarding_to_seventhOnboarding)
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_sixthOnboarding_to_seventhOnboarding)
        }

        binding.skip.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                ).putExtra("skip", "userSkipped")
            )
            requireActivity().finish()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            })
    }
}