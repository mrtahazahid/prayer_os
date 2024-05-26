package com.iw.android.prayerapp.utils


import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode


class GooglePlaceHelper(
    private val activity: Activity,
    private val fragment: Fragment,
    private val googlePlaceDataInterface: GooglePlaceDataInterface,
) {

    fun openAutocompleteActivity() {
        // The autocomplete activity requires Google Play Services to be available. The intent
        // builder checks this and throws an exception if it is not the case.
        val intent: Intent
        if (!Places.isInitialized()) {
            Places.initialize(activity, GEO_API_KEY)
        }

        // Set the fields to specify which types of place data to return.
        val fields: List<Place.Field> =
            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Start the autocomplete intent.
        val autocompleteActivityMode: AutocompleteActivityMode = AutocompleteActivityMode.OVERLAY
        intent = Autocomplete.IntentBuilder(autocompleteActivityMode, fields).build(activity)
        fragment.startActivityForResult(intent, REQUEST_CODE_PLACE_HELPER)
    }

    fun onActivityResultAutoCompleted(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PLACE_HELPER && data != null) {
            when (resultCode) {
                AutocompleteActivity.RESULT_ERROR -> {
                    Log.d(
                        "CheckMap",
                        "onActivityResultAutoCompleted: resultCode = ${resultCode}, data = $data"
                    )
                    // TODO: Handle the error.
                    val status: Status = Autocomplete.getStatusFromIntent(data)
                    Log.e("CheckMap", "Error: Status = $status")
                    googlePlaceDataInterface.onError(status.toString())
                }

                Activity.RESULT_CANCELED -> {
                    Log.d(
                        "CheckMap",
                        "onActivityResultAutoCompleted:  resultCode = 0, data = $data"
                    )
                    // Indicates that the activity closed before a selection was made. For example if
                    // the user pressed the back button.
                }

                else -> {
                    Log.d("CheckMap", "Else:  resultCode = ${resultCode}, data = $data")
                    val place: Place = Autocomplete.getPlaceFromIntent(data) as Place
                    val locationName: String = place.name?.toString() ?: ""
                    val latitude: Double = place.latLng?.latitude ?: 0.0
                    val longitude: Double = place.latLng?.longitude ?: 0.0
                    Log.d(
                        TAG,
                        "onActivityResult MAP: locationName = $locationName"
                    )
                    Log.d(
                        TAG,
                        "onActivityResult MAP: latitude = $latitude"
                    )
                    Log.d(
                        TAG,
                        "onActivityResult MAP: longitude = $longitude"
                    )
                    googlePlaceDataInterface.onPlaceActivityResult(
                        longitude,
                        latitude,
                        locationName
                    )
                }
            }
        }
    }

    interface GooglePlaceDataInterface {
        fun onPlaceActivityResult(longitude: Double, latitude: Double, locationName: String?)
        fun onError(error: String?)
    }

    companion object {
        const val REQUEST_CODE_PLACE_HELPER = 6666
        const val GEO_API_KEY = "AIzaSyBmaS0B0qwokES4a_CiFNVkVJGkimXkNsk"
        private const val TAG = "Google Place"
        const val PLACE_PICKER = 0
    }
}
