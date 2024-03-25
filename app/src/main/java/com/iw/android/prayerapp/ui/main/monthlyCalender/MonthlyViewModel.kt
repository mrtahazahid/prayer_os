package com.iw.android.prayerapp.ui.main.monthlyCalender

import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.MonthlyCalenderData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MonthlyViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

        var monthlyList = arrayListOf<MonthlyCalenderData>()

    init {
        viewModelScope.launch {
            for(data in 1..365){
                    monthlyList.add(MonthlyCalenderData("1","mon","4:25","6:54","12:46","5:30","6:45","8:15","1"))
            }
        }
    }
}