package com.iw.android.prayerapp.fragments.moreFragment.itemView

import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.adapter.OnItemClickListener
import com.iw.android.prayerapp.adapter.ViewType
import com.iw.android.prayerapp.data.MoreData
import com.iw.android.prayerapp.databinding.RowItemMoreBinding

class RowItemMore(private val data: MoreData) : ViewType<MoreData> {
    private var isViewShow = false

    override fun layoutId(): Int {
        return R.layout.row_item_more
    }

    override fun data(): MoreData {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemMoreBinding).also { binding ->
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title

            if(data.title == "Read tutorial"){
                binding.imageViewDropDownMenu.visibility = View.VISIBLE
                binding.imageViewDropDownMenu.setOnClickListener {
                    if (!isViewShow) {
                        binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_drop_down)
                        isViewShow = true
                    } else {
                        //  binding.detailViews.visibility = View.GONE
                        binding.imageViewDropDownMenu.setImageResource(R.drawable.ic_forward)
                        isViewShow = false
                    }
                }
            }else{
                binding.imageViewDropDownMenu.visibility = View.GONE
            }




        }
    }
}
