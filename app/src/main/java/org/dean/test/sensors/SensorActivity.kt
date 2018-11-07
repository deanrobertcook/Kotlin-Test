package org.dean.test.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.dean.test.R


class SensorActivity: AppCompatActivity(), SensorEventListener {

    private var lastShakeTime = 0L
    private lateinit var textView: TextView
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        textView = findViewById(R.id.text)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()

        val registered = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        if (registered != true) {
            Log.v("TAG", "No accelerometer available")
        }

    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        super.onPause()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.v("TAG", "Sensor accuracy changed: $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            if (curTime - lastShakeTime > MIN_TIME_BETWEEN_SHAKES) {

                val x = event.values[0].toDouble()

                //knocked out y and z axes to get shake detection just in the x axis
                val y = event.values[1].toDouble() * 0f
                val z = event.values[2].toDouble() * 0f

                val acceleration = Math.sqrt(Math.pow(x, 2.0) +
                        Math.pow(y, 2.0) +
                        Math.pow(z, 2.0)) - SensorManager.GRAVITY_EARTH
                Log.d("TAG", "Acceleration is " + acceleration + "m/s^2")

                if (acceleration > SHAKE_THRESHOLD) {
                    lastShakeTime = curTime
                    textView.visibility = if (textView.visibility == View.GONE) View.VISIBLE else View.GONE
                }
            }
        }
    }

    companion object {
        const val MIN_TIME_BETWEEN_SHAKES = 1000
        const val SHAKE_THRESHOLD = 3.25f // m/(s^2) - acceleration threshold
    }


}