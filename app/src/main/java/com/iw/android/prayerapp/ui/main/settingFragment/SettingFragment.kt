package com.iw.android.prayerapp.ui.main.settingFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentSettingBinding
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.ui.activities.screens.MainActivity
import com.iw.android.prayerapp.utils.TinyDB

class SettingFragment : BaseFragment(R.layout.fragment_setting), View.OnClickListener {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var tinyDB: TinyDB

    private var isCalViewShow = false
    private var isLocViewShow = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        (requireActivity() as MainActivity).showBottomSheet()
        tinyDB = TinyDB(context)
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
        binding.imageViewCalculationArrow.setOnClickListener(this)
        binding.imageViewLocationArrowButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewCalculationArrow.id -> {
                if (!isCalViewShow) {
                    binding.imageViewCalculationArrow.setImageResource(R.drawable.ic_drop_down)
                    binding.calculationMainDetailViews.visibility = View.VISIBLE
                    isCalViewShow = true
                } else {
                    binding.imageViewCalculationArrow.setImageResource(R.drawable.ic_forward)
                    binding.calculationMainDetailViews.visibility = View.GONE
                    isCalViewShow = false
                }
            }

            binding.imageViewLocationArrowButton.id -> {
                if (!isLocViewShow) {
                    binding.imageViewLocationArrowButton.setImageResource(R.drawable.ic_drop_down)
                    binding.locationDetailViews.visibility = View.VISIBLE
                    isLocViewShow = true
                } else {
                    binding.imageViewLocationArrowButton.setImageResource(R.drawable.ic_forward)
                    binding.locationDetailViews.visibility = View.GONE
                    isLocViewShow = false
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}