package com.iw.android.prayerapp.ui.main.notificationList.itemView

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.NotificationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationListViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

        var notificationList = arrayListOf<NotificationData>()

    init {
        viewModelScope.launch {
            Log.d("size",getAllNotificationData().size.toString())
            for(data in getAllNotificationData()){
                if (data != null) {
                    notificationList.add(data)
                }
            }
        }
    }
}