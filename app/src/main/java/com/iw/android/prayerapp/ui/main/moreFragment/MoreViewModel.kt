package com.iw.android.prayerapp.ui.main.moreFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Madhab
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.viewModel.BaseViewModel
import com.iw.android.prayerapp.data.repositories.MainRepository
import com.iw.android.prayerapp.data.response.MoreData
import com.iw.android.prayerapp.data.response.UserLatLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(repository: MainRepository) :
    BaseViewModel(repository) {
    var moreList = arrayListOf<MoreData>()
    var userLatLong: UserLatLong? = null
     var method: CalculationParameters? = null
     var methodInt: Int? = null
     var madhab: Madhab? = null
     var madhabInt: Int? = null

    init {
        viewModelScope.launch {
            userLatLong = getUserLatLong()
            if (getPrayerJurisprudence().isNullOrEmpty()) {
                madhab = if (getPrayerJurisprudence().toInt() == 1) {
                    Madhab.HANAFI
                } else {
                    Madhab.SHAFI
                }
            }
            methodInt = if (!getPrayerMethod().isNullOrEmpty()) {
                getPrayerMethod().toInt()
            } else {
                0
            }


            madhabInt = if (getPrayerJurisprudence().isNullOrEmpty()) {
                if (getPrayerJurisprudence().toInt() == 1) {
                    1
                } else {
                    0
                }
            } else {
                0
            }
            if (!getPrayerMethod().isNullOrEmpty()) {
                method = when (getPrayerMethod().toInt()) {
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
            } else {
                method = CalculationMethod.OTHER.parameters.copy(madhab = madhab ?: Madhab.SHAFI)
            }

        }
        moreList.add(
            MoreData(
                R.drawable.ic_home,
                "About  this app"
            )
        )

        moreList.add(
            MoreData(
                R.drawable.ic_rea,
                "Read tutorial"
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_sent,
                "Request support",
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_subscribe,
                "Subscribe for updates",
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_star,
                "Rate this app",
            )
        )
        moreList.add(
            MoreData(
                R.drawable.ic_share,
                "Share this app",
            )
        )

        moreList.add(
            MoreData(
                R.drawable.ic_mike,
                "Plan adhan",

                )
        )

    }


}