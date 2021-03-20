package io.trewartha.positional.ui.compass

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin

// Adapted from this SO answer: http://stackoverflow.com/a/22738030/1253644
class CompassView : AppCompatImageView {

    constructor(context: Context) :
            super(context)

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) :
            super(context, attrs, defStyle)

    var azimuth: Float = 0f
        set(value) {
            field = value
            if (abs(angle0 - value) > ANGLE_DELTA_THRESHOLD) {
                angle0 = value
                invalidate()
            }
        }

    private var time1 = 0L
    private var time2 = 0L
    private var angle0 = 0f
    private var angle1 = 0f
    private var angle2 = 0f
    private var angleLastDrawn = 0f

    override fun onDraw(canvas: Canvas?) {
        if (calculateNextAngleToDraw(System.currentTimeMillis())) {
            rotation = angle1
        }
        super.onDraw(canvas)
        invalidate()
    }

    private fun calculateNextAngleToDraw(timeNew: Long): Boolean {
        var deltaT1 = (timeNew - time1) / 1000f
        if (deltaT1 > TIME_DELTA_THRESHOLD) {
            deltaT1 = TIME_DELTA_THRESHOLD
            time1 = timeNew + (TIME_DELTA_THRESHOLD * 1000).roundToLong()
        }
        var deltaT2 = (time1 - time2) / 1000f
        if (deltaT2 > TIME_DELTA_THRESHOLD) {
            deltaT2 = TIME_DELTA_THRESHOLD
        }
        val koefI = INERTIA_MOMENT_DEFAULT / deltaT1 / deltaT2
        val koefAlpha = DAMPING_COEFFICIENT_DEFAULT / deltaT1
        val koefk =
            MB_DEFAULT * (sin(Math.toRadians(angle0.toDouble())) * cos(Math.toRadians(angle1.toDouble())) -
                    sin(Math.toRadians(angle1.toDouble())) * cos(Math.toRadians(angle0.toDouble()))).toFloat()
        val angleNew =
            (koefI * (angle1 * 2f - angle2) + koefAlpha * angle1 + koefk) / (koefI + koefAlpha)
        angle2 = angle1
        angle1 = angleNew
        time2 = time1
        time1 = timeNew
        return if (abs(angleLastDrawn - angle1) < ANGLE_DELTA_THRESHOLD) {
            false
        } else {
            angleLastDrawn = angle1
            true
        }
    }


    companion object {
        private const val TIME_DELTA_THRESHOLD = 0.15f
        private const val ANGLE_DELTA_THRESHOLD = 0.1f
        private const val INERTIA_MOMENT_DEFAULT = 0.1f
        private const val DAMPING_COEFFICIENT_DEFAULT = 15f
        private const val MB_DEFAULT = 3000f
    }

}