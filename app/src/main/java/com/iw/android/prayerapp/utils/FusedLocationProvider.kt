package com.iw.android.prayerapp.utils

import android.annotation.SuppressLint
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@SuppressLint("MissingPermission")
suspend fun FusedLocationProviderClient.getCurrentLocationSuspend(
    priority: Int = LocationRequest.PRIORITY_HIGH_ACCURACY,
    timeoutMillis: Long = 30_000L
): android.location.Location? = suspendCancellableCoroutine { cont ->
    val cts = CancellationTokenSource()
    // Request a single up-to-date location
    this.getCurrentLocation(priority, cts.token)
        .addOnSuccessListener { location ->
            cont.resume(location)
        }
        .addOnFailureListener { exc ->
            cont.resumeWithException(exc)
        }
    // If coroutine is cancelled, cancel the location request too
    cont.invokeOnCancellation { cts.cancel() }
}
