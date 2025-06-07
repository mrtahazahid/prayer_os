package com.iw.android.prayerapp.ui.main.moreFragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentMoreBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.ui.main.moreFragment.itemView.OnClickMoreItem
import com.iw.android.prayerapp.ui.main.moreFragment.itemView.RowItemMore

class MoreFragment : BaseFragment(R.layout.fragment_more), View.OnClickListener, OnClickMoreItem {

    private var _binding: FragmentMoreBinding? = null
    val binding
        get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private val viewModel: MoreViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var isPlayAdhanChecked = false

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        setRecyclerView()
        setOnBackPressedListener()

    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.getMoreList()) {
            viewTypeArray.add(RowItemMore(data, this))
        }
        adapter.items = viewTypeArray
    }

    override fun setOnClickListener() {
        binding.instagramViewClick.setOnClickListener(this)
        binding.carViewProject.setOnClickListener(this)
        binding.twitteriewClick.setOnClickListener(this)
        binding.policyViewClick.setOnClickListener(this)
        binding.disclaimerClickView.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            binding.instagramViewClick.id -> {
                openCustomTab(requireContext(),"https://www.instagram.com/praywatchapp?igsh=MTBza2t0MHo2Yzdybw==")
            }

            binding.twitteriewClick.id -> {
                openCustomTab(requireContext(),"https://twitter.com/praywatchapp")
            }

            binding.carViewProject.id -> {
                openCustomTab(requireContext(),"https://quranplus.app/apple/")
            }

            binding.policyViewClick.id -> {
                openCustomTab(requireContext(),"https://praywatch.app/privacy/")
            }

            binding.disclaimerClickView.id -> {
                openCustomTab(requireContext(),"https://praywatch.app/disclaimer/")
            }
        }
    }

    override fun onStop() {
        super.onStop()
        // Release MediaPlayer resources when activity stops
        stopSound()
    }

    override fun onMoreItemClick(title: String) {
        when (title) {
            "About  this app" -> openCustomTab(binding.imageView.context, "https://praywatch.app/")

            "Subscribe for updates" -> openCustomTab(
                binding.imageView.context,
                "https://praywatch.app/subscribe/"
            )

            "Share this app" -> shareApp(requireContext())

            "Read tutorial" -> readTutorial()

            "Rate this app" -> rateThisApp()

            "Request support" -> {
                sendUserToGmail(
                    context = requireContext(),
                    lat = viewModel.userLatLong?.latitude ?: 0.0,
                    lng = viewModel.userLatLong?.latitude ?: 0.0,
                    method = viewModel.method!!,
                    methodInt = viewModel.methodInt!!,
                    madhab = viewModel.madhabInt!!
                )
            }

            "Play adhan" -> {
                if (!isPlayAdhanChecked) {
                    isPlayAdhanChecked = true
                    startSound()
                } else {
                    isPlayAdhanChecked = false
                    stopSound()
                }
            }
        }
    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    private fun startSound() {
        val uri =
            Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.raw.adhan_abdul_basit)
        mediaPlayer = MediaPlayer.create(context, uri)
        mediaPlayer?.isLooping = false // This will play sound in repeatable mode.
        mediaPlayer?.start()
    }


    private fun stopSound() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun readTutorial() {
        val intent = Intent(
            binding.imageView.context,
            OnBoardingActivity::class.java
        )
        intent.putExtra("data", "value")
        requireActivity().startActivity(intent)
    }

    private fun rateThisApp() {
        try {
            // Try to open in Play Store app
            requireContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=${requireContext().packageName}")
                )
            )
        } catch (e: ActivityNotFoundException) {
            // Fallback: open in browser
            requireContext().startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=${requireContext().packageName}")
                )
            )
        }
    }


    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {}
            })
    }
}