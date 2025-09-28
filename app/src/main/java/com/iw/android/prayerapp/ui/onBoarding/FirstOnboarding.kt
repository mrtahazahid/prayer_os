package com.iw.android.prayerapp.ui.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentFirstOnboardingBinding
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.utils.decodeImage.decodeSampledBitmap

class FirstOnboarding : BaseFragment(R.layout.fragment_first_onboarding) {

    private var _binding: FragmentFirstOnboardingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstOnboardingBinding.inflate(inflater, container, false)
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
        val bitmap = decodeSampledBitmap(requireContext(), R.drawable.prayer_removebg, 1024, 1024)
        binding.imageView.setImageBitmap(bitmap)

    }

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.btnGetStarted.setOnClickListener {
            findNavController().navigate(R.id.action_firstOnboarding_to_secondOnboarding)
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
                    if (!(requireActivity() as OnBoardingActivity).data.isNullOrEmpty() && (requireActivity() as OnBoardingActivity).data != "null") {
                        requireActivity().finish()
                    }
                }
            })
    }
}