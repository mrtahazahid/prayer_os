package com.iw.android.prayerapp.utils

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.databinding.AssetDialogBinding

class AssetDialog : DialogFragment(), OnItemClick {
    private lateinit var binding: AssetDialogBinding

    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var mediaPlayer: MediaPlayer? = null
    val arraList = arrayListOf<SoundData>()

    val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AssetDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    private fun initialize() {
        binding.recyclerView.adapter = adapter
    }

    private fun setObserver() {
        viewTypeArray.clear()

        arraList.addAll(GetAdhanSound.adhanSound)
        arraList.addAll(GetAdhanSound.notificationSound)
        for (data in arraList) {
            viewTypeArray.add(
                RowItemAsset(data, this)
            )
        }
        adapter.items = viewTypeArray
    }

    private fun setOnClickListener() {
        binding.backView.setOnClickListener {
            dismiss()
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

    private fun startMediaPlayer() {
        mediaPlayer?.start()
    }


    override fun onClick(isSoundOn: Boolean, data: SoundData, position: Int) {
        for (checked in arraList) {
            checked.isSoundSelected = false
        }

        // Select the clicked item
        arraList[position].isSoundSelected = true
        // Notify the adapter about the change in the entire dataset
        binding.recyclerView.adapter?.notifyDataSetChanged()
        //stopMediaPlayer()
        val uri =  Uri.parse("android.resource://" + requireContext().packageName + "/" + data.soundFile)
        mediaPlayer = MediaPlayer.create(requireContext(),uri )
        // Create a new MediaPlayer instance
        if (isSoundOn) {
            stopMediaPlayer()
        } else {
            mediaPlayer?.start()
        }


        // Set an event listener to release the MediaPlayer when playback is completed
        mediaPlayer?.setOnCompletionListener {
            stopMediaPlayer()
        }

    }
}