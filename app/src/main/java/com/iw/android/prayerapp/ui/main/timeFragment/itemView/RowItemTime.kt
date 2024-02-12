package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.databinding.RowItemPrayTimeBinding

class RowItemTime(private val data: PrayTime, val recyclerView: RecyclerView) : ViewType<PrayTime> {
    private var isViewShow = false
    private var currentMinute = 20

    override fun layoutId(): Int {
        return R.layout.row_item_pray_time
    }

    override fun data(): PrayTime {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemPrayTimeBinding).also { binding ->
            binding.view4.visibility = if (data.title == "Last Third") View.GONE else View.VISIBLE
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title
            binding.textViewTime.text = data.time

            binding.imageViewDropDownMenu.setOnClickListener {
                if (!isViewShow) {
                    binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_drop_down)
                    binding.detailViews.visibility = View.VISIBLE
                    recyclerView.smoothScrollToPosition(0)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            binding.textViewTime.context,
                            R.color.yellow_text
                        )
                    )
                    isViewShow = true
                } else {
                    binding.detailViews.visibility = View.GONE
                    binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_forward)
                    binding.textViewTime.setTextColor(
                        ContextCompat.getColorStateList(
                            binding.textViewTime.context,
                            R.color.white
                        )
                    )
                    isViewShow = false
                }
            }

            binding.imageViewAdd.setOnClickListener {
                binding.textViewSetTime.text = incrementMinute()
            }

            binding.imageViewRemove.setOnClickListener {
                binding.textViewSetTime.text = decrementMinute()
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
