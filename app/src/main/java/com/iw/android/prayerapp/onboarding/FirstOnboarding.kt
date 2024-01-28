package com.iw.android.prayerapp.onboarding

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentFirstOnboardingBinding
import com.iw.android.prayerapp.screens.MainActivity
import com.iw.android.prayerapp.utils.TinyDB

class FirstOnboarding : Fragment() {

    private var _binding : FragmentFirstOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstOnboardingBinding.inflate(inflater,container, false)

        tinyDB = TinyDB(context)

        binding.btnGetStarted.setOnClickListener {
//            startProgress()
            findNavController().navigate(R.id.action_firstOnboarding_to_secondOnboarding)

        }

        binding.skip.setOnClickListener {
            startActivity(Intent(activity,MainActivity::class.java))
            activity?.finish()
        }

        return binding.root
    }

    private fun startProgress() {
        Handler(Looper.getMainLooper()).postDelayed({
            stopProgress()
            findNavController().navigate(R.id.action_firstOnboarding_to_secondOnboarding)
        }, 8000)

        binding.pBar.visibility = View.VISIBLE
        Toast.makeText(context, "Getting your current location", Toast.LENGTH_SHORT).show()
    }

    private fun stopProgress() {
        binding.pBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}