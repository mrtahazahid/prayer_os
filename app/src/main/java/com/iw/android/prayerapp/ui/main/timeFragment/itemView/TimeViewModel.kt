package com.iw.android.prayerapp.ui.main.timeFragment.itemView

import androidx.lifecycle.ViewModel
import com.iw.android.prayerapp.data.response.IslamicHolidayResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TimeViewModel @Inject constructor() :
    ViewModel() {
    var islamicHolidayArray = arrayListOf<IslamicHolidayResponse>()

    init {
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Isra' & Mi'raj",
                "27 Rajab 1445",
                "8 February 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Ramadan",
                "1 Ramadan 1445",
                "11 March 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Eid al-Fitr",
                "2 Shawwal 1445",
                "10 April 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Hajj",
                "8 Dhu'l-Hijjah 1445",
                "14 June 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Day of Arafah",
                "9 Dhu'l-Hijjah 1445",
                "15 June 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Eid al-Adha",
                "10 Dhu'l-Hijjah 1445",
                "16 June 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "New Year",
                "1 Muharram 1446",
                "7 July 2024"
            )
        )
        islamicHolidayArray.add(
            IslamicHolidayResponse(
                "Ashura",
                "10 Muharram 1446",
                "16 July 2024"
            )
        )

    }
}