package io.trewartha.positional.ui.compass

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import java.util.*

// Pulled from this Stack Overflow answer: http://stackoverflow.com/a/22738030/1253644
class CompassView : AppCompatImageView {
    private var time1: Long = 0
    private var time2 // timestamps of previous iterations--used in numerical integration
            : Long = 0
    private var angle1 = 0f
    private var angle2 = 0f
    private var angle0 = 0f // angles of previous iterations = 0f
    private var angleLastDrawn = 0f // last drawn anglular position = 0f
    private var animationOn = false // if animation should be performed
    private var inertiaMoment = INERTIA_MOMENT_DEFAULT // moment of inertia
    private var dampingCoefficient = DAMPING_COEFFICIENT_DEFAULT // damping coefficient
    private var mB = MB_DEFAULT // magnetic field coefficient

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {}

    public override fun onDraw(canvas: Canvas) {
        if (animationOn) {
            if (angleRecalculate(Date().time)) {
                this.rotation = angle1
            }
        } else {
            this.rotation = angle1
        }
        super.onDraw(canvas)
        if (animationOn) {
            this.invalidate()
        }
    }

    fun setPhysical(inertiaMoment: Float, dampingCoefficient: Float, mB: Float) {
        this.inertiaMoment = if (inertiaMoment >= 0) inertiaMoment else INERTIA_MOMENT_DEFAULT
        this.dampingCoefficient = if (dampingCoefficient >= 0) dampingCoefficient else DAMPING_COEFFICIENT_DEFAULT
        this.mB = if (mB >= 0) mB else MB_DEFAULT
    }

    fun rotationUpdate(angleNew: Float, animate: Boolean) {
        if (animate) {
            if (Math.abs(angle0 - angleNew) > ANGLE_DELTA_THRESHOLD) {
                angle0 = angleNew
                this.invalidate()
            }
            animationOn = true
        } else {
            angle1 = angleNew
            angle2 = angleNew
            angle0 = angleNew
            angleLastDrawn = angleNew
            this.invalidate()
            animationOn = false
        }
    }

    private fun angleRecalculate(timeNew: Long): Boolean { // recalculate angle using simple numerical integration of motion equation
        var deltaT1 = (timeNew - time1) / 1000f
        if (deltaT1 > TIME_DELTA_THRESHOLD) {
            deltaT1 = TIME_DELTA_THRESHOLD
            time1 = timeNew + Math.round(TIME_DELTA_THRESHOLD * 1000)
        }
        var deltaT2 = (time1 - time2) / 1000f
        if (deltaT2 > TIME_DELTA_THRESHOLD) {
            deltaT2 = TIME_DELTA_THRESHOLD
        }
        // circular acceleration coefficient
        val koefI = inertiaMoment / deltaT1 / deltaT2
        // circular velocity coefficient
        val koefAlpha = dampingCoefficient / deltaT1
        // angular momentum coefficient
        val koefk = mB * (Math.sin(Math.toRadians(angle0.toDouble())) * Math.cos(Math.toRadians(angle1.toDouble())) -
                Math.sin(Math.toRadians(angle1.toDouble())) * Math.cos(Math.toRadians(angle0.toDouble()))).toFloat()
        val angleNew = (koefI * (angle1 * 2f - angle2) + koefAlpha * angle1 + koefk) / (koefI + koefAlpha)
        // reassign previous iteration variables
        angle2 = angle1
        angle1 = angleNew
        time2 = time1
        time1 = timeNew
        // if angles changed less then threshold, return false - no need to redraw the view
        return if (Math.abs(angleLastDrawn - angle1) < ANGLE_DELTA_THRESHOLD) {
            false
        } else {
            angleLastDrawn = angle1
            true
        }
    }

    companion object {
        private const val TIME_DELTA_THRESHOLD = 0.15f // maximum time difference between iterations, s
        private const val ANGLE_DELTA_THRESHOLD = 0.1f // minimum rotation change to be redrawn, deg
        private const val INERTIA_MOMENT_DEFAULT = 0.1f // default physical properties
        private const val DAMPING_COEFFICIENT_DEFAULT = 15f
        private const val MB_DEFAULT = 3000f
    }
}