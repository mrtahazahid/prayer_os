package com.iw.android.prayerapp.fragments.timeFragment.itemView

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.adapter.OnItemClickListener
import com.iw.android.prayerapp.adapter.ViewType
import com.iw.android.prayerapp.data.PrayTime
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding

class RowItemTime(private val data: PrayTime,val recyclerView: RecyclerView) : ViewType<PrayTime> {
    private var isViewShow = false

    override fun layoutId(): Int {
        return R.layout.row_item_pray_time
    }

    override fun data(): PrayTime {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPrayTimeBinding).also { binding ->
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title
            binding.textViewTime.text = data.time

            binding.imageViewDropDownMenu.setOnClickListener {
                if (!isViewShow) {
                    binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_drop_down)
                    binding.detailViews.visibility = View.VISIBLE
                    recyclerView.smoothScrollToPosition(0)
                    isViewShow = true
                } else {
                    binding.detailViews.visibility = View.GONE
                    binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_forward)
                    isViewShow = false
                }

            }

        }
    }
}
