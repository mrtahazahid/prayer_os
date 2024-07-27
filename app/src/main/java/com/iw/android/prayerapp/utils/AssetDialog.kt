package com.iw.android.prayerapp.utils

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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
    var arraList = arrayListOf<SoundData>()
    private var previousPosition: Int? = null

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
        arraList = GetAdhanSound.assetList
    }

    private fun setObserver() {
        viewTypeArray.clear()


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
        Log.d("isSoundOn",isSoundOn.toString())
        if (isSoundOn) {
            stopMediaPlayer()
            return
        }
        previousPosition?.let {
            arraList[it].isSoundSelected = false
            adapter.notifyItemChanged(it)
        }

        // Update current item
        arraList[position].isSoundSelected = true
        adapter.notifyItemChanged(position)
        previousPosition = position

        stopMediaPlayer()
        mediaPlayer = MediaPlayer.create(requireContext(), data.soundFile)
        startMediaPlayer()

        // Set an event listener to release the MediaPlayer when playback is completed
        mediaPlayer?.setOnCompletionListener {
            stopMediaPlayer()
            arraList[position].isSoundSelected = false
            adapter.notifyItemChanged(position)
        }
    }

    override fun onPause() {
        super.onPause()
        stopMediaPlayer()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        stopMediaPlayer()
    }


}