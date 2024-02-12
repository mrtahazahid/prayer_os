package com.iw.android.prayerapp.base.response

import okhttp3.ResponseBody

sealed class Resource<out T> {
    data class Success<out T>(val value: T) : Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?,
        val message:String?
    ) : Resource<Nothing>()
    object InvalidTokenSessionClose : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

