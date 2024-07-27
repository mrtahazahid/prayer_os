package com.iw.android.prayerapp.utils

import android.media.MediaPlayer
import android.util.Log
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.databinding.RowItemAssetBinding
import com.iw.android.prayerapp.extension.getRawFileSize

class RowItemAsset(private val data: SoundData, private val listener: OnItemClick,var mediaPlayer: MediaPlayer? = null) :
    ViewType<SoundData> {
private var isSoundOn = false

    override fun layoutId(): Int {
        return R.layout.row_item_asset
    }

    override fun data(): SoundData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemAssetBinding).also { binding ->
            binding.textViewTitle.text = data.title
            binding.textViewSize.text = getRawFileSize(data.soundFile, binding.textViewSize.context)

            if (data.isSoundSelected) {
                binding.imageViewCheck.setImageResource(R.drawable.ic_pause)
            } else {
                binding.imageViewCheck.setImageResource(R.drawable.ic_play)
            }

            binding.imageViewCheck.setOnClickListener {
                Log.d("isSoundSelected",data.isSoundSelected.toString())
                if(data.isSoundSelected){

                    binding.imageViewCheck.setImageResource(R.drawable.ic_play)
                    data.isSoundSelected = false
                    listener.onClick(true, data, position)
                }else{
                    listener.onClick(false, data, position)
                }

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
}

fun interface OnItemClick {
    fun onClick(isSoundOn: Boolean, data: SoundData, position: Int)
}
