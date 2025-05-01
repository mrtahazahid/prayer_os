package com.iw.android.prayerapp.utils

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


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

class LocationPermissionTextProvider : PermissionTextProvider {
    override fun getDescription(isPermanentlyDeclined: Boolean): String {
        return if (isPermanentlyDeclined) {
            "It seems you permanently declined location permission. " +
                    "You can go to the app settings to grant it and allow the app to calculate prayer times based on your location."
        } else {
            "This app needs access to your location to accurately calculate prayer times for your area."
        }
    }
}