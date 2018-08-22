package io.trewartha.positional.ui.compass;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import java.util.Date;

import androidx.appcompat.widget.AppCompatImageView;

// Pulled from this Stack Overflow answer: http://stackoverflow.com/a/22738030/1253644

public class CompassView extends AppCompatImageView {

    private static final float TIME_DELTA_THRESHOLD = 0.15f; // maximum time difference between iterations, s
    private static final float ANGLE_DELTA_THRESHOLD = 0.1f; // minimum rotation change to be redrawn, deg

    private static final float INERTIA_MOMENT_DEFAULT = 0.1f;    // default physical properties
    private static final float ALPHA_DEFAULT = 15;
    private static final float MB_DEFAULT = 3000;

    private long time1, time2;              // timestamps of previous iterations--used in numerical integration
    private float angle1, angle2, angle0;   // angles of previous iterations
    private float angleLastDrawn;           // last drawn anglular position
    private boolean animationOn = false;    // if animation should be performed

    private float inertiaMoment = INERTIA_MOMENT_DEFAULT;   // moment of inertia
    private float alpha = ALPHA_DEFAULT;    // damping coefficient
    private float mB = MB_DEFAULT;  // magnetic field coefficient

    public CompassView(Context context) {
        super(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (animationOn) {
            if (angleRecalculate(new Date().getTime())) {
                this.setRotation(angle1);
            }
        } else {
            this.setRotation(angle1);
        }
        super.onDraw(canvas);
        if (animationOn) {
            this.invalidate();
        }
    }

    public void setPhysical(float inertiaMoment, float alpha, float mB) {
        this.inertiaMoment = inertiaMoment >= 0 ? inertiaMoment : INERTIA_MOMENT_DEFAULT;
        this.alpha = alpha >= 0 ? alpha : ALPHA_DEFAULT;
        this.mB = mB >= 0 ? mB : MB_DEFAULT;
    }

    public void rotationUpdate(final float angleNew, final boolean animate) {
        if (animate) {
            if (Math.abs(angle0 - angleNew) > ANGLE_DELTA_THRESHOLD) {
                angle0 = angleNew;
                this.invalidate();
            }
            animationOn = true;
        } else {
            angle1 = angleNew;
            angle2 = angleNew;
            angle0 = angleNew;
            angleLastDrawn = angleNew;
            this.invalidate();
            animationOn = false;
        }
    }

    private boolean angleRecalculate(final long timeNew) {

        // recalculate angle using simple numerical integration of motion equation
        float deltaT1 = (timeNew - time1) / 1000f;
        if (deltaT1 > TIME_DELTA_THRESHOLD) {
            deltaT1 = TIME_DELTA_THRESHOLD;
            time1 = timeNew + Math.round(TIME_DELTA_THRESHOLD * 1000);
        }
        float deltaT2 = (time1 - time2) / 1000f;
        if (deltaT2 > TIME_DELTA_THRESHOLD) {
            deltaT2 = TIME_DELTA_THRESHOLD;
        }

        // circular acceleration coefficient
        float koefI = inertiaMoment / deltaT1 / deltaT2;

        // circular velocity coefficient
        float koefAlpha = alpha / deltaT1;

        // angular momentum coefficient
        float koefk = mB * (float) (Math.sin(Math.toRadians(angle0)) * Math.cos(Math.toRadians(angle1)) -
                (Math.sin(Math.toRadians(angle1)) * Math.cos(Math.toRadians(angle0))));

        float angleNew = (koefI * (angle1 * 2f - angle2) + koefAlpha * angle1 + koefk) / (koefI + koefAlpha);

        // reassign previous iteration variables
        angle2 = angle1;
        angle1 = angleNew;
        time2 = time1;
        time1 = timeNew;

        // if angles changed less then threshold, return false - no need to redraw the view
        if (Math.abs(angleLastDrawn - angle1) < ANGLE_DELTA_THRESHOLD) {
            return false;
        } else {
            angleLastDrawn = angle1;
            return true;
        }
    }
}