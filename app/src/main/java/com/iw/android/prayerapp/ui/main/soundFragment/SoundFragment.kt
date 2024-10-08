package com.iw.android.prayerapp.ui.main.soundFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.databinding.FragmentSoundBinding
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.soundFragment.itemView.OnItemClick
import com.iw.android.prayerapp.ui.main.soundFragment.itemView.RowItemSound
import com.iw.android.prayerapp.utils.GetAdhanSound

class SoundFragment : BaseFragment(R.layout.fragment_sound), View.OnClickListener, OnItemClick {

    private var _binding: FragmentSoundBinding? = null
    val binding
        get() = _binding!!

    //    private val viewModel: TimeViewModel by viewModels()
    private val args by navArgs<SoundFragmentArgs>()
    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var mediaPlayer: MediaPlayer? = null
    private var dateOffset = 0

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    val soundList = GetAdhanSound().adhanSound
    val notificationList = GetAdhanSound().notificationSound
    val duaList = GetAdhanSound().duaSound
    var selectedList = arrayListOf<SoundData>()
    private var selectedItem = ""
    private var selectedItemPosition = 0

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
        _binding = FragmentSoundBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.stop()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        binding.textViewTitle.text = args.title
        binding.textViewNamazName.text = args.subTitle
        setRecyclerView()

    }

    override fun setObserver() {
        viewTypeArray.clear()
        selectedList = if (args.type == "true") soundList else notificationList


        for (data in selectedList) {
            viewTypeArray.add(
                RowItemSound(data, this, selectedItemPosition)
            )
        }
        adapter.items = viewTypeArray
    }


    override fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)

    }

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewBack.id -> {
                setFragmentResult(
                    "selected_sound",
                    bundleOf(
                        "sound" to selectedItem,
                        "soundPosition" to selectedItemPosition,
                        "isSoundSelectNotification" to args.dataFromTimeFragment.toBoolean()
                    )
                )
                findNavController().popBackStack()
            }
        }

    }

    private fun stopMediaPlayer() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }

    }

    override fun onClick(position: Int, data: SoundData) {
        for (checked in selectedList) {
            checked.isSoundSelected = false
        }

        // Select the clicked item
        selectedList[position].isSoundSelected = true
        // Notify the adapter about the change in the entire dataset
        binding.recyclerView.adapter?.notifyDataSetChanged()
        stopMediaPlayer()
        mediaPlayer = MediaPlayer.create(requireContext(), data.soundFile)
        // Create a new MediaPlayer instance
        selectedItem = data.title
        selectedItemPosition = position

        // Start playing the selected sound
        mediaPlayer?.start()

        // Set an event listener to release the MediaPlayer when playback is completed
        mediaPlayer?.setOnCompletionListener {
            stopMediaPlayer()
        }
    }

    override fun onItemSelected(data: SoundData) {
        selectedItem = data.title
    }
}

