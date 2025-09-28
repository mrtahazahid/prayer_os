package com.iw.android.prayerapp.utils.method

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.HighLatitudeRule
import com.batoulapps.adhan2.PrayerAdjustments
import com.iw.android.prayerapp.data.response.MethodData


fun getMethod(selectedJurisprudence: String, selectedMethod: String): CalculationParameters {

    val madhab = getMadhab(selectedJurisprudence)
    return when (selectedMethod.toIntOrNull()) {
        0 -> CalculationMethod.NORTH_AMERICA.parameters
        1 -> CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        2 -> CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
        3 -> CalculationMethod.EGYPTIAN.parameters
        4 -> CalculationParameters(fajrAngle = 18.0, ishaAngle = 17.0)
        5 -> CalculationParameters(fajrAngle = 18.0, ishaAngle = 18.0)
        6 -> CalculationParameters(
            fajrAngle = 15.0,
            ishaAngle = 15.0,
            method = CalculationMethod.MOON_SIGHTING_COMMITTEE,
            highLatitudeRule = HighLatitudeRule.SEVENTH_OF_THE_NIGHT,
            prayerAdjustments = PrayerAdjustments(
                fajr = +2,
                isha = +2
            )
        )

        7, 8 -> CalculationMethod.UMM_AL_QURA.parameters
        9 -> CalculationMethod.DUBAI.parameters
        10 -> CalculationMethod.KUWAIT.parameters
        11 -> CalculationMethod.SINGAPORE.parameters
        12 -> CalculationMethod.QATAR.parameters
        13 -> CalculationMethod.KARACHI.parameters
        14 -> CalculationMethod.EGYPTIAN.parameters.copy(
            fajrAngle = 19.1,
            ishaAngle = 17.0
        )

        else -> CalculationMethod.NORTH_AMERICA.parameters
    }.copy(madhab = madhab)
}

fun getSelectedMethod(selectedMethod: Int): MethodData {

    return getMethodList(-1)[selectedMethod]
}

fun getMethodList(selectedMethod: Int): List<MethodData> {
    val methodList = listOf(
        MethodData(
            title = "Islamic Society of N. America (ISNA)",
            CalculationMethod.NORTH_AMERICA.parameters.fajrAngle.toString(),
            CalculationMethod.NORTH_AMERICA.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Muslim World League",
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters.fajrAngle.toString(),
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Moonsighting Committee",
            CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters.fajrAngle.toString(),
            CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Egyptian General Authority of Survey",
            CalculationMethod.EGYPTIAN.parameters.fajrAngle.toString(),
            CalculationMethod.EGYPTIAN.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Algerian Ministry of Awqaf and Religious Affairs",
            "18",
            "17"
        ),
        MethodData(
            title = "Tunisian Ministry of Religious Affairs",
            "18",
            "18"
        ),
        MethodData(
            title = "London Unified Prayer Timetable",
            "",
            "",
            isDescriptionOn = true,
            description = "Timetable by london mosques"
        ),
        MethodData(
            title = "Umm Al-Quran University",
            CalculationMethod.UMM_AL_QURA.parameters.fajrAngle.toString(),
            "90 min"
        ),
        MethodData(
            title = "Umm Al-Qura University, Makkah",
            CalculationMethod.UMM_AL_QURA.parameters.fajrAngle.toString(),
            "90 min (+30 for Ramadan)"
        ),
        MethodData(
            title = "Authority of Dubai - UAE",
            CalculationMethod.DUBAI.parameters.fajrAngle.toString(),
            CalculationMethod.DUBAI.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Authority of Kuwait",
            CalculationMethod.KUWAIT.parameters.fajrAngle.toString(),
            CalculationMethod.KUWAIT.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Authority of Indonesia, Malaysia, Singapore",
            CalculationMethod.SINGAPORE.parameters.fajrAngle.toString(),
            CalculationMethod.SINGAPORE.parameters.ishaAngle.toString()
        ),
        MethodData(
            title = "Authority of Qatar",
            CalculationMethod.QATAR.parameters.fajrAngle.toString(),
            "90 min"
        ),
        MethodData(
            title = "Univ. of Islamic Sciences, Karachi",
            CalculationMethod.KARACHI.parameters.fajrAngle.toString(),
            CalculationMethod.KARACHI.parameters.ishaAngle.toString()
        ),
        MethodData(title = "Moroccan Ministry of Awqaf and Islamic Affairs", "19.1", "17", isItemLast = true),
    )
    return methodList.mapIndexed { index, method ->
        method.copy(isSelected = index == selectedMethod)
    }
}

