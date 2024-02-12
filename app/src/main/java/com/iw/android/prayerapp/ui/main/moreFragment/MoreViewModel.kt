package com.iw.android.prayerapp.ui.main.moreFragment

import androidx.lifecycle.ViewModel
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.data.response.MoreData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoreViewModel@Inject constructor()  : ViewModel() {
    var moreList = arrayListOf<MoreData>()

    init {
        moreList.add(
            MoreData(
                R.drawable.ic_home,
                "About  this app"
            )
        )

        moreList.add(
            MoreData(
                R.drawable.ic_rea,
                "Read tutorial"
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_sent,
                "Request support",
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_subscribe,
                "Subscribe for updates",
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_star,
                "Rate this app",
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_share,
                "Share this app",
            )
        )

        moreList.add(
            MoreData(
                R.drawable.ic_mike,
                "plan azan",

            )
        )

    }


}