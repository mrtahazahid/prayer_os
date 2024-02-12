package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentThirdOnboardingBinding
import com.iw.android.prayerapp.ui.activities.screens.MainActivity

class ThirdOnboarding : BaseFragment(R.layout.fragment_third_onboarding) {

    private var _binding: FragmentThirdOnboardingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdOnboardingBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
    }

    override fun setObserver() {
    }

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
            findNavController().navigate(R.id.action_thirdOnboarding_to_fourthOnboarding)
        }

        binding.notNow.setOnClickListener {
            findNavController().navigate(R.id.action_thirdOnboarding_to_fourthOnboarding)
        }

        binding.skip.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}