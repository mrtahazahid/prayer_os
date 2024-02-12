package com.iw.android.prayerapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentSeventhOnboardingBinding
import com.iw.android.prayerapp.ui.activities.screens.MainActivity
import com.iw.android.prayerapp.utils.AppConstant.Companion.IS_ONBOARDING
import com.iw.android.prayerapp.utils.TinyDB

class SeventhOnboarding : BaseFragment(R.layout.fragment_seventh_onboarding) {

    private var _binding: FragmentSeventhOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB


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
        tinyDB = TinyDB(context)
    }

    override fun setObserver() {

    }

    override fun setOnClickListener() {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}