package com.iw.android.prayerapp.base.viewModel

import androidx.lifecycle.ViewModel
import com.iw.android.prayerapp.base.repo.BaseRepository
import com.iw.android.prayerapp.data.response.UserLatLong


abstract class BaseViewModel(private val repository: BaseRepository) : ViewModel() {


    suspend fun saveLoginUserId(userLoginId:String) = repository.saveLoginUserId(userLoginId)
    suspend fun getLoginUserId() = repository.getLoginUserId()

    suspend fun savePrayerMethod(prayerMethod:String) = repository.savePrayerMethod(prayerMethod)
    suspend fun getPrayerMethod() = repository.getPrayerMethod()
    suspend fun savePrayerElevation(prayerElevation:String) = repository.savePrayerElevationRule(prayerElevation)
    suspend fun getPrayerElevation() = repository.getPrayerElevation()
    suspend fun savePrayerJurisprudence(prayerJurisprudence:String) = repository.savePrayerJurisprudence(prayerJurisprudence)
    suspend fun getPrayerJurisprudence() = repository.getPrayerJurisprudence()

    suspend fun getUserInfoData() = repository.getLoginUserData()

    suspend fun saveUserLatLong(userLatLong:UserLatLong) {  return repository.saveUserLatLong(userLatLong) }
    suspend fun saveIsOnBoarding() {   repository.setIsOnboarding() }
    suspend fun getUserLatLong(): UserLatLong? {  return repository.getUserLatLong() }
}