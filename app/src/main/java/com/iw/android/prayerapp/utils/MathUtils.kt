package com.iw.android.prayerapp.utils

import kotlin.math.floor

object MatchUtils {
    fun normaliseWithBound(angle1: Double, angle2: Double): Double {
        return angle1 - angle2 * floor(angle1 / angle2)
    }

    fun unwindAngle(angle: Double): Double {
        return normaliseWithBound(angle, 360.0)
    }
}
