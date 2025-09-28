package com.iw.android.prayerapp.ui.main.timeFragment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.response.LocationResponse
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.CurrentNamazNotificationData
import com.iw.android.prayerapp.data.response.LocationData
import com.iw.android.prayerapp.data.response.NotificationData
import com.iw.android.prayerapp.data.response.PrayTime
import com.iw.android.prayerapp.data.response.PrayerTime
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.utils.GetAdhanDetails
import com.iw.android.prayerapp.utils.dateFormat.convertAndGetCurrentTimeMillis
import com.iw.android.prayerapp.utils.dateFormat.convertTimeToMillis
import com.iw.android.prayerapp.utils.dateFormat.formatDateWithCurrentTime
import com.iw.android.prayerapp.utils.dateFormat.getCurrentDate
import com.iw.android.prayerapp.utils.method.getMadhab
import com.iw.android.prayerapp.utils.method.getMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {
    var selectedPrayerDate = Date()
    var selectedJurisprudenceFromDB = ""
    var selectedMethodFromDB = ""

    var recentLocationList: List<LocationResponse> = emptyList()

    private val _prayTimeArray = MutableLiveData<ArrayList<PrayTime>>()
    val prayTimeArray: LiveData<ArrayList<PrayTime>> get() = _prayTimeArray

    private val prayerNames =
        listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha", "Midnight", "Last Third")

   private val _userLocation = MutableLiveData<UserLatLong?>()
    val userLocation: LiveData<UserLatLong?> get() = _userLocation
    var isLoading = MutableLiveData<Boolean>()

    private val _location = MutableLiveData<LocationData?>()
    val location: LiveData<LocationData?> = _location

    private lateinit var method: CalculationParameters
    private lateinit var madhab: Madhab

    init {
        isLoading.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            val userLatLong = getUserLatLong()
            _userLocation.postValue(userLatLong)
            getRecentLocationDataFromDB()
             selectedMethodFromDB = getPrayerMethod()
            selectedJurisprudenceFromDB = getPrayerJurisprudence()
            madhab = getMadhab(selectedJurisprudenceFromDB)
            method = getMethod(
                selectedMethod = selectedMethodFromDB,
                selectedJurisprudence = selectedJurisprudenceFromDB
            )
            Log.d("pray method",method.toString())
            getPrayList(userLatLong?.latitude ?: 0.0, userLatLong?.longitude ?: 0.0)
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

    fun fetchTimeZoneAndCity(context: Context,lat:Double,long:Double) {
        viewModelScope.launch {
            val locationData = withContext(Dispatchers.IO) {
                GetAdhanDetails.getTimeZoneAndCity(
                    context,
                    lat,
                    long
                )
            }
            _location.postValue(locationData)
        }
    }

    fun clearPrayerTimes() {
        _prayTimeArray.postValue(arrayListOf())
    }

    suspend fun getRecentLocationDataFromDB() {
        recentLocationList = emptyList()
        recentLocationList = getRecentLocationData()
    }

    suspend fun getPrayList(lat: Double, long: Double) {
        val getPrayerTime = getPrayerTime(lat, long)
        val prayTimeArrayList = arrayListOf<PrayTime>()

        val icons = listOf(
            R.drawable.ic_mike,
            R.drawable.ic_speaker_zzz,
            R.drawable.ic_mike,
            R.drawable.ic_mike,
            R.drawable.ic_mike,
            R.drawable.ic_mike,
            R.drawable.ic_notification_mute,
            R.drawable.ic_notification_mute
        )

        val namazDetail = listOf(
            getFajrDetail(),
            getSunriseDetail(),
            getDuhrDetail(),
            getAsrDetail(),
            getMagribDetail(),
            getIshaDetail(),
            getMidNightDetail(),
            getLastNightDetail()
        )


        prayerNames.forEachIndexed { index, name ->
            prayTimeArrayList.add(
                PrayTime(
                    icons[index],
                    name,
                    getPrayerTime[index],
                    formatDateWithCurrentTime(selectedPrayerDate),
                    namazDetail[index] ?: NotificationData()
                )
            )
        }

        // Get the upcoming namaz using getTimeDifferenceToNextPrayer function
        val upcomingNamaz = getTimeDifferenceToNextPrayer(lat, long)

        // Iterate through the prayTimeArrayList and set isCurrentNamaz accordingly
        for (prayTime in prayTimeArrayList) {
            prayTime.isCurrentNamaz = prayTime.title == upcomingNamaz.currentNamazName
        }
        _prayTimeArray.postValue(prayTimeArrayList)
        isLoading.postValue(false)
    }


    private fun getTimeDifferenceToNextPrayer(lat: Double, long: Double): PrayerTime {

        val namazTime = getPrayerTime(lat, long)
        val prayerTimeList = arrayListOf<PrayerTime>()
        prayerNames.forEachIndexed { index, name ->
            prayerTimeList.add(
                PrayerTime(
                    currentNamazName = name,
                    currentNamazTime = convertTimeToMillis(namazTime[index])
                )
            )
        }

        val currentTimeMillis = convertAndGetCurrentTimeMillis()


        // Iterate through the array to find the next prayer time
        var nextPrayerTimeIndex = 0
        var currentPrayerTimeIndex = 0
        var previousPrayerTimeIndex = 0
        for (i in prayerTimeList.indices) {
            if (prayerTimeList[i].currentNamazTime > currentTimeMillis) {
                when (prayerTimeList[i].currentNamazName) {
                    "Fajr" -> {
                        previousPrayerTimeIndex = prayerTimeList.size - 1
                        currentPrayerTimeIndex = i
                        nextPrayerTimeIndex = i + 1
                    }

                    "Isha" -> {
                        previousPrayerTimeIndex = i - 1
                        currentPrayerTimeIndex = i
                        nextPrayerTimeIndex = 0
                    }

                    else -> {
                        previousPrayerTimeIndex = i - 1
                        currentPrayerTimeIndex = i
                        nextPrayerTimeIndex = i + 1
                    }
                }
                break
            } else {
                continue
            }
        }

        // Calculate the time difference between the current time and the next prayer time
        val timeDifferenceMillis =
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime - currentTimeMillis


        // Calculate the total time difference between the previous and up-coming prayer
        val totalDifferenceMillis =
            if (prayerTimeList[currentPrayerTimeIndex].currentNamazName == "Fajr") {
                prayerTimeList[previousPrayerTimeIndex].currentNamazTime - prayerTimeList[nextPrayerTimeIndex].currentNamazTime
            } else {
                prayerTimeList[nextPrayerTimeIndex].currentNamazTime - prayerTimeList[previousPrayerTimeIndex].currentNamazTime
            }
        // Return the PrayerTime object with time differences
        return PrayerTime(
            prayerTimeList[currentPrayerTimeIndex].currentNamazName,
            prayerTimeList[currentPrayerTimeIndex].currentNamazTime,
            timeDifferenceMillis,
            totalDifferenceMillis
        )
    }

     fun savePrayerDetailData(prayerDetailData:NotificationData?,namazName:String,namazTime:String) {
         Log.d("prayerDetailData",prayerDetailData.toString())
         Log.d("namazName",namazName.toString())
         Log.d("namazTime",namazTime.toString())
        val fajrData = NotificationData(
            namazName = namazName,
            namazTime = namazTime,
            notificationSound = CurrentNamazNotificationData(
                prayerDetailData?.notificationSound?.currentNamazName ?: "",
                prayerDetailData?.notificationSound?.soundName ?: "",
                prayerDetailData?.notificationSound?.soundToneName ?: "",
                prayerDetailData?.notificationSound?.selectedSoundPosition,
                prayerDetailData?.notificationSound?.selectedSoundTonePosition,
                prayerDetailData?.notificationSound?.selectedSoundItemPosition,
                prayerDetailData?.notificationSound?.isSoundSelected ?: false,
                prayerDetailData?.notificationSound?.isForAdhan ?: false,
                prayerDetailData?.notificationSound?.isVibrate ?: false,
                prayerDetailData?.notificationSound?.isSilent ?: false,
                prayerDetailData?.notificationSound?.isOff ?: false,
                prayerDetailData?.notificationSound?.soundAdhan,
                prayerDetailData?.notificationSound?.soundTone
            ),
            reminderSound = prayerDetailData?.reminderSound,
            reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes ?: "off",
            reminderTime = prayerDetailData?.reminderTime ?: "",
            secondReminderTimeMinutes = prayerDetailData?.secondReminderTimeMinutes ?: "off",
            secondReminderTime = prayerDetailData?.secondReminderTime ?: "",
            duaReminderMinutes = "off",
            duaTime = "",
            duaType = "off",
            createdDate = getCurrentDate(),
        )

        val sunriseData = NotificationData(
            namazName = namazName,
            namazTime = namazTime,
            notificationSound = CurrentNamazNotificationData(
                prayerDetailData?.notificationSound?.currentNamazName ?: "",
                prayerDetailData?.notificationSound?.soundName ?: "",
                prayerDetailData?.notificationSound?.soundToneName ?: "",
                prayerDetailData?.notificationSound?.selectedSoundPosition,
                prayerDetailData?.notificationSound?.selectedSoundTonePosition,
                prayerDetailData?.notificationSound?.selectedSoundItemPosition,
                prayerDetailData?.notificationSound?.isSoundSelected ?: false,
                prayerDetailData?.notificationSound?.isForAdhan ?: false,
                prayerDetailData?.notificationSound?.isVibrate ?: false,
                prayerDetailData?.notificationSound?.isSilent ?: false,
                prayerDetailData?.notificationSound?.isOff ?: false,
                prayerDetailData?.notificationSound?.soundAdhan,
                prayerDetailData?.notificationSound?.soundTone
            ),
            reminderSound = prayerDetailData?.reminderSound,
            reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes ?: "off",
            reminderTime = prayerDetailData?.reminderTime ?: "",
            secondReminderTimeMinutes = "off",
            secondReminderTime = "off",
            duaReminderMinutes = prayerDetailData?.duaReminderMinutes ?: "off",
            duaTime = prayerDetailData?.duaTime ?: "",
            duaType = prayerDetailData?.duaType ?: "off",
            createdDate = getCurrentDate(),
        )

        val saveData = NotificationData(
            namazName = namazName,
            namazTime = namazTime,
            notificationSound = CurrentNamazNotificationData(
                prayerDetailData?.notificationSound?.currentNamazName ?: "",
                prayerDetailData?.notificationSound?.soundName ?: "",
                prayerDetailData?.notificationSound?.soundToneName ?: "",
                prayerDetailData?.notificationSound?.selectedSoundPosition,
                prayerDetailData?.notificationSound?.selectedSoundTonePosition,
                prayerDetailData?.notificationSound?.selectedSoundItemPosition,
                prayerDetailData?.notificationSound?.isSoundSelected ?: false,
                prayerDetailData?.notificationSound?.isForAdhan ?: false,
                prayerDetailData?.notificationSound?.isVibrate ?: false,
                prayerDetailData?.notificationSound?.isSilent ?: false,
                prayerDetailData?.notificationSound?.isOff ?: false,
                prayerDetailData?.notificationSound?.soundAdhan,
                prayerDetailData?.notificationSound?.soundTone
            ),
            reminderSound = prayerDetailData?.reminderSound,
            reminderTimeMinutes = prayerDetailData?.reminderTimeMinutes ?: "off",
            reminderTime = prayerDetailData?.reminderTime ?: "",
            secondReminderTimeMinutes = "off",
            secondReminderTime = "",
            duaReminderMinutes = "off",
            duaTime = "",
            duaType = "off",
            createdDate = getCurrentDate(),
        )

        when (namazName) {
            "Fajr" -> saveFajrDetail(fajrData)

            "Sunrise" -> saveSunriseDetail(sunriseData)

            "Dhuhr" -> saveDuhrDetail(saveData)

            "Asr" -> saveAsrDetail(saveData)

            "Maghrib" -> saveMagribDetail(saveData)

            "Isha" -> saveIshaDetail(saveData)

            "Midnight" -> saveMidNightDetail(saveData)

            "Last Third" -> saveLastNightDetail(saveData)
        }

    }
}