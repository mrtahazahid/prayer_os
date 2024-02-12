package com.iw.android.prayerapp.base.viewModel

import androidx.lifecycle.ViewModel
import com.iw.android.prayerapp.repo.BaseRepository


abstract class BaseViewModel(private val repository: BaseRepository) : ViewModel() {


    suspend fun getLoginUserId() = repository.getLoginUserId()

    suspend fun getUserInfoData() = repository.getLoginUserData()
    // suspend fun saveUserProfileData(data:LoginUserResponse) = repository.saveUserProfileData(data)
}