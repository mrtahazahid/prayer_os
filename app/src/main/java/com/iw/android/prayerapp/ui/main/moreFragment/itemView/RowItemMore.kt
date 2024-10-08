package com.iw.android.prayerapp.ui.main.moreFragment.itemView

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.databinding.ViewDataBinding
import com.batoulapps.adhan2.CalculationParameters
import com.iw.android.prayerapp.BuildConfig
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.data.response.MoreData
import com.iw.android.prayerapp.databinding.RowItemMoreBinding
import com.iw.android.prayerapp.extension.convertToFunDateTime
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.extension.getAndroidVersion
import com.iw.android.prayerapp.extension.getDeviceName
import com.iw.android.prayerapp.ui.activities.onBoarding.OnBoardingActivity
import com.iw.android.prayerapp.utils.GetAdhanDetails
import java.util.Locale

class RowItemMore(
    private val data: MoreData,
    private val activity: Activity,
    private val lat: Double,
    private val long: Double,
    private val method: CalculationParameters,
    private val methodInt: Int,
    private val madhab: Int,
    private val context: Context,
    private val listener: OnClickPlayAdhan
) : ViewType<MoreData> {

    private var mediaPlayer: MediaPlayer? = null
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
                when (data.title) {
                    "About  this app" -> {
                        openCustomTab(binding.imageView.context, "https://praywatch.app/")
                    }

                    "Read tutorial" -> {
                        val intent = Intent(
                            binding.imageView.context,
                            OnBoardingActivity::class.java
                        )
                        intent.putExtra("data","value")
                        activity.startActivity(
                         intent

                        )
                    }

                    "Request support" -> sendUserToGmail()
                    "Subscribe for updates" -> {
                        openCustomTab(binding.imageView.context, "https://praywatch.app/subscribe/")
                    }

                    "Rate this app" -> {
                        Toast.makeText(
                            binding.imageView.context,
                            "Coming Soon",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    "Share this app" -> {
                        Toast.makeText(
                            binding.imageView.context,
                            "Coming Soon",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    "Play adhan" -> {
                        if (!isPlayAdhanChecked) {
                            isPlayAdhanChecked = true
                            binding.imageView.setImageResource(R.drawable.ic_mute_mike)
                            binding.textViewTitle.text = "Stop adhan"
                        } else {
                            isPlayAdhanChecked = false
                            binding.imageView.setImageResource(R.drawable.ic_mike)
                            binding.textViewTitle.text = "Play adhan"
                        }
                        listener.onClick(isPlayAdhanChecked)
                    }
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
        val location = GetAdhanDetails.getTimeZoneAndCity(
            context, lat,
            long
        )
        val methodsList = ArrayList<String>().apply {
            add("Muslim World League")
            add("Islamic Society of N.America (ISNA)")
            add("Moonsighting Committee")
            add("Egyptian General Authority of Survey")
            add("Algerian Ministry of Awqaf and Religious Affairs")
            add("Tunisian Ministry of Religious Affairs")
            add("London Unified Prayer Timetable")
            add("Umm Al-Quran University")
            add("Umm Al-Qura University, Makkah")
            add("Authority of Dubai - UAE")
            add("Kuwait")
            add("Singapore")
            add("Other")
            add("Authority of Qatar")
            add("Karachi")
        }
        val juriList = arrayListOf<String>().apply {
            add("Standard")
            add("Hanafi")
        }
        val getPrayerTime =
            GetAdhanDetails.getPrayTimeInLong(lat, long, method)
        try {
            try {
                val intent = Intent(Intent.ACTION_SEND)
                val recipients = arrayOf("help@praywatch.app")
                intent.putExtra(Intent.EXTRA_EMAIL, recipients)
                intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Feedback For " + activity.getString(R.string.app_name)
                )
                intent.putExtra(
                    Intent.EXTRA_TEXT,
                    """Assalamu Alaikum, please specify your question, feature request, or bug report:




===================
Version: ${BuildConfig.VERSION_NAME}

-------------------
Phone Information
-------------------
Device: ${getDeviceName()}
Version: Android ${getAndroidVersion()}
Background: true

-------------------
Notifications
-------------------
Status: authorized
Pending: 49
Tray: 2

-------------------
Prayer Times
-------------------
Fajr: ${convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds())}
Sunrise: ${convertToFunTime(getPrayerTime.sunrise.toEpochMilliseconds())}
Dhuhr: ${convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds())}
Asr: ${convertToFunTime(getPrayerTime.asr.toEpochMilliseconds())}
Maghrib: ${convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds())}
Isha: ${convertToFunTime(getPrayerTime.isha.toEpochMilliseconds())}

-------------------
Prayer Parameters
-------------------
Method: ${methodsList[methodInt]}
Jurisprudence: ${juriList[madhab]}
Auto GPS: true
Time zone: ${location?.timeZone}
Locale: ${Locale.getDefault()}
Region: ${location?.timeZone?.substringAfterLast("/")}
Hijri offset: 0
Adjustments: none
Coordinates: true
Cache: ${convertToFunDateTime(getCacheDirectoryLastModified(context))}
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

    fun getCacheDirectoryLastModified(context: Context): Long {
        val cacheDir = context.cacheDir
        return cacheDir.lastModified()
    }


}

fun interface OnClickPlayAdhan {
    fun onClick(isChecked: Boolean)
}