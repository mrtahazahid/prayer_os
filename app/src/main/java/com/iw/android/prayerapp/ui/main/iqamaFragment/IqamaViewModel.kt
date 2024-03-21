package com.iw.android.prayerapp.ui.main.iqamaFragment


import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.IqamaData
import com.iw.android.prayerapp.extension.convertToFunTime
import com.iw.android.prayerapp.ui.main.timeFragment.DuaTypeEnum
import com.iw.android.prayerapp.utils.GetAdhanDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IqamaViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {

    var iqamaList = arrayListOf<IqamaData>()
    var lat = 0.0
    var long = 0.0
    private var method: CalculationParameters? = null
    private var madhab: Madhab? = null
    var getSavedPrayerJurisprudence = ""
    var getMethod = ""

    init {
        viewModelScope.launch {
            getSavedPrayerJurisprudence = getPrayerJurisprudence()
            getMethod = getPrayerMethod()
            getMethod1()
        }
        addList()
    }

     fun addList() = viewModelScope.launch {
        lat = repository.getUserLatLong()?.latitude ?: 0.0
        long = repository.getUserLatLong()?.longitude ?: 0.0
        val getPrayerTime = GetAdhanDetails.getPrayTimeInLong(lat, long, method!!)
        iqamaList.add(
            IqamaData(
                "Fajr", convertToFunTime(getPrayerTime.fajr.toEpochMilliseconds()),
                getIqamaFajrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaFajrDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Dhuhr", convertToFunTime(getPrayerTime.dhuhr.toEpochMilliseconds()),
                getIqamaDhuhrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaDhuhrDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Asr", convertToFunTime(getPrayerTime.asr.toEpochMilliseconds()),
                getIqamaAsrDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaAsrDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Maghrib", convertToFunTime(getPrayerTime.maghrib.toEpochMilliseconds()),
                getIqamaMaghribDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaMaghribDetail()?.iqamaTime
            )
        )
        iqamaList.add(
            IqamaData(
                "Isha", convertToFunTime(getPrayerTime.isha.toEpochMilliseconds()),
                getIqamaIshaDetail()?.iqamaType ?: DuaTypeEnum.OFF.getValue(),
                getIqamaIshaDetail()?.iqamaTime
            )
        )

    }

    suspend fun getMethod1(){
        if (!getSavedPrayerJurisprudence.isNullOrEmpty()) {
            madhab = if (getSavedPrayerJurisprudence.toInt() == 1) {
                Madhab.HANAFI
            } else {
                Madhab.SHAFI
            }
        }

        if (!getMethod.isNullOrEmpty()) {
            method = when (getMethod.toInt()) {
                0 -> {
                    CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                1 -> {
                    CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                2 -> {
                    CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters.copy(
                        madhab = madhab ?: Madhab.SHAFI
                    )
                }

                3 -> {
                    CalculationMethod.EGYPTIAN.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                4 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                5 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                6 -> {
                    CalculationMethod.UMM_AL_QURA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                8 -> {
                    CalculationMethod.UMM_AL_QURA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                9 -> {
                    CalculationMethod.DUBAI.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                10 -> {
                    CalculationMethod.KUWAIT.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                11 -> {
                    CalculationMethod.SINGAPORE.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                12 -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                13 -> {
                    CalculationMethod.QATAR.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                14 -> {
                    CalculationMethod.KARACHI.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }

                else -> {
                    CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
                }
            }
        }else{
            CalculationMethod.NORTH_AMERICA.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
        }
    }
}