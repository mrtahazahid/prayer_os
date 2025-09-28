package com.iw.android.prayerapp.utils.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.iw.android.prayerapp.base.response.LocationResponse

fun openLocationDialog(
    data: List<LocationResponse>,
    activity: FragmentActivity,
    listener: MapDialog.MapDialogListener?
) {
    val locationDialog = MapDialog()
    locationDialog.listener = listener
    locationDialog.recentLocationList = data
    locationDialog.show(activity.supportFragmentManager, "SoundDialogFragment")
}

@SuppressLint("QueryPermissionsNeeded")
fun openGoogleMapsNearbyPlaces(
    latitude: Double,
    longitude: Double,
    activity: FragmentActivity
) {
    val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=mosque")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }

    if (mapIntent.resolveActivity(activity.packageManager) != null) {
        activity.startActivity(mapIntent) // Use the passed activity
    }
}