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
import com.iw.android.prayerapp.databinding.FragmentFifthOnboardingBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity

class FifthOnboarding : BaseFragment(R.layout.fragment_fifth_onboarding) {

    private var _binding: FragmentFifthOnboardingBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFifthOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {setOnBackPressedListener() }

    override fun setObserver() {}

    override fun setOnClickListener() {
        binding.btnEnableNotification.setOnClickListener {
//            findNavController().navigate(R.id.action_fifthOnboarding_to_sixthOnboarding)
        }

        binding.notNow.setOnClickListener {
//            requireActivity().startActivity(
//                Intent(
//                    requireContext(),
//                    MainActivity::class.java
//                ).putExtra("skip", "userSkipped")
//            )
//            requireActivity().finish()
            Toast.makeText(requireContext(), "Work in process", Toast.LENGTH_SHORT).show()
        }

        binding.skip.setOnClickListener {
//            requireActivity().startActivity(
//                Intent(
//                    requireContext(),
//                    MainActivity::class.java
//                ).putExtra("skip", "userSkipped")
//            )
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