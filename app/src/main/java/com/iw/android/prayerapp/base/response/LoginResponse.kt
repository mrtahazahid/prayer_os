package com.iw.android.prayerapp.base.response

import kotlinx.serialization.Serializable

data class LoginUserData(
    val id: String,
    val name: String,
    var userImage: String,
    val accessToken: String,
    val refreshToken: String,
    val email: String,
)
@Serializable
data class LocationResponse(val timeZone: String,val locationName: String, val lat: Double = 0.0, val long: Double = 0.0)

data class LoginUserResponse(

    val id: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val profile_image: String,
    val phone_number: String,
    val date_of_birth: String,
    val about: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val state: String,
    val city: String,
    val push_notification: Int,
    val is_profile_complete: Int,
    val is_forgot: Int,
    val is_verified: Int,
    val is_active: Int,
    val is_blocked: Int,
    val is_social: Int

)