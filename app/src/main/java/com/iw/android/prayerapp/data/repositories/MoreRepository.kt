package com.iw.android.prayerapp.data.repositories

import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.repo.BaseRepository
import com.iw.android.prayerapp.data.response.MoreData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoreRepository @Inject constructor(preferences: DataPreference) :
    BaseRepository(preferences) {

    fun getStaticMoreList(): List<MoreData> {
        return listOf(
            MoreData(R.drawable.ic_home, "About this app"),
            MoreData(R.drawable.ic_rea, "Read tutorial"),
            MoreData(R.drawable.ic_sent, "Request support"),
            MoreData(R.drawable.ic_subscribe, "Subscribe for updates"),
            MoreData(R.drawable.ic_star, "Rate this app"),
            MoreData(R.drawable.ic_share, "Share this app"),
            MoreData(R.drawable.ic_mike, "Play adhan")
        )
    }
}
