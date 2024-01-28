package com.iw.android.prayerapp.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.FragmentSeventhOnboardingBinding
import com.iw.android.prayerapp.screens.MainActivity
import com.iw.android.prayerapp.utils.AppConstant.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.utils.TinyDB

class SeventhOnboarding : Fragment() {

    private var _binding: FragmentSeventhOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSeventhOnboardingBinding.inflate(inflater, container, false)

        tinyDB = TinyDB(context)

        binding.btnEnableNotification.setOnClickListener {
            tinyDB.putBoolean(IS_ONBOARDING, true)
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
        }

        binding.notNow.setOnClickListener {
            startActivity(Intent(activity, MainActivity::class.java))
            activity?.finish()
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