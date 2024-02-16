package com.iw.android.prayerapp.ui.main.prayerFragment

import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.UserLatLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrayerViewModel @Inject constructor(private val repository: MainRepository) :
    BaseViewModel(repository) {

         var userLatLong:UserLatLong?=null

        init {
            viewModelScope.launch {
                userLatLong = getUserLatLong()
            }
        }

}