package com.iw.android.prayerapp.ui.main.moreFragment.itemView

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.ViewDataBinding
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MoreData
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
            binding.mainView.setOnClickListener {
               openCustomTab(binding.imageView.context,"https://www.instagram.com/praywatchapp/?hl=en")

            }
            if (data.title == "Read tutorial") {
                binding.imageViewDropDownMenu.visibility = View.VISIBLE
            } else {
                binding.imageViewDropDownMenu.visibility = View.GONE
            }


        }
    }

    fun openCustomTab(context: Context, url: String?) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}
