package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.method.getMadhab
import com.iw.android.prayerapp.utils.method.getMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor( repository: MainRepository) :
    BaseViewModel(repository) {

    var selectedPrayerDate = Date()
    var userLatLong: UserLatLong? = null
    var selectedJurisprudenceFromDB =""
    lateinit var method: CalculationParameters
    lateinit  var madhab: Madhab

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            val selectedMethodFromDB = getPrayerMethod()
            selectedJurisprudenceFromDB = getPrayerJurisprudence()
            madhab = getMadhab(selectedJurisprudenceFromDB)
            method = getMethod(
                selectedMethod = selectedMethodFromDB,
                selectedJurisprudence = selectedJurisprudenceFromDB
            )
        }

    }

    fun getPrayerTime(lat: Double, long: Double): ArrayList<String> {
        return GetAdhanDetails.getPrayTime(
            lat,
            long,
            method,
            selectedPrayerDate
        )

    }

}