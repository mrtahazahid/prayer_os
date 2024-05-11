package com.iw.android.prayerapp.extension

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.iw.android.prayerapp.R
import com.iw.android.prayerapp.databinding.DialogToolsBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun AppCompatActivity.setStatusBarWithBlackIcon(@ColorRes color: Int) {
    this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    this.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    this.window.statusBarColor = ContextCompat.getColor(this, color)
}

fun Fragment.setStatusBarWithBlackIcon(@ColorRes color: Int) {
    requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    requireActivity().window.statusBarColor =
        ContextCompat.getColor(requireActivity(), R.color.black)
}

fun formatRemainingTime(secondsRemaining: Int): String {
    val hours = TimeUnit.SECONDS.toHours(secondsRemaining.toLong())
    val minutes = TimeUnit.SECONDS.toMinutes(secondsRemaining.toLong()) % 60
    val seconds = secondsRemaining % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

@SuppressLint("SimpleDateFormat")
fun convertToFunTime(timestamp: Long): String {
    val timeZoneID = TimeZone.getDefault().id
    val formatter = SimpleDateFormat("h:mm a")
    formatter.timeZone = TimeZone.getTimeZone(timeZoneID)
    return formatter.format(Date(timestamp))


}

@SuppressLint("SimpleDateFormat")
fun convertToFunDateTime(timestamp: Long): String {
    val timeZoneID = TimeZone.getDefault().id
    val formatter = SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone(timeZoneID)
    return formatter.format(Date(timestamp))


}

fun showToolDialog(context: Context, title: String, des: String) {
    val dialog = android.app.AlertDialog.Builder(context).create()
    val dialogBinding: DialogToolsBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.dialog_tools,
        null,
        false
    )
    dialog?.let { dialog ->
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setView(dialogBinding.root)
        dialog.setCancelable(true)


        dialogBinding.textViewGotIt.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.textViewActionTitle.text = title
        dialogBinding.textViewDes.text = des

        dialog.show()
    }
}

fun getDeviceName(): String {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        capitalize(model)
    } else {
        capitalize(manufacturer) + " " + model
    }
}

fun getAndroidVersion(): String {
    val release = Build.VERSION.RELEASE
    val sdkInt = Build.VERSION.SDK_INT
    return "$sdkInt"
}

private fun capitalize(s: String): String {
    if (s.isEmpty()) return s
    val firstChar = s[0]
    return if (Character.isUpperCase(firstChar)) {
        s
    } else {
        Character.toUpperCase(firstChar) + s.substring(1)
    }
}

fun showPermissionDialog(
    context: Context,
    permissionTextProvider: PermissionTextProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    val dialogBuilder = AlertDialog.Builder(context)
    dialogBuilder.apply {
        setTitle("Permission required")
        setCancelable(false)
        setMessage(permissionTextProvider.getDescription(isPermanentlyDeclined))
        setPositiveButton(if (isPermanentlyDeclined) "Grant permission" else "OK") { dialogInterface: DialogInterface, _: Int ->
            if (isPermanentlyDeclined) {
                onGoToAppSettingsClick()
            } else {
                onOkClick()
            }
        }
    }
    val dialog = dialogBuilder.create()
    dialog.show()
}

interface PermissionTextProvider {
    fun getDescription(isPermanentlyDeclined: Boolean): String
}

class NotificationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined notification permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "Please allow notification permission to get notifications of this app."
        }
    }
}

class LocationFinePermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined location permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "Please allow location permission to get namaz time."
        }
    }
}

class LocationGPSPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined GPS permission. " +
                    "You can go to the app settings to grant it."
        } else {
            "Please allow GPS permission to get namaz time."
        }
    }
}

fun getCurrentDateFormatted(): String {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val calendar = Calendar.getInstance()
    return dateFormat.format(calendar.time)
}

fun getIslamicDate(): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd")
    val currentDate = sdf.format(Date())

    val splitDate = currentDate.split("/")
    val year = splitDate[0].toInt()
    val month = splitDate[1].toInt()
    val day = splitDate[2].toInt()

    val gregorianDate: LocalDate = LocalDate.of(year, month, day)
    val hijrahDate = HijrahDate.from(gregorianDate)

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
    val formattedHijrahDate = hijrahDate.format(formatter)

    return formattedHijrahDate.replace(" ", " ") // Customize space as needed
}

fun getIslamicDateByOffSet(offset: Int): String {
    val sdf = SimpleDateFormat("yyyy/M/dd")
    val currentDate = sdf.format(Date())

    val splitDate = currentDate.split("/")
    val year = splitDate[0].toInt()
    val month = splitDate[1].toInt()
    val day = splitDate[2].toInt()

    val gregorianDate: LocalDate = LocalDate.of(year, month, day)

    // Add offset days to the current date
    val offsetGregorianDate = gregorianDate.plusDays(offset.toLong())

    val hijrahDate = HijrahDate.from(offsetGregorianDate)

    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH)
    val formattedHijrahDate = hijrahDate.format(formatter)

    return formattedHijrahDate.replace(" ", " ") // Customize space as needed
}
