package com.iw.android.prayerapp.utils

import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.response.LocationResponse
import com.iw.android.prayerapp.databinding.RowItemRecentSearchBinding

class RowItemRecent(private val data: LocationResponse, private val listener: OnDataPass) :
    ViewType<LocationResponse> {

    override fun layoutId(): Int {
        return R.layout.row_item_recent_search
    }

    override fun data(): LocationResponse {
        return data
    }

    override fun bind(bi: ViewDataBinding, position: Int, onClickListener: OnItemClickListener<*>) {
        (bi as RowItemRecentSearchBinding).also { binding ->
            binding.textViewTitle.text = data.locationName
            binding.root.setOnClickListener {
                listener.onItemClick(data)
            }

        }

    }
}

fun interface OnDataPass {
    fun onItemClick(data: LocationResponse)
}
