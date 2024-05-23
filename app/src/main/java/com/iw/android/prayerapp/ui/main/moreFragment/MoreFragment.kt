package com.iw.android.prayerapp.ui.main.moreFragment

import android.app.AlertDialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.viewModels
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.databinding.FragmentMoreBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.moreFragment.itemView.OnClickPlayAdhan
import com.iw.android.prayerapp.ui.main.moreFragment.itemView.RowItemMore

class MoreFragment : BaseFragment(R.layout.fragment_more), View.OnClickListener, OnClickPlayAdhan {

    private var _binding: FragmentMoreBinding? = null
    val binding
        get() = _binding!!

    private var mediaPlayer: MediaPlayer? = null
    private val viewModel: MoreViewModel by viewModels()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var isItemClick = true

    //    private var dialogBinding: PostDialogBinding? = null
//    private var dialogExitBinding: ExitDialogBinding? = null
    private var dialog: AlertDialog? = null

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

    }

    override fun setObserver() {
        viewTypeArray.clear()
        for (data in viewModel.moreList) {
            viewTypeArray.add(
                RowItemMore(
                    data,
                    requireActivity(),
                    viewModel.userLatLong?.latitude ?: 0.0,
                    viewModel.userLatLong?.latitude ?: 0.0,
                    viewModel.method!!,
                    viewModel.methodInt!!,
                    viewModel.madhabInt!!,
                    requireContext(),
                    this
                )
            )
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

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.instagramViewClick.id -> {
                openCustomTab("https://www.instagram.com/praywatchapp?igsh=MTBza2t0MHo2Yzdybw==")
            }

            binding.twitteriewClick.id -> {
                openCustomTab("https://twitter.com/praywatchapp")
            }

            binding.carViewProject.id -> {
                openCustomTab("https://quranplus.app/apple/")
//                showToast("Work in progress")
            }

            binding.policyViewClick.id -> {
                openCustomTab("https://praywatch.app/privacy/")
            }

            binding.disclaimerClickView.id -> {
                openCustomTab("https://praywatch.app/disclaimer/")
            }
        }
    }


    fun openCustomTab(url: String?) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url))
    }

    private fun startSound() {
        val uri =
            Uri.parse("android.resource://" + requireActivity().packageName + "/" + R.raw.adhan_abdul_basit)
        mediaPlayer = MediaPlayer.create(context, uri)
        mediaPlayer?.isLooping = false // This will play sound in repeatable mode.
        mediaPlayer?.start()
    }

    override fun onStop() {
        super.onStop()
        // Release MediaPlayer resources when activity stops
        stopSound()
    }

    private fun stopSound() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onClick(isChecked: Boolean) {
        if (isChecked) {
            startSound()
        } else {
            stopSound()
        }
    }

}