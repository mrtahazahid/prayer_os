package com.iw.android.prayerapp.ui.main.timeFragment

enum class DuaTypeEnum(private val type: String) {
    OFF("Off"),
    TIME("Time"),
    MINUTES("Minutes");

    companion object {
        fun fromInt(value: String) = DuaTypeEnum.values().first { it.type == value }
    }

    fun getValue(): String {
        return type
    }
}