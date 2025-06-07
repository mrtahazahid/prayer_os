package com.iw.android.prayerapp.utils.method

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters


fun getMethod(selectedJurisprudence: String, selectedMethod: String): CalculationParameters {

    val madhab = getMadhab(selectedJurisprudence)

    return when (selectedMethod.toIntOrNull()) {
        0 -> CalculationMethod.NORTH_AMERICA.parameters
        1 -> CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        2 -> CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
        3 -> CalculationMethod.EGYPTIAN.parameters
        4, 5, 12 -> CalculationMethod.OTHER.parameters
        6, 8 -> CalculationMethod.UMM_AL_QURA.parameters
        9 -> CalculationMethod.DUBAI.parameters
        10 -> CalculationMethod.KUWAIT.parameters
        11 -> CalculationMethod.SINGAPORE.parameters
        13 -> CalculationMethod.QATAR.parameters
        14 -> CalculationMethod.KARACHI.parameters
        else -> CalculationMethod.NORTH_AMERICA.parameters
    }.copy(madhab = madhab)
}