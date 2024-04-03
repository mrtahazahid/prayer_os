package com.iw.android.prayerapp.ui.main.moreFragment.itemView

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.Toast
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
            binding.view4.visibility = if (data.title == "Plan adhan") View.GONE else View.VISIBLE
            binding.imageView.setImageResource(data.image)
            binding.textViewTitle.text = data.title

            if (data.title == "Read tutorial") {
                binding.imageViewDropDownMenu.visibility = View.VISIBLE
            } else {
                binding.imageViewDropDownMenu.visibility = View.GONE
            }

            binding.mainView.setOnClickListener {
                when(data.title){
                    "About  this app" -> {openCustomTab(binding.imageView.context,"https://praywatch.app/")}
                    "Read tutorial" -> {Toast.makeText(binding.imageView.context, "Work in process", Toast.LENGTH_SHORT).show()}
                    "Request support" -> {Toast.makeText(binding.imageView.context, "Work in process", Toast.LENGTH_SHORT).show()}
                    "Subscribe for updates" -> {openCustomTab(binding.imageView.context,"https://praywatch.app/subscribe/")}
                    "Rate this app" -> {Toast.makeText(binding.imageView.context, "Work in process", Toast.LENGTH_SHORT).show()}
                    "Share this app" -> {Toast.makeText(binding.imageView.context, "Work in process", Toast.LENGTH_SHORT).show()}
                    "Plan adhan" -> {Toast.makeText(binding.imageView.context, "Work in process", Toast.LENGTH_SHORT).show()}
                }
            }
        }
    }

    fun openCustomTab(context: Context, url: String?) {
        val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
        val customTabsIntent: CustomTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}
