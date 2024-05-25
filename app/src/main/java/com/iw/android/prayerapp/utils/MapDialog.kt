package com.iw.android.prayerapp.utils

import android.app.Activity.RESULT_CANCELED
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.maps.model.LatLng
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.adapter.GenericListAdapter
import com.iw.android.prayerapp.base.adapter.OnItemClickListener
import com.iw.android.prayerapp.base.adapter.ViewType
import com.iw.android.prayerapp.base.response.LocationResponse
import com.iw.android.prayerapp.databinding.LocationDialogBinding
import java.util.Locale

class MapDialog : DialogFragment(),
    GooglePlaceHelper.GooglePlaceDataInterface,
    View.OnClickListener, OnDataPass {
    private lateinit var binding: LocationDialogBinding

    private var googlePlaceHelper: GooglePlaceHelper? = null

    var recentLocationList = listOf<LocationResponse>()
    var listener: MapDialogListener? = null

    var latitude: Double? = null
    var longitude: Double? = null
    var city: String? = null

    private var viewTypeArray = ArrayList<ViewType<*>>()

    val adapter by lazy {
        GenericListAdapter(object : OnItemClickListener<ViewType<*>> {
            override fun onItemClicked(view: View, item: ViewType<*>, position: Int) {
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogFragmentStyle)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LocationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    private fun initialize() {
        binding.recyclerView.adapter = adapter
    }

    private fun setObserver() {
        viewTypeArray.clear()
        for (data in recentLocationList) {
            viewTypeArray.add(
                RowItemRecent(data, this)
            )
        }
        adapter.items = viewTypeArray
    }

    private fun setOnClickListener() {
        binding.searchTextView.setOnClickListener(this)
        binding.imageViewBack.setOnClickListener(this)
        binding.backView.setOnClickListener(this)
        binding.textViewDone.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.searchTextView.id -> {
                googlePlaceHelper =
                    GooglePlaceHelper(
                        requireActivity(),
                        this, this
                    )
                googlePlaceHelper?.openAutocompleteActivity()
            }

            binding.textViewDone.id -> {
                val location = GetAdhanDetails.getTimeZoneAndCity(
                    requireContext(),latitude?:0.0,
                    longitude?:0.0
                )
                sendDataBack(
                    LocationResponse(
                        location?.timeZone ?: "",
                        city ?: "",
                        latitude ?: 0.0,
                        longitude ?: 0.0
                    )
                )
            }

            binding.imageViewBack.id, binding.backView.id -> {
                dismiss()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != RESULT_CANCELED) {
            if (requestCode == GooglePlaceHelper.REQUEST_CODE_PLACE_HELPER) {
                super.onActivityResult(requestCode, resultCode, data)
                googlePlaceHelper?.onActivityResultAutoCompleted(requestCode, resultCode, data)
            }
        }

    }

    override fun onPlaceActivityResult(longitude: Double, latitude: Double, locationName: String?) {
        Log.d("CheckMap", "onPlaceActivityResult: $latitude")
        this.latitude = latitude // Assign latitude directly
        this.longitude = longitude // Assign longitude directly
        updateLocationResponse(LatLng(latitude, longitude))
        binding.textViewDone.visibility = View.VISIBLE


    }

    override fun onError(error: String?) {
        Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
    }


    private fun updateLocationResponse(latLng: LatLng) {
        // Fetch location name using reverse geocoding
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses?.isNotEmpty() == true) {

            val address = addresses[0]
            Log.d("address", address.toString())
            binding.searchTextView.text = address.locality + "," + address.countryName
            latitude = address.latitude
            longitude = address.longitude
            city = address.locality + "," + address.countryName
        }
    }


    private fun sendDataBack(data: LocationResponse) {
        listener?.onDataPassed(data)
        dismiss()
    }

    interface MapDialogListener {
        fun onDataPassed(data: LocationResponse)
    }

    override fun onItemClick(data: LocationResponse) {
        listener?.onDataPassed(data)
        dismiss()
    }
}