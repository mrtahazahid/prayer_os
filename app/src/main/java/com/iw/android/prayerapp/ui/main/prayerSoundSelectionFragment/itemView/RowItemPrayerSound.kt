package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.itemView

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.databinding.RowItemPraySoundBinding
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding

class RowItemPrayerSound(private val data: PrayerSoundData) : ViewType<PrayerSoundData> {
    private var isViewShow = false
    private var currentMinute = 20

    override fun layoutId(): Int {
        return R.layout.row_item_pray_sound
    }

    override fun data(): PrayerSoundData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPraySoundBinding).also { binding ->
            binding.imageViewCheck.visibility =    if(data.isItemSelected) View.VISIBLE else View.GONE

            //binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
            binding.imageViewDropDownMenu.visibility = if(data.isImageForwardShow) View.VISIBLE else View.GONE
            binding.imageView.setImageResource(data.icon)
            binding.textViewTitle.text = data.title
            binding.textViewSelectSoundTitle.text = data.selectedItemTitle

            binding.imageViewDropDownMenu.setOnClickListener {

            }
            binding.mainView.setOnClickListener {
               data.isItemSelected = true
                binding.imageViewCheck.visibility = View.VISIBLE
            }

        }
    }

    private fun incrementMinute(): String {
        currentMinute++
        return "$currentMinute min"
    }

    private fun decrementMinute(): String {
        currentMinute--
        return "$currentMinute min"
    }
}
