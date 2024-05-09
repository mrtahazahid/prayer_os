package com.iw.android.prayerapp.ui.main.qibla

import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.UserLatLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QiblaViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {
    var getUserLatLong: UserLatLong? = null

    init {
        viewModelScope.launch {
            getUserLatLong = getUserLatLong()
        }
    }

}