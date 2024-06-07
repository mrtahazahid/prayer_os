package com.iw.android.prayerapp.ui.main.settingFragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.iw.android.prayerapp.BuildConfig
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.base.fragment.BaseFragment
import com.iw.android.prayerapp.base.response.LocationResponse
import com.iw.android.prayerapp.data.response.NotificationSettingData
import com.iw.android.prayerapp.data.response.UserLatLong
import com.iw.android.prayerapp.databinding.FragmentSettingBinding
import com.iw.android.prayerapp.extension.CustomDialog
import com.iw.android.prayerapp.extension.MethodDialog
import com.iw.android.prayerapp.extension.convertToFunDateTime
import com.iw.android.prayerapp.extension.setStatusBarWithBlackIcon
import com.iw.android.prayerapp.services.gps.LocationService
import com.iw.android.prayerapp.ui.activities.main.MainActivity
import com.iw.android.prayerapp.ui.main.soundFragment.OnDataSelected
import com.iw.android.prayerapp.ui.main.soundFragment.SoundDialog
import com.iw.android.prayerapp.utils.AssetDialog
import com.iw.android.prayerapp.utils.GetAdhanDetails.getTimeZoneAndCity
import com.iw.android.prayerapp.utils.MapDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Locale

class SettingFragment : BaseFragment(R.layout.fragment_setting), View.OnClickListener,
    OnDataSelected, MapDialog.MapDialogListener {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!


    private val viewModel: SettingViewModel by viewModels()


    private var isCalViewShow = false
    private var isLocViewShow = false
    private var isTimeViewShow = false
    private var isSystemShow = false
    private var isAppShow = false
    private var geofence = 75
    private var snoozeTime = 0
    private var adjustHijriDate = 0
    private var countUpTime = 0
    private var isAdhanTap = false
    private var isPlayOnTap = false
    private var snooze = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        setStatusBarWithBlackIcon(R.color.bg_color)
        (requireActivity() as MainActivity).showBottomSheet()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
        setObserver()
        setOnClickListener()
    }

    override fun initialize() {
        binding.textViewLocal1.text = Locale.getDefault().toString()
        lifecycleScope.launch {
            geofence = viewModel.getGeofenceRadius()
            binding.switchAdhanDua.isChecked =
                viewModel.getSettingNotificationData()?.isAdhanDuaOn ?: false
            isAdhanTap = viewModel.getSettingNotificationData()?.isAdhanDuaOn ?: false
            isPlayOnTap = viewModel.getSettingNotificationData()?.isPrayOnTap ?: false
            binding.switchPlayOnTap.isChecked =
                viewModel.getSettingNotificationData()?.isPrayOnTap ?: false
            binding.subTxtSnoozeTime.text =
                viewModel.getSettingNotificationData()?.snoozeTime ?: "off"
            snooze = viewModel.getSettingNotificationData()?.snoozeTime ?: "off"
        }
        binding.textViewGeofenceRadius.text = "$geofence Kilometers"

        binding.textViewCoordinatesPoint.text = getFormattedCoordinates(
            viewModel.getUserLatLong?.latitude ?: 0.0,
            viewModel.getUserLatLong?.longitude ?: 0.0
        )
        val location = getTimeZoneAndCity(
            requireContext(), viewModel.getUserLatLong?.latitude ?: 0.0,
            viewModel.getUserLatLong?.longitude ?: 0.0
        )
        binding.textViewCityName.text = location?.city
        binding.textViewCityTimeZoneName.text = location?.timeZone
        binding.textViewVersions.text = "${BuildConfig.VERSION_NAME}"
        binding.textViewCaches1.text =
            convertToFunDateTime(getCacheDirectoryLastModified(requireContext()))
        binding.switchAutomatic.isChecked = viewModel.getAutomaticLocation
    }

    override fun setObserver() {
    }


    override fun setOnClickListener() {
        binding.imageViewCalculationArrow.setOnClickListener(this)
        binding.calculationView.setOnClickListener(this)
        binding.imageViewLocationArrowButton.setOnClickListener(this)
        binding.imageViewAddGeofence.setOnClickListener(this)
        binding.imageViewMinusGeofence.setOnClickListener(this)
        binding.imageViewSnoozeMinus.setOnClickListener(this)
        binding.imageViewSnoozeAdd.setOnClickListener(this)
        binding.clSchReminder.setOnClickListener(this)
        binding.timeView.setOnClickListener(this)
        binding.locationView.setOnClickListener(this)
        binding.imageViewAddHijri.setOnClickListener(this)
        binding.iqamaView.setOnClickListener(this)
        binding.system.setOnClickListener(this)
        binding.imageViewMinusHijri.setOnClickListener(this)
        binding.imageViewMinusCountUpTime.setOnClickListener(this)
        binding.imageViewAddCountUpTime.setOnClickListener(this)
        binding.imageViewSnoozeHelp.setOnClickListener(this)
        binding.imageVeiwAdhanHelp.setOnClickListener(this)
        binding.imageViewPlayHelp.setOnClickListener(this)
        binding.imageViewJuriHelp.setOnClickListener(this)
        binding.imageViewAdjustHijriHelp.setOnClickListener(this)
        binding.imageViewCountDownHelp.setOnClickListener(this)
        binding.imageViewIqamaHelp.setOnClickListener(this)
        binding.imageViewGeoHelp.setOnClickListener(this)
        binding.imageViewAuto.setOnClickListener(this)
        binding.imageViewAssetHelp.setOnClickListener(this)
        binding.imageViewSystem.setOnClickListener(this)
        binding.imageViewAutoIncrementHelp.setOnClickListener(this)
        binding.imageViewMethodHelp.setOnClickListener(this)
        binding.imageViewApp.setOnClickListener(this)
        binding.imageViewPhone.setOnClickListener(this)
        binding.imageViewAsset.setOnClickListener(this)
        binding.assetView.setOnClickListener(this)
        binding.cityView.setOnClickListener(this)
        binding.viewAsset.setOnClickListener(this)
        if (!viewModel.getAutomaticLocation) {
            binding.group.gone()
        } else {
            binding.group.show()
        }
        binding.switchAutomatic.setOnClickListener {
            binding.cityView.isClickable = !binding.switchAutomatic.isChecked
            if (binding.switchAutomatic.isChecked) {
                binding.group.show()
                requireActivity().startService(
                    Intent(
                        requireActivity(),
                        LocationService::class.java
                    )
                )

            } else {
                binding.group.gone()
                binding.cityView.isClickable = true
                requireActivity().stopService(
                    Intent(
                        requireActivity(),
                        LocationService::class.java
                    )
                )
            }
            binding.textViewGeofenceRadius.text = "$geofence Kilometers"

            binding.textViewCoordinatesPoint.text = getFormattedCoordinates(
                viewModel.getUserLatLong?.latitude ?: 0.0,
                viewModel.getUserLatLong?.longitude ?: 0.0
            )
            val location = getTimeZoneAndCity(
                requireContext(), viewModel.getUserLatLong?.latitude ?: 0.0,
                viewModel.getUserLatLong?.longitude ?: 0.0
            )
            binding.textViewCityName.text = location?.city
            binding.textViewCityTimeZoneName.text = location?.timeZone
            viewModel.setLocationAutomaticValue(binding.switchAutomatic.isChecked)
        }

        binding.switchAdhanDua.setOnCheckedChangeListener { _, isChecked ->
            // Do something with the isChecked value
            if (isChecked) {
                viewModel.saveSettingNotificationData(
                    NotificationSettingData(
                        isAdhanDuaOn = true,
                        snoozeTime = snooze,
                        isPrayOnTap = isPlayOnTap
                    )
                )
            } else {
                viewModel.saveSettingNotificationData(
                    NotificationSettingData(
                        isAdhanDuaOn = false,
                        snoozeTime = snooze,
                        isPrayOnTap = isPlayOnTap
                    )
                )
                // Code to execute when the switch is unchecked (OFF)
                // For example, you can reverse the action or update the variable back
            }
        }

        binding.switchPlayOnTap.setOnCheckedChangeListener { _, isChecked ->
            // Do something with the isChecked value
            if (isChecked) {
                viewModel.saveSettingNotificationData(
                    NotificationSettingData(
                        isAdhanDuaOn = isAdhanTap,
                        snoozeTime = snooze,
                        isPrayOnTap = true
                    )
                )
            } else {
                // Stop sound
                viewModel.saveSettingNotificationData(
                    NotificationSettingData(
                        isAdhanDuaOn = isAdhanTap,
                        snoozeTime = snooze,
                        isPrayOnTap = false
                    )
                )
                // Code to execute when the switch is unchecked (OFF)
                // For example, you can reverse the action or update the variable back
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.imageViewSnoozeHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Snooze Time",
                    "The number of minutes to remind you \n again later for prayer, like the \n functionality of an alarm clock."
                ).show()
            }

            binding.cityView.id -> {
                openLocationDialog()
            }

      binding.imageViewAssetHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Assets",
                    "The Sound assets stored locally for \n notifications and reminders."
                ).show()
            }


            binding.imageViewPlayHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Play on tap",
                    "The full adhan will play when tapped \n from a notification. This setting will be \n ignored for maghrib in Ramadan and \n will always play full adhan in that case."
                ).show()
            }


            binding.imageViewAuto.id -> {
                CustomDialog(
                    requireContext(),
                    "Automatic",
                    "Use your device GPS location to \n calculate prayers and updates  \n automatically, otherwise input a \n manual address."
                ).show()
            }

            binding.imageViewMethodHelp.id -> {
                val location = getTimeZoneAndCity(
                    requireContext(), viewModel.getUserLatLong?.latitude ?: 0.0,
                    viewModel.getUserLatLong?.longitude ?: 0.0
                )

                MethodDialog(
                    requireContext(),
                    "Method",
                    "Calculation methods are entirely based \n on location, so it's very important to \n choose the method the best matches \n'${location?.city}'.If none of the method match \n your region, Moonsighting Committee \n or Muslim world League  are suitable \n defaults in most cases, if you notice a \nlarge difference between the prayer \n times in the app and those of your \n local masjid, especially for Fajr and \n Isha, your masjid may be using custom \n twilight angles to generate their prayer \n times, which can be adjusted in the\n Elevation Rule section.You may swipe \n the row and tap recommend to let the \n app suggest a calculation method."
                ).show()
            }

            binding.imageViewJuriHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Jurisprudence",
                    "The School of thought used to \n calculate Asr.The ${"standard"} selection\n encompresses Maliki, Shafi'i, Hanbali,\n and Ja'fari schools of thought."
                ).show()
            }


            binding.imageViewCountDownHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Count up time",
                    "Specify how long the timers should \n count 'up' since the last adhan."
                ).show()
            }

            binding.imageViewIqamaHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Iqama",
                    "Specify your masjid iqama times to \n receive notifications for praying in \n congregation."
                ).show()
            }

            binding.imageViewGeoHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Geo-fence",
                    "The distance from the centre of your\n geographic region. Traveling outside of\n this boundary will trigger a notification \n for rescheduling notifications."
                ).show()
            }


            binding.imageVeiwAdhanHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Adhan dua",
                    "Dua will play after the adhan when \ntapped from a notification."
                ).show()
            }


            binding.imageViewAdjustHijriHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Adjust hijri date",
                    "Allows you to  add or subtract days \n from the hijri date until the Ummah can \n decide on a single calender.Pray \n Watch uses Umm al-Qura to stay in \n sync with hajj."
                ).show()
            }


            binding.imageViewAutoIncrementHelp.id -> {
                CustomDialog(
                    requireContext(),
                    "Auto increment hijri",
                    "The hijri days are incremented at \n maghrib instead of midnight."
                ).show()
            }

            binding.iqamaView.id -> {
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToIqamaFragment())
            }


            binding.imageViewAddCountUpTime.id -> {
                binding.textViewCountUpTimeSetter.text = incrementACountUpDate()

            }

            binding.imageViewMinusCountUpTime.id -> {
                binding.textViewCountUpTimeSetter.text = decrementCountUpDate()

            }

            binding.imageViewAddHijri.id -> {
                binding.textViewAdjustHijriDateSetter.text = incrementAdjustHijriDate()

            }

            binding.imageViewMinusHijri.id -> {
                binding.textViewAdjustHijriDateSetter.text = decrementAdjustHijriDate()

            }

            binding.clSchReminder.id -> {
                findNavController().navigate(SettingFragmentDirections.actionSettingFragmentToNotificationFragment())
            }

            binding.timeView.id -> {
                if (!isTimeViewShow) {
                    binding.imageViewTimeArrowButton.setImageResource(R.drawable.ic_down)
                    binding.timeDetailViews.visibility = View.VISIBLE
                    isTimeViewShow = true
                } else {
                    binding.imageViewTimeArrowButton.setImageResource(R.drawable.ic_forward)
                    binding.timeDetailViews.visibility = View.GONE
                    isTimeViewShow = false
                }
            }

            binding.imageViewSystem.id -> {
                if (!isSystemShow) {
                    binding.imageViewSystem.setImageResource(R.drawable.ic_down)
                    binding.systemDetailViews.visibility = View.VISIBLE
                    isSystemShow = true
                } else {
                    binding.imageViewSystem.setImageResource(R.drawable.ic_forward)
                    binding.systemDetailViews.visibility = View.GONE
                    isSystemShow = false
                }
            }

            binding.imageViewApp.id -> {
                if (!isAppShow) {
                    binding.imageViewApp.setImageResource(R.drawable.ic_down)
                    binding.appDetailViews.visibility = View.VISIBLE
                    isAppShow = true
                } else {
                    binding.imageViewApp.setImageResource(R.drawable.ic_forward)
                    binding.appDetailViews.visibility = View.GONE
                    isAppShow = false
                }
            }

            binding.imageViewSnoozeMinus.id -> {
                binding.subTxtSnoozeTime.text = decrementSnoozeMinute()
                viewModel.saveSettingNotificationData(
                    NotificationSettingData(
                        isAdhanDuaOn = isAdhanTap,
                        snoozeTime = decrementSnoozeMinute(),
                        isPrayOnTap = isPlayOnTap
                    )
                )
            }

            binding.imageViewSnoozeAdd.id -> {
                lifecycleScope.launch {
                    binding.subTxtSnoozeTime.text = incrementSnoozeMinute()
                    viewModel.saveSettingNotificationData(
                        NotificationSettingData(
                            isAdhanDuaOn = isAdhanTap,
                            snoozeTime = incrementSnoozeMinute(),
                            isPrayOnTap = isPlayOnTap
                        )
                    )
                }


            }

            binding.imageViewAddGeofence.id -> {
                binding.textViewGeofenceRadius.text = incrementGeofence(5)
                viewModel.setGeofenceRadius(geofence)
            }

            binding.imageViewMinusGeofence.id -> {
                binding.textViewGeofenceRadius.text = decrementGeofence(5)
                viewModel.setGeofenceRadius(geofence)
            }

            binding.calculationView.id, binding.imageViewCalculationArrow.id -> {
                if (!isCalViewShow) {
                    binding.imageViewCalculationArrow.setImageResource(R.drawable.ic_down)
                    binding.calculationMainDetailViews.visibility = View.VISIBLE
                    isCalViewShow = true
                    spinnerMethod()
                    spinnerElevation()
                    spinnerJurisprudence()
                } else {
                    binding.imageViewCalculationArrow.setImageResource(R.drawable.ic_forward)
                    binding.calculationMainDetailViews.visibility = View.GONE
                    isCalViewShow = false
                }
            }

            binding.locationView.id, binding.imageViewLocationArrowButton.id -> {
                if (!isLocViewShow) {
                    binding.imageViewLocationArrowButton.setImageResource(R.drawable.ic_down)
                    binding.locationDetailViews.visibility = View.VISIBLE
                    isLocViewShow = true
                } else {
                    binding.imageViewLocationArrowButton.setImageResource(R.drawable.ic_forward)
                    binding.locationDetailViews.visibility = View.GONE
                    isLocViewShow = false
                }

            }

            binding.imageViewPhone.id -> {
                openAppSettings()
            }

            binding.assetView.id, binding.imageViewAsset.id -> {
                AssetDialog().show(this.childFragmentManager, "SoundDialogFragment")
            }
        }
    }

    private fun spinnerElevation() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.elevation,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerElevation.adapter = adapter
        binding.spinnerElevation.setSelection(viewModel.getSavedPrayerElevationRule.toInt())

        binding.spinnerElevation.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch { viewModel.savePrayerElevation(position.toString()) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun spinnerJurisprudence() {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.jurisprudence,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerJurisprudence.adapter = adapter
        binding.spinnerJurisprudence.setSelection(viewModel.getSavedPrayerJurisprudence.toInt())

        binding.spinnerJurisprudence.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch { viewModel.savePrayerJurisprudence(position.toString()) }
                binding.textViewJurisprudenceDes.visibility =
                    if (position == 0) View.GONE else View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun spinnerMethod() {

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.methods,
            R.layout.custom_spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMethod.adapter = adapter
        binding.spinnerMethod.setSelection(viewModel.getSavedPrayerMethod.toInt())

        binding.spinnerMethod.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                lifecycleScope.launch { viewModel.savePrayerMethod(position.toString()) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getFormattedCoordinates(latitude: Double, longitude: Double): String {
        val decimalFormat = DecimalFormat("#.##")
        val formattedLatitude = decimalFormat.format(latitude)
        val formattedLongitude = decimalFormat.format(longitude)

        return "$formattedLatitude°, $formattedLongitude°"
    }

    private fun incrementGeofence(incrementBy: Int): String {
        geofence = (geofence + incrementBy).coerceIn(
            0,
            300
        ) // Ensure the value is within the specified range
        return "$geofence Kilometers"
    }

    private fun decrementGeofence(decrementBy: Int): String {
        geofence = (geofence - decrementBy).coerceIn(
            0,
            300
        ) // Ensure the value is within the specified range
        return if(geofence == 0) "off" else "$geofence Kilometers"
    }

    private fun incrementSnoozeMinute(): String {
        snoozeTime++
        return "$snoozeTime min"
    }

    private fun decrementSnoozeMinute(): String {
        snoozeTime--
        return if (snoozeTime > 0) "$snoozeTime min" else {
            snoozeTime = 0
            "0 min"
        }
    }

    private fun openSoundDialogFragment(
        title: String,
        subTitle: String,
        isForNotification: Boolean
    ) {
        val soundDialog = SoundDialog()
        soundDialog.listener = this
        soundDialog.title = title
        soundDialog.subTitle = subTitle
        soundDialog.isForNotification = isForNotification
        soundDialog.show(childFragmentManager, "SoundDialogFragment")
    }

    override fun onDataPassed(
        soundName: String,
        soundPosition: Int,
        sound: Int?,
        isSoundForNotification: Boolean
    ) {

    }

    private fun incrementAdjustHijriDate(): String {
        adjustHijriDate++
        return "$adjustHijriDate days"
    }

    private fun decrementAdjustHijriDate(): String {
        return if (adjustHijriDate > 0) {
            adjustHijriDate--
            "$adjustHijriDate days"
        } else {
            adjustHijriDate = 0
            "off"
        }
    }

    private fun incrementACountUpDate(): String {
        countUpTime++
        return "$countUpTime mins"
    }

    private fun decrementCountUpDate(): String {
        return if (countUpTime > 0) {
            countUpTime--
            "$countUpTime mins"
        } else {
            countUpTime = 0
            "off"
        }
    }

    private fun openAppSettings() {
        val packageName = requireActivity().packageName
        val intent = Intent()
        intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }

    fun getCacheDirectoryLastModified(context: Context): Long {
        val cacheDir = context.cacheDir
        return cacheDir.lastModified()
    }

    private fun openLocationDialog() {
        val locationDialog = MapDialog()
        locationDialog.listener = this
        lifecycleScope.launch {
            locationDialog.recentLocationList = viewModel.getRecentLocationData()
            Log.d("lsit", viewModel.getRecentLocationData().toString())
        }
        locationDialog.show(requireActivity().supportFragmentManager, "SoundDialogFragment")
    }

    override fun onDataPassed(data: LocationResponse) {

        binding.textViewCityName.text = data.locationName
        binding.textViewCityTimeZoneName.text = data.timeZone

        binding.textViewCoordinatesPoint.text = getFormattedCoordinates(
            data.lat,
            data.long
        )
        lifecycleScope.launch {
            viewModel.saveRecentLocationData(data)
            viewModel.saveUserLatLong(UserLatLong(data.lat, data.long))
            delay(1500)

        }
    }

}
