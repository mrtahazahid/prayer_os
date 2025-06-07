package com.iw.android.prayerapp.utils.method

import com.batoulapps.adhan2.Madhab

fun getMadhab(selectedJurisprudence: String): Madhab {
    return if (!selectedJurisprudence.isNullOrEmpty()) {
        if (selectedJurisprudence.toInt() == 1) {
            Madhab.HANAFI
        } else {
            Madhab.SHAFI
        }
    } else {
        Madhab.HANAFI
    }
}
