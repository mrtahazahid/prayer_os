package com.iw.android.prayerapp.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentSixthOnboardingBinding
import com.iw.android.prayerapp.screens.MainActivity

class SixthOnboarding : Fragment() {

    private var _binding : FragmentSixthOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSixthOnboardingBinding.inflate(inflater, container, false)

        binding.btnEnableNotification.setOnClickListener {
            findNavController().navigate(R.id.action_sixthOnboarding_to_seventhOnboarding)
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_sixthOnboarding_to_seventhOnboarding)
        }

        binding.skip.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}