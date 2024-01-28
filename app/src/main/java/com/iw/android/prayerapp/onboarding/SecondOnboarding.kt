package com.iw.android.prayerapp.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentSecondOnboardingBinding
import com.iw.android.prayerapp.screens.MainActivity

class SecondOnboarding : Fragment() {

    private var _binding : FragmentSecondOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondOnboardingBinding.inflate(inflater,container, false)

        binding.btnEnableNotification.setOnClickListener {
            findNavController().navigate(R.id.action_secondOnboarding_to_thirdOnboarding)
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_secondOnboarding_to_thirdOnboarding)
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