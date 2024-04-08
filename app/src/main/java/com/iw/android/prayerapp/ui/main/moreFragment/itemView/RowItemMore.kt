package com.iw.android.prayerapp.ui.main.moreFragment.itemView

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
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

class RowItemMore(private val data: MoreData,private val activity: Activity) : ViewType<MoreData> {

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
                    "Request support" -> sendUserToGmail()
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

    fun sendUserToGmail() {
        try {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf("prayerApp@gmail.com")
                intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Feedback For " + activity.getString(R.string.app_name)
                )
                intent.putExtra(
                    Intent.EXTRA_TEXT, """
     Dear Development Team
     Greetings,
     
     """.trimIndent()
                )
                intent.type = "text/html"
                intent.setPackage("com.google.android.gm")
                activity.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
            }
        } catch (ignored: Exception) {
        }
    }

}
