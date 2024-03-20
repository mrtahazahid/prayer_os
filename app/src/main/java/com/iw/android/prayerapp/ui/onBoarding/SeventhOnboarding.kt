package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentSeventhOnboardingBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingViewModel
import kotlinx.coroutines.launch

class SeventhOnboarding : BaseFragment(R.layout.fragment_seventh_onboarding) {

    private var _binding: FragmentSeventhOnboardingBinding? = null
    private val binding get() = _binding!!

    val viewModel: OnBoardingViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSeventhOnboardingBinding.inflate(inflater, container, false)
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
          lifecycleScope.launch {   viewModel.saveIsOnBoarding()}
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }

        binding.notNow.setOnClickListener {
//            startActivity(Intent(requireActivity() , MainActivity::class.java))
//            requireActivity().finish()
            Toast.makeText(requireContext(), "Work in process", Toast.LENGTH_SHORT).show()
        }

        binding.skip.setOnClickListener {
//            startActivity(Intent(requireActivity(), MainActivity::class.java))
//            requireActivity().finish()
            Toast.makeText(requireContext(), "Work in process", Toast.LENGTH_SHORT).show()
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