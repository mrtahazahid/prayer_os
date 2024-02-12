package com.iw.android.prayerapp.repo

import com.google.gson.Gson
import com.iw.android.prayerapp.base.prefrence.DataPreference
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.IS_LOGIN
import com.iw.android.prayerapp.base.prefrence.DataPreference.Companion.USER_ID
import com.iw.android.prayerapp.base.response.LoginUserResponse
import com.iw.android.prayerapp.base.network.BaseApi
import com.iw.android.prayerapp.base.network.SafeApiCall
import kotlinx.coroutines.flow.first
import javax.inject.Singleton

@Singleton
abstract class BaseRepository(
    private val api: BaseApi,
    val preferences: DataPreference
) : SafeApiCall {


    suspend fun getLoginUserId(): String {
        return preferences.loginUserId.first()
    }

    suspend fun getLoginUserData(): LoginUserResponse? {
        return Gson().fromJson(
            preferences.getStringData(DataPreference.USER_INFO),
            LoginUserResponse::class.java
        )
    }

    suspend fun getPrivacyContentApi(type: String) = safeApiCall {
        api.getPrivacyContent(type)
    }


    suspend fun saveUserProfileData(userInfo: LoginUserResponse) {
        preferences.setStringData(DataPreference.USER_INFO, Gson().toJson(userInfo))
    }


    suspend fun logout() = safeApiCall {
        api.logout()
    }


    suspend fun saveLoginUserId(userId: String) {
        preferences.setStringData(USER_ID, userId)
    }

    suspend fun saveAccessToken(accessToken: String) {
        preferences.saveAccessTokens(accessToken)
    }

    suspend fun setUserLoggedIn() {
        preferences.setBooleanData(IS_LOGIN, true)
    }

    suspend fun clearUserPreferenceData() {
        preferences.performLogout()
    }

    suspend fun clearUserLoginData() {
        preferences.clearLoginDataForUpdate()
    }

//    suspend fun getDeviceToken(): String {
//        return preferences.deviceToken.first()
//    }

}