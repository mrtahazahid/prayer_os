package com.iw.android.prayerapp.ui.main.moreFragment.itemView

import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MoreData
import com.iw.android.prayerapp.databinding.RowItemMoreBinding

class RowItemMore(
    private val data: MoreData,
    private val listener: OnClickMoreItem
) : ViewType<MoreData> {

    private var isPlayAdhanChecked = false
    override fun layoutId(): Int {
        return R.layout.row_item_more
    }

    override fun data(): MoreData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemMoreBinding).also { binding ->
            binding.view4.visibility = if (data.title == "Play adhan") View.GONE else View.VISIBLE
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title

            if (data.title == "Read tutorial") {
                binding.imageViewDropDownMenu.visibility = View.VISIBLE
            } else {
                binding.imageViewDropDownMenu.visibility = View.GONE
            }

            binding.mainView.setOnClickListener {
                if (data.title == "Play adhan") {
                    if (!isPlayAdhanChecked) {
                        isPlayAdhanChecked = true
                        binding.imageView.setImageResource(R.drawable.ic_mute_mike)
                        binding.textViewTitle.text = "Stop adhan"
                    } else {
                        isPlayAdhanChecked = false
                        binding.imageView.setImageResource(R.drawable.ic_mike)
                        binding.textViewTitle.text = "Play adhan"
                    }
                }
                listener.onMoreItemClick(data.title)
            }
        }
    }
}

fun interface OnClickMoreItem {
    fun onMoreItemClick(title: String)
}