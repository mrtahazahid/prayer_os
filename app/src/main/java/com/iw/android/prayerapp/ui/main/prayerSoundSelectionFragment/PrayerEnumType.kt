package com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment

enum class PrayerEnumType(private val type: String) {

    NONE("none"),
    ADHAN("adhan"),
    TONES("tones"),
    VIBRATE("vibrate"),
    SILENT("silent"),
    OFF("off");

    companion object {
        fun fromInt(value: String) = PrayerEnumType.values().first { it.type == value }
    }

    fun getValue(): String {
        return type
    }
}
