package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundViewModel @Inject constructor( repository: MainRepository) :
    BaseViewModel(repository) {


}