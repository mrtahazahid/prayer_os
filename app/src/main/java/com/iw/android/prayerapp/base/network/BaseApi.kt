package com.iw.android.prayerapp.base.network

import com.iw.android.prayerapp.base.response.BaseNetworkResponse
import com.iw.android.prayerapp.data.response.ContentData
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface BaseApi {
    @POST("/api/logout")
    suspend fun logout(): BaseNetworkResponse<Any>

    @GET("content")
    suspend fun getPrivacyContent(
        @Query("type") type: String
    ): BaseNetworkResponse<ContentData>
}