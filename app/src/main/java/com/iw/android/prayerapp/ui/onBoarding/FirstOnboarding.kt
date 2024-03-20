package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentFirstOnboardingBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity

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

    }

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.btnGetStarted.setOnClickListener {
//            startProgress()
            findNavController().navigate(R.id.action_firstOnboarding_to_secondOnboarding)

        }

        binding.skip.setOnClickListener {
            Toast.makeText(requireContext(), "Work in process", Toast.LENGTH_SHORT).show()
//            requireActivity().startActivity(
//                Intent(
//                    requireContext(),
//                    MainActivity::class.java
//                ).putExtra("skip", "userSkipped")
//            )
//            requireActivity().finish()

        }
    }

//    private fun startProgress() {
//        Handler(Looper.getMainLooper()).postDelayed({
//            stopProgress()
//            findNavController().navigate(R.id.action_firstOnboarding_to_secondOnboarding)
//        }, 8000)
//
//        binding.pBar.visibility = View.VISIBLE
//        Toast.makeText(context, "Getting your current location", Toast.LENGTH_SHORT).show()
//    }

    private fun stopProgress() {
        binding.pBar.visibility = View.GONE
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