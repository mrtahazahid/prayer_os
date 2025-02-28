package com.iw.android.prayerapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.location.Location
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.iw.android.prayerapp.R


class QiblaCompassViewV2 : FrameLayout, QiblaSensorEventListener {

    private var currentDegree = 0f
    private  var  qiblaSensor: QiblaSensor?= null
    private var rotationDegree: Float = 138.0f
    private var currentLocation = Location("current location")

     lateinit var imageNeedle: AppCompatImageView


    private var needleDrawable: Drawable? = null
    private var hideStatusText = false

    var degreeListener: QiblaDegreeListener? = null

    var location: Location
        get() = currentLocation
        set(value) {
            currentLocation = value
            qiblaSensor?.currentLocation = LocationCoordinates(currentLocation.latitude, currentLocation.longitude)

            invalidateUI()
        }

    var degree: Float
        get() = currentDegree
        set(value) {
            currentDegree = value
            invalidateUI()
        }


    constructor(context: Context) : super(context) {
        init(null, 0)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        init(attrs, defStyle)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {

        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.QiblaCompassView, defStyle, 0)

        currentDegree = typedArray.getFloat(
            R.styleable.QiblaCompassView_degrees, 0f)
        val latitude = typedArray.getFloat(
            R.styleable.QiblaCompassView_currentLatitude, 0f)
        val longitude = typedArray.getFloat(
            R.styleable.QiblaCompassView_currentLongitude, 0f)

        hideStatusText = typedArray.getBoolean(
            R.styleable.QiblaCompassView_hideStatusText, false)


        if (typedArray.hasValue(R.styleable.QiblaCompassView_needleDrawable)) {
            needleDrawable = typedArray.getDrawable(
                R.styleable.QiblaCompassView_needleDrawable
            )
            needleDrawable?.callback = this
        }
        else {
            needleDrawable = ContextCompat.getDrawable(context,R.drawable.qibla_direction)
        }

        typedArray.recycle()

        val root = inflate(context, R.layout.view_qibla, this)

        imageNeedle = root.findViewById(R.id.imageNeedle)



        qiblaSensor = QiblaSensor(this.context)

        imageNeedle.rotation = rotationDegree
        imageNeedle.refreshDrawableState()

        qiblaSensor?.register(this)

        if(latitude !=0f && longitude !=0f) {
            currentLocation.apply {
                this.latitude = latitude.toDouble()
                this.longitude = longitude.toDouble()
            }

            qiblaSensor?.currentLocation = LocationCoordinates(latitude.toDouble(), longitude.toDouble())
        }

        invalidateUI()

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        qiblaSensor?.unregister()
    }

    @SuppressLint("SetTextI18n")
    private fun invalidateUI () {

        imageNeedle.rotation = rotationDegree



        needleDrawable?.let {
            imageNeedle.setImageDrawable(needleDrawable)
        }

    }

    override fun onDeviceAngle(angle: Int) {}

    override fun setDirectionRotation(angle: Float) {
        this.imageNeedle.rotation = angle

    }

    override fun setDialRotation(angle: Float) {
        degreeListener?.onDegreeChange(angle)
    }
}

