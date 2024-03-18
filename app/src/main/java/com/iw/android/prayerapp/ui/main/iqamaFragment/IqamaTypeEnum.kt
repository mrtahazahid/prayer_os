package com.iw.android.prayerapp.ui.main.iqamaFragment

enum class IqamaTypeEnum(private val type: String) {
    NONE("None"),
    BY_WEEKLY("By weekly"),
    BY_MONTHLY("By monthly"),
    BY_YEARLY("By yearly");

    companion object {
        fun fromInt(value: String) = IqamaTypeEnum.values().first { it.type == value }
    }

    fun getValue(): String {
        return type
    }
}