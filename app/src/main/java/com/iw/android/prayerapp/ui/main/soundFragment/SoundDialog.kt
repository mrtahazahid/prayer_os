package com.iw.android.prayerapp.ui.main.soundFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.databinding.DialogSoundBinding
import com.iw.android.prayerapp.ui.main.soundFragment.itemView.OnItemClick
import com.iw.android.prayerapp.ui.main.soundFragment.itemView.RowItemSound
import com.iw.android.prayerapp.utils.GetAdhanSound

class SoundDialog : DialogFragment(), View.OnClickListener, OnItemClick {
    private var _binding: DialogSoundBinding? = null
    private val binding get() = _binding!!

    var title = ""
    var subTitle = ""
    var selectedItem = ""
    var selectedItemPosition = 0
    var selectedSound = 0
    var listener: OnDataSelected? = null
    var sound: Int? = null
    var isForNotification = false
    private var previousPosition: Int? = null

    private var notificationList = arrayListOf<SoundData>()

    private var viewTypeArray = ArrayList<ViewType<*>>()
    private var mediaPlayer: MediaPlayer? = null

    private val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
        isCancelable = false

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogSoundBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    private fun initialize() {
        setOnBackPressedListener()
        binding.textViewTitle.text = title
        binding.textViewNamazName.text = subTitle
        Log.d("listener", selectedItem)
        setRecyclerView()

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


    private fun setObserver() {
        viewTypeArray.clear()
        notificationList = if (selectedItemPosition == 0) GetAdhanSound.adhanSound else GetAdhanSound.notificationSound
        for (data in notificationList) {
            data.isSoundSelected = false
            viewTypeArray.add(
                RowItemSound(data, this, selectedSound)
            )
        }
        adapter.items = viewTypeArray
    }


    private fun setOnClickListener() {
        binding.imageViewBack.setOnClickListener(this)
        binding.backView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.backView.id, binding.imageViewBack.id -> {
                listener?.onDataPassed(selectedItem, selectedItemPosition, sound, isForNotification)

                dismiss()
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

    private fun setRecyclerView() {
        binding.recyclerView.adapter = adapter
    }


    override fun onClick(position: Int, data: SoundData) {
        previousPosition?.let {
            notificationList[it].isSoundSelected = false
            adapter.notifyItemChanged(it)
        }

        // Update current item
        notificationList[position].isSoundSelected = true
        adapter.notifyItemChanged(position)
        previousPosition = position

        stopMediaPlayer()
        mediaPlayer = MediaPlayer.create(requireContext(), data.soundFile)
        // Create a new MediaPlayer instance
        selectedItem = data.title
        selectedItemPosition = position
        sound = data.soundFile
        // Start playing the selected sound
        mediaPlayer?.start()


        // Set an event listener to release the MediaPlayer when playback is completed
        mediaPlayer?.setOnCompletionListener {
            stopMediaPlayer()
        }
    }

    private fun setOnBackPressedListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    listener?.onDataPassed(
                        selectedItem,
                        selectedItemPosition,
                        sound,
                        isForNotification
                    )
                    dismiss()
                }
            })
    }
}

fun interface OnDataSelected {
    fun onDataPassed(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        isSoundForNotification: Boolean
    )
}