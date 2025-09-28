package com.iw.android.prayerapp.utils.asset



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.utils.GetAdhanSound
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    repository: MainRepository
) : BaseViewModel(repository) {

     val _pagedAssetList = MutableLiveData<List<SoundData>>(emptyList())
    val pagedAssetList: LiveData<List<SoundData>> get() = _pagedAssetList

    private var fullAssetList: List<SoundData> = emptyList()
    private var currentPage = 0
    private val pageSize = 15

    init {
        Log.d("init","called")
        viewModelScope.launch(Dispatchers.IO) {
            fullAssetList = GetAdhanSound().assetList
            loadNextPage() // okay to call, it only posts value
        }
    }

    fun loadNextPage() {
        val startIndex = currentPage * pageSize
        val endIndex = minOf(startIndex + pageSize, fullAssetList.size)

        if (startIndex < fullAssetList.size) {
            val nextPage = fullAssetList.subList(startIndex, endIndex)
            val currentList = _pagedAssetList.value ?: emptyList()
            // Post to LiveData from background thread safely
            _pagedAssetList.postValue(currentList + nextPage)
            currentPage++
        }
    }
}