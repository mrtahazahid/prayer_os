package com.iw.android.prayerapp.data.repositories


import com.iw.android.prayerapp.base.network.BaseApi
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.repo.BaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor( private val api: BaseApi,preferences: DataPreference) :
    BaseRepository(api, preferences) {

}