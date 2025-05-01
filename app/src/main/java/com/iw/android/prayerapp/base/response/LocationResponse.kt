package com.iw.android.prayerapp.base.response

import kotlinx.serialization.Serializable


@Serializable
data class LocationResponse(val timeZone: String,val locationName: String, val lat: Double = 0.0, val long: Double = 0.0)