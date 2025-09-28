package com.iw.android.prayerapp.utils.method.itemView

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MethodData
import com.iw.android.prayerapp.databinding.RowItemMethodBinding

class RowItemMethod(private val data: MethodData, private val listener: OnItemClick) :
    ViewType<MethodData> {

    override fun layoutId(): Int {
        return R.layout.row_item_method
    }

    override fun data(): MethodData {
        return data
    }

    @SuppressLint("SetTextI18n")
    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemMethodBinding).also { binding ->
            binding.textViewTitle.text = data.title
            binding.imageViewCheck.visibility = if (data.isSelected) {
                View.VISIBLE
            } else {
                View.GONE
            }
            if (data.isDescriptionOn) {
                binding.textViewDes.visibility = View.VISIBLE
                binding.textViewComma.visibility = View.GONE
                binding.textViewFajrAngle.visibility = View.GONE
                binding.textViewIshaAngle.visibility = View.GONE
                binding.textViewDes.text = data.description
            } else {
                binding.textViewDes.visibility = View.GONE
                binding.textViewComma.visibility = View.VISIBLE
                binding.textViewFajrAngle.visibility = View.VISIBLE
                binding.textViewIshaAngle.visibility = View.VISIBLE
                binding.textViewFajrAngle.text = "Fajr: ${data.fajrAngle}°"
                binding.textViewIshaAngle.text = "Isha:  ${data.ishaAngle}°"
            }

            binding.viewSeparator.visibility = if (data.isItemLast) View.GONE else View.VISIBLE
            binding.mainView.setOnClickListener {
                binding.imageViewCheck.visibility = View.VISIBLE
                listener.onClick(position)

            }

        }
    }

}

fun interface OnItemClick {
    fun onClick(position: Int)
}
