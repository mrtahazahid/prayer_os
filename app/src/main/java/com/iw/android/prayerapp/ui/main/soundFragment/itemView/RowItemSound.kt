package com.iw.android.prayerapp.ui.main.soundFragment.itemView

import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.databinding.RowItemSoundBinding

class RowItemSound(private val data: SoundData, private val listener: OnItemClick) :
    ViewType<SoundData> {


    override fun layoutId(): Int {
        return R.layout.row_item_sound
    }

    override fun data(): SoundData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemSoundBinding).also { binding ->
            binding.textViewTitle.text = data.title
            if(data.isSoundSelected){
                binding.imageViewCheck.visibility = View.VISIBLE
            }

            binding.mainView.setOnClickListener {

                listener.onClick(position, data)

            }
        }
    }

}

fun interface OnItemClick {
    fun onClick(position: Int, data: SoundData)
}
