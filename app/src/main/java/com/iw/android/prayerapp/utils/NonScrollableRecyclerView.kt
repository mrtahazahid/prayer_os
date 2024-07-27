package com.iw.android.prayerapp.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NonScrollableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onInterceptTouchEvent(e: MotionEvent): Boolean {
        // Prevent touch event from being intercepted by RecyclerView
        return false
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        // Prevent touch event from being handled by RecyclerView
        return false
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        // Calculate the height of the RecyclerView based on its children
        val heightSpec = MeasureSpec.makeMeasureSpec(
            Int.MAX_VALUE shr 2, MeasureSpec.AT_MOST
        )
        super.onMeasure(widthSpec, heightSpec)
    }
}
