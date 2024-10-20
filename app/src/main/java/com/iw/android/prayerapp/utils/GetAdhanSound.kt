package com.iw.android.prayerapp.utils

import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.data.response.PrayerSoundData
import com.iw.android.prayerapp.data.response.SoundData
import com.iw.android.prayerapp.ui.main.prayerSoundSelectionFragment.PrayerEnumType

object GetAdhanSound {
    val adhanSound = arrayListOf<SoundData>()
    val notificationSound = arrayListOf<SoundData>()
    val duaSound = arrayListOf<SoundData>()
    val assetList = arrayListOf<SoundData>()
    var prayerSoundList = arrayListOf<PrayerSoundData>()


    init {
        adhanSound.add(SoundData("Abdul Basit", R.raw.adhan_abdul_basit,false))
        adhanSound.add(SoundData("Abdul Basit Short", R.raw.adhan_abdul_basit_short,false))
        adhanSound.add(SoundData("Abdul Ghaffar", R.raw.adhan_abdul_ghaffar,false))
        adhanSound.add(SoundData("Abdul Ghaffar Short", R.raw.adhan_abdul_ghaffar_short,false))
        adhanSound.add(SoundData("Abdul Hakam", R.raw.adhan_abdul_hakam,false))
        adhanSound.add(SoundData("Abdul Hakam Short", R.raw.adhan_abdul_hakam_short,false))
        adhanSound.add(SoundData("Abdul Nasir Harak", R.raw.adhan_abdul_nasir_harak,false))
        adhanSound.add(SoundData("Abdul Nasir Harak Short", R.raw.adhan_abdul_nasir_harak_short,false))
        adhanSound.add(SoundData("Abdul Rahman Al Hindi", R.raw.adhan_abdulrahman_al_hindi,false))
        adhanSound.add(SoundData("Abdul Rahman Al Hindi Short", R.raw.adhan_abdulrahman_al_hindi_short,false))
        adhanSound.add(SoundData("Abdul Ahmad Al Batal", R.raw.adhan_ahmad_al_batal,false))
        adhanSound.add(SoundData("Abdul Ahmad Al Batal Short", R.raw.adhan_ahmad_al_batal_short,false))
        adhanSound.add(SoundData("Ahmed Al Nufais", R.raw.adhan_ahmed_al_nufais,false))
        adhanSound.add(SoundData("Ahmed Al Nufais Short", R.raw.adhan_ahmed_al_nufais_short,false))
        adhanSound.add(SoundData("Al Aqsa", R.raw.adhan_al_aqsa,false))
        adhanSound.add(SoundData("Al Aqsa Fajr", R.raw.adhan_al_aqsa_fajr,false))
        adhanSound.add(SoundData("Al Aqsa Short", R.raw.adhan_al_aqsa_short,false))
        adhanSound.add(SoundData("Al Daghreeri", R.raw.adhan_al_daghreeri,false))
        adhanSound.add(SoundData("Al Daghreeri Short", R.raw.adhan_al_daghreeri_short,false))
        adhanSound.add(SoundData("Al Hosari", R.raw.adhan_al_hosari,false))
        adhanSound.add(SoundData("Al Hosari Short", R.raw.adhan_al_hosari_short,false))
        adhanSound.add(SoundData("Al Hossaini", R.raw.adhan_al_hossaini,false))
        adhanSound.add(SoundData("Al Hossaini Short", R.raw.adhan_al_hossaini_short,false))
        adhanSound.add(SoundData("Al Imadi Qatar", R.raw.adhan_al_imadi_qatar,false))
        adhanSound.add(SoundData("Al Imadi Qatar Short", R.raw.adhan_al_imadi_qatar_short,false))
        adhanSound.add(SoundData("Al Jaziri", R.raw.adhan_al_jazairi,false))
        adhanSound.add(SoundData("Al Jaziri Short", R.raw.adhan_al_jazairi_short,false))
        adhanSound.add(SoundData("Al Maroosh Morocco", R.raw.adhan_al_maroosh_morocco,false))
        adhanSound.add(SoundData("Al Maroosh Morocco Short", R.raw.adhan_al_maroosh_morocco_short,false))
        adhanSound.add(SoundData("Al Surayhi", R.raw.adhan_al_surayhi,false))
        adhanSound.add(SoundData("Al Surayhi Short", R.raw.adhan_al_surayhi_short,false))
        adhanSound.add(SoundData("Ali Barraq Tunis", R.raw.adhan_ali_barraq_tunis,false))
        adhanSound.add(SoundData("Ali Barraq Tunis Short", R.raw.adhan_ali_barraq_tunis_short,false))
        adhanSound.add(SoundData("Azzam Dweik", R.raw.adhan_azzam_dweik,false))
        adhanSound.add(SoundData("Azzam Dweik Short", R.raw.adhan_azzam_dweik_short,false))
        adhanSound.add(SoundData("Bakir Bash", R.raw.adhan_bakir_bash,false))
        adhanSound.add(SoundData("Bakir Bash Fajr", R.raw.adhan_bakir_bash_fajr,false))
        adhanSound.add(SoundData("Bakir Bash Short", R.raw.adhan_bakir_bash_short,false))
        adhanSound.add(SoundData("Cairo", R.raw.adhan_cairo,false))
        adhanSound.add(SoundData("Cairo Fajr", R.raw.adhan_cairo_fajr,false))
        adhanSound.add(SoundData("Cairo Short", R.raw.adhan_cairo_short,false))
        adhanSound.add(SoundData("Egypt", R.raw.adhan_egypt,false))
        adhanSound.add(SoundData("Egypt Short", R.raw.adhan_egypt_short,false))
        adhanSound.add(SoundData("Fatih Seferagic", R.raw.adhan_fatih_seferagic,false))
        adhanSound.add(SoundData("Fatih Seferagic Short", R.raw.adhan_fatih_seferagic_short,false))
        adhanSound.add(SoundData("Hafez", R.raw.adhan_hafez,false))
        adhanSound.add(SoundData("Hafez Short", R.raw.adhan_hafez_short,false))
        adhanSound.add(SoundData("Hafiz Murad", R.raw.adhan_hafiz_murad,false))
        adhanSound.add(SoundData("Hafiz Murad Short", R.raw.adhan_hafiz_murad_short,false))
        adhanSound.add(SoundData("Halab", R.raw.adhan_halab,false))
        adhanSound.add(SoundData("Halab Short", R.raw.adhan_halab_short,false))
        adhanSound.add(SoundData("Ibrahim Jabar", R.raw.adhan_ibrahim_jabar,false))
        adhanSound.add(SoundData("Ibrahim Jabar Fajr", R.raw.adhan_ibrahim_jabar_fajr,false))
        adhanSound.add(SoundData("Ibrahim Jabar Short", R.raw.adhan_ibrahim_jabar_short,false))
        adhanSound.add(SoundData("Kareem Mansoury Shia", R.raw.adhan_kareem_mansoury_shia,false))
        adhanSound.add(SoundData("Kareem Mansoury Shia Short", R.raw.adhan_kareem_mansoury_shia_short,false))
        adhanSound.add(SoundData("Kuwait", R.raw.adhan_kuwait,false))
        adhanSound.add(SoundData("Kuwait Fajr", R.raw.adhan_kuwait_fajr,false))
        adhanSound.add(SoundData("Kuwait Short", R.raw.adhan_kuwait_short,false))
        adhanSound.add(SoundData("Madina", R.raw.adhan_madina,false))
        adhanSound.add(SoundData("Madina Fajr", R.raw.adhan_madina_fajr,false))
        adhanSound.add(SoundData("Madina Short", R.raw.adhan_madina_short,false))
        adhanSound.add(SoundData("Makkah", R.raw.adhan_makkah,false))
        adhanSound.add(SoundData("Makkah Fajr", R.raw.adhan_madina_fajr,false))
        adhanSound.add(SoundData("Makkah Short", R.raw.adhan_makkah_short,false))
        adhanSound.add(SoundData("Mansour Al Zahrani", R.raw.adhan_mansour_al_zahrani,false))
        adhanSound.add(SoundData("Mansour Al Zahrani Fajr", R.raw.adhan_mansour_al_zahrani_fajr,false))
        adhanSound.add(SoundData("Mansour Al Zahrani Short", R.raw.adhan_mansour_al_zahrani_short,false))
        adhanSound.add(SoundData("Marouf Al Shareef", R.raw.adhan_marouf_al_shareef,false))
        adhanSound.add(SoundData("Marouf Al Shareef Short", R.raw.adhan_marouf_al_shareef_short,false))
        adhanSound.add(SoundData("Menshawi", R.raw.adhan_menshawi,false))
        adhanSound.add(SoundData("Menshawi Short", R.raw.adhan_menshawi_short,false))
        adhanSound.add(SoundData("Mishary Al Afasy", R.raw.adhan_mishary_al_afasy,false))
        adhanSound.add(SoundData("Mishary Al Afasy 2", R.raw.adhan_mishary_al_afasy2,false))
        adhanSound.add(SoundData("Mishary Al Afasy Fajr", R.raw.adhan_mishary_al_afasy_fajr,false))
        adhanSound.add(SoundData("Mishary Al Afasy 2 Fajr", R.raw.adhan_mishary_al_afasy2_fajr,false))
        adhanSound.add(SoundData("Mishary Al Afasy Short", R.raw.adhan_mishary_al_afasy_short,false))
        adhanSound.add(SoundData("Mishary Al Afasy 2 Short", R.raw.adhan_mishary_al_afasy2_short,false))
        adhanSound.add(SoundData("Mohammad Refaat", R.raw.adhan_mohammad_refaat,false))
        adhanSound.add(SoundData("Mohammad Refaat Short", R.raw.adhan_mohammad_refaat_short,false))
        adhanSound.add(SoundData("Mohammad Al Banna", R.raw.adhan_mohammed_al_banna,false))
        adhanSound.add(SoundData("Mohammad Al Banna Short", R.raw.adhan_mohammed_al_banna_short,false))
        adhanSound.add(SoundData("Morocco", R.raw.adhan_morocco,false))
        adhanSound.add(SoundData("Morocco Short", R.raw.adhan_morocco_short,false))
        adhanSound.add(SoundData("Muhammad Qassas", R.raw.adhan_muhammad_qassas,false))
        adhanSound.add(SoundData("Muhammad Qassas Fajr", R.raw.adhan_muhammad_qassas_fajr,false))
        adhanSound.add(SoundData("Muhammad Qassas Short", R.raw.adhan_muhammad_qassas_short,false))
        adhanSound.add(SoundData("Mustapha Waleed", R.raw.adhan_mustapha_waleed,false))
        adhanSound.add(SoundData("Mustapha Waleed Short", R.raw.adhan_mustapha_waleed_short,false))
        adhanSound.add(SoundData("Muzammil Hasaballah", R.raw.adhan_muzammil_hasaballah,false))
        adhanSound.add(SoundData("Muzammil Hasaballah Short", R.raw.adhan_muzammil_hasaballah_short,false))
        adhanSound.add(SoundData("Naghshbandi", R.raw.adhan_naghshbandi,false))
        adhanSound.add(SoundData("Naghshbandi Short", R.raw.adhan_naghshbandi_short,false))
        adhanSound.add(SoundData("Nasser Al Qatami", R.raw.adhan_nasser_al_qatami,false))
        adhanSound.add(SoundData("Nasser Al Qatami Short", R.raw.adhan_nasser_al_qatami_short,false))
        adhanSound.add(SoundData("Oman", R.raw.adhan_oman,false))
        adhanSound.add(SoundData("Oman Short", R.raw.adhan_oman_short,false))
        adhanSound.add(SoundData("Saber", R.raw.adhan_saber,false))
        adhanSound.add(SoundData("Saber Short", R.raw.adhan_saber_short,false))
        adhanSound.add(SoundData("Sharif Doman", R.raw.adhan_sharif_doman,false))
        adhanSound.add(SoundData("Sharif Doman Short", R.raw.adhan_sharif_doman_short,false))
        adhanSound.add(SoundData("Turkiye", R.raw.adhan_turkiye,false))
        adhanSound.add(SoundData("Turkiye Short", R.raw.adhan_turkiye_short,false))
        adhanSound.add(SoundData("Yusuf Islam", R.raw.adhan_yusuf_islam,false))
        adhanSound.add(SoundData("Yusuf Islam Short", R.raw.adhan_yusuf_islam_short,false))

        duaSound.add(SoundData("Wasilah Fadilah",R.raw.dua_wasilah_fadilah,false))

        notificationSound.add(SoundData("Adventure",R.raw.noti_adventure,false))
        notificationSound.add(SoundData("Adventure Long",R.raw.noti_adventure_long,false))
        notificationSound.add(SoundData("Allahu Ya'Rabbi",R.raw.noti_alalhu_ya_rabbi,false))
        notificationSound.add(SoundData("Alarm  Beats",R.raw.noti_alarm_beats,false))
        notificationSound.add(SoundData("Alarm  Beats Long",R.raw.noti_alarm_beats_long,false))
        notificationSound.add(SoundData("Alert",R.raw.noti_alert,false))
        notificationSound.add(SoundData("Allah's Name",R.raw.noti_allahs_names,false))
        notificationSound.add(SoundData("Allahu Akbar",R.raw.noti_allahu_akbar,false))
        notificationSound.add(SoundData("Allahu Akbar Allahu Akbar",R.raw.noti_allahu_akbar_allahu_akbar,false))
        notificationSound.add(SoundData("Allahu Ya Allah",R.raw.noti_allahu_ya_allah,false))
        notificationSound.add(SoundData("Allahu Ya Allah Long",R.raw.noti_allahu_ya_allah_long,false))
        notificationSound.add(SoundData("Announce Asr Soon",R.raw.noti_announce_asr_soon,false))
        notificationSound.add(SoundData("Announce Asr Time",R.raw.noti_announce_asr_time,false))
        notificationSound.add(SoundData("Announce Dhuhr Soon",R.raw.noti_announce_dhuhr_soon,false))
        notificationSound.add(SoundData("Announce Dhuhr Time",R.raw.noti_announce_dhuhr_time,false))
        notificationSound.add(SoundData("Announce Fajr Soon",R.raw.noti_announce_fajr_soon,false))
        notificationSound.add(SoundData("Announce Fajr Time",R.raw.noti_announce_fajr_time,false))
        notificationSound.add(SoundData("Announce Isha Soon",R.raw.noti_announce_isha_soon,false))
        notificationSound.add(SoundData("Announce Isha Time",R.raw.noti_announce_isha_time,false))
        notificationSound.add(SoundData("Announce Maghrib Soon",R.raw.noti_announce_maghrib_soon,false))
        notificationSound.add(SoundData("Announce Maghrib Time",R.raw.noti_announce_maghrib_time,false))
        notificationSound.add(SoundData("Beep",R.raw.noti_beep,false))
        notificationSound.add(SoundData("Bismillah 1",R.raw.noti_bismillah1,false))
        notificationSound.add(SoundData("Bismillah 2",R.raw.noti_bismillah2,false))
        notificationSound.add(SoundData("Bismillahilladhi",R.raw.noti_bismillahilladhi,false))
        notificationSound.add(SoundData("Chime",R.raw.noti_chime,false))
        notificationSound.add(SoundData("Chimes",R.raw.noti_chimes,false))
        notificationSound.add(SoundData("Chimes Long",R.raw.noti_chimes_long,false))
        notificationSound.add(SoundData("Circles",R.raw.noti_circles,false))
        notificationSound.add(SoundData("Drums",R.raw.noti_drums,false))
        notificationSound.add(SoundData("Ertugrul",R.raw.noti_ertugrul,false))
        notificationSound.add(SoundData("Ertugrul 2",R.raw.noti_ertugrul2,false))
        notificationSound.add(SoundData("Ertugrul 3",R.raw.noti_ertugrul3,false))
        notificationSound.add(SoundData("Ertugrul Long",R.raw.noti_ertugrul_long,false))
        notificationSound.add(SoundData("Ertugrul 2 Flute",R.raw.noti_ertugrul2_flute,false))
        notificationSound.add(SoundData("Fajr",R.raw.noti_fajr,false))
        notificationSound.add(SoundData("Fajr long",R.raw.noti_fajr_long,false))
        notificationSound.add(SoundData("Galaxy",R.raw.noti_galaxy,false))
        notificationSound.add(SoundData("Hasbi Allah",R.raw.noti_hasbi_allah,false))
        notificationSound.add(SoundData("High",R.raw.noti_high,false))
        notificationSound.add(SoundData("High Long",R.raw.noti_high_long,false))
        notificationSound.add(SoundData("Iqama",R.raw.noti_iqama,false))
        notificationSound.add(SoundData("Iqama 2",R.raw.noti_iqama2,false))
        notificationSound.add(SoundData("Iqama 3",R.raw.noti_iqama3,false))
        notificationSound.add(SoundData("Kalima",R.raw.noti_kalima,false))
        notificationSound.add(SoundData("Labbaik Allahuma",R.raw.noti_labbaik_allahuma,false))
        notificationSound.add(SoundData("March",R.raw.noti_march,false))
        notificationSound.add(SoundData("Message",R.raw.noti_message,false))
        notificationSound.add(SoundData("Morning",R.raw.noti_morning,false))
        notificationSound.add(SoundData("Prayer Better Sleep",R.raw.noti_prayer_better_sleep,false))
        notificationSound.add(SoundData("Presto",R.raw.noti_presto,false))
        notificationSound.add(SoundData("Ring",R.raw.noti_ring,false))
        notificationSound.add(SoundData("Sci-Fi Beat",R.raw.noti_sci_fi_beat,false))
        notificationSound.add(SoundData("Sci-Fi Beat Long",R.raw.noti_sci_fi_beat_long,false))
        notificationSound.add(SoundData("Shahada",R.raw.noti_shahada,false))
        notificationSound.add(SoundData("Sonar",R.raw.noti_sonar,false))
        notificationSound.add(SoundData("Sonar Long",R.raw.noti_sonar_long,false))
        notificationSound.add(SoundData("SubhanAllah 1",R.raw.noti_subhanallah1,false))
        notificationSound.add(SoundData("SubhanAllah 2",R.raw.noti_subhanallah2,false))
        notificationSound.add(SoundData("Takbeer Eid",R.raw.noti_takbeer_eid,false))
        notificationSound.add(SoundData("Tone",R.raw.noti_tone,false))
        notificationSound.add(SoundData("Tones",R.raw.noti_tones,false))
        notificationSound.add(SoundData("Trickle",R.raw.noti_trickle,false))
        notificationSound.add(SoundData("Tweet",R.raw.noti_tweet,false))
        notificationSound.add(SoundData("UFO",R.raw.noti_ufo,false))
        notificationSound.add(SoundData("UFO Long",R.raw.noti_ufo_long,false))
        notificationSound.add(SoundData("Vibrate",R.raw.noti_vibrate,false))
        notificationSound.add(SoundData("Wake Up",R.raw.noti_wake_up,false))
        notificationSound.add(SoundData("Wake Up Long",R.raw.noti_wake_up_long,false))
        notificationSound.add(SoundData("Winds",R.raw.noti_winds,false))
        notificationSound.add(SoundData("Xylophone",R.raw.noti_xylophone,false))



        for (sound in adhanSound){
            sound.isSoundSelected = false
            assetList.add(sound)
        }

        for (sound in notificationSound){
            sound.isSoundSelected = false
            assetList.add(sound)
        }

        prayerSoundList.add(
            PrayerSoundData(
                "Adhan", R.drawable.ic_mike, PrayerEnumType.ADHAN.getValue(),
                isImageForwardShow = true,
                isItemSelected = true,
                selectedItemAdhanTitle = "adhan",
                selectedItemTonesTitle = "Tones",
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Tones", R.drawable.ic_mike, PrayerEnumType.TONES.getValue(),
                isImageForwardShow = true,
                isItemSelected = false
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Vibrate", R.drawable.ic_mike, PrayerEnumType.VIBRATE.getValue(),
                isImageForwardShow = false,
                isItemSelected = false
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Silent", R.drawable.ic_mike, PrayerEnumType.SILENT.getValue(),
                isImageForwardShow = false,
                isItemSelected = false
            )
        )
        prayerSoundList.add(
            PrayerSoundData(
                "Off", R.drawable.ic_mike, PrayerEnumType.OFF.getValue(),
                isImageForwardShow = false,
                isItemSelected = false
            )
        )
    }
}