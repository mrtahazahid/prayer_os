package com.iw.android.prayerapp.utils.decodeImage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun decodeSampledBitmap(
    context: Context,
    resId: Int,
    reqWidth: Int,
    reqHeight: Int
): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    // First decode to get dimensions
    context.resources.openRawResource(resId).use {
        BitmapFactory.decodeStream(it, null, options)
    }

    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
    options.inJustDecodeBounds = false
    options.inPreferredConfig = Bitmap.Config.RGB_565 // Optional: reduce memory

    // Decode actual bitmap with sampling
    return context.resources.openRawResource(resId).use {
        BitmapFactory.decodeStream(it, null, options)!!
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height, width) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}