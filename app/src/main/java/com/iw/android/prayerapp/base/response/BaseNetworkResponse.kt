package com.iw.android.prayerapp.base.response

data class BaseNetworkResponse<T>(
    val status: Int,
    val message: String,
    val bearer_token: String? = null,
    val data: T?
)