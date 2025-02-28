package com.iw.android.prayerapp.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class Compass1(context: Context) : SensorEventListener {

    interface CompassListener {
        fun onNewAzimuth(azimuth: Float)
    }

    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private var listener: CompassListener? = null

    fun setListener(l: CompassListener?) {
        listener = l
    }

    fun start() {
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)

            // Extract only azimuth (rotation around Z-axis)
            val azimuthInDegrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
            val roundedAzimuth = (azimuthInDegrees + 360) % 360 // Normalize to 0-360°

            listener?.onNewAzimuth(roundedAzimuth)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need to handle accuracy changes for this use case
    }
}
