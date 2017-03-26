package io.trewartha.positional.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.trewartha.positional.R;

public class CompassFragment extends Fragment implements SensorEventListener {

    private static final float ALPHA = 0.10f; // if ALPHA = 1 OR 0, no filter applies

    @BindView(R.id.compass_degrees_image_view) CompassView compassDegreesImageView;
    @BindView(R.id.compass_degrees_text_view) TextView compassDegreesTextView;
    @BindView(R.id.compass_accuracy_accelerometer_text_view) TextView accelerometerAccuracyTextView;
    @BindView(R.id.compass_accuracy_magnetometer_text_view) TextView magnetometerAccuracyTextView;

    private Sensor accelerometerSensor;
    private Sensor magnetometerSensor;
    private float[] accelerometerReading;
    private float[] magnetometerReading;
    private float[] mR = new float[9];
    private float[] orientation = new float[3];
    private SensorManager sensorManager;
    private Unbinder viewUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        accelerometerReading = new float[3];
        magnetometerReading = new float[3];
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.compass_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewUnbinder = ButterKnife.bind(this, view);

        if (accelerometerSensor == null) {
            accelerometerAccuracyTextView.setText(getString(R.string.compass_accuracy_accelerometer, getString(R.string.compass_accuracy_no_sensor_found)));
            magnetometerAccuracyTextView.setText(getString(R.string.compass_accuracy_magnetometer, getString(R.string.compass_accuracy_not_applicable)));
        }
        if (magnetometerSensor == null) {
            accelerometerAccuracyTextView.setText(getString(R.string.compass_accuracy_accelerometer, getString(R.string.compass_accuracy_not_applicable)));
            magnetometerAccuracyTextView.setText(getString(R.string.compass_accuracy_magnetometer, getString(R.string.compass_accuracy_no_sensor_found)));
        }
        if (accelerometerSensor == null || magnetometerSensor == null) {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.compass_sensor_missing_title)
                    .setMessage(R.string.compass_sensor_missing_message)
                    .setPositiveButton(R.string.compass_sensor_missing_button_positive, null)
                    .show();
        } else {
            accelerometerAccuracyTextView.setText(getString(R.string.compass_accuracy_accelerometer, getString(R.string.compass_accuracy_unknown)));
            magnetometerAccuracyTextView.setText(getString(R.string.compass_accuracy_magnetometer, getString(R.string.compass_accuracy_unknown)));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometerSensor != null && magnetometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (accelerometerSensor != null && magnetometerSensor != null) {
            sensorManager.unregisterListener(this, accelerometerSensor);
            sensorManager.unregisterListener(this, magnetometerSensor);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewUnbinder.unbind();
    }

    //region SensorEventListener methods

    @Override
    public void onSensorChanged(@NonNull SensorEvent event) {
        if (event.sensor == accelerometerSensor) {
            accelerometerReading = lowPass(event.values.clone(), accelerometerReading);
        } else if (event.sensor == magnetometerSensor) {
            magnetometerReading = lowPass(event.values.clone(), magnetometerReading);
        }
        if (accelerometerReading != null && magnetometerReading != null) {
            SensorManager.getRotationMatrix(mR, null, accelerometerReading, magnetometerReading);
            SensorManager.getOrientation(mR, orientation);
            float degrees = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
            updateCompassDegrees(degrees);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == accelerometerSensor) {
            accelerometerAccuracyTextView.setText(getAccuracyText(R.string.compass_accuracy_accelerometer, accuracy));
        } else if (sensor == magnetometerSensor) {
            magnetometerAccuracyTextView.setText(getAccuracyText(R.string.compass_accuracy_magnetometer, accuracy));
        }
    }

    //endregion

    private String getAccuracyText(@StringRes int accuracyStringRes, int accuracyConstant) {
        int accuracyTextRes;
        switch (accuracyConstant) {
            case SensorManager.SENSOR_STATUS_NO_CONTACT:
                accuracyTextRes = R.string.compass_accuracy_no_contact;
                break;
            case SensorManager.SENSOR_STATUS_UNRELIABLE:
                accuracyTextRes = R.string.compass_accuracy_unreliable;
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                accuracyTextRes = R.string.compass_accuracy_low;
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                accuracyTextRes = R.string.compass_accuracy_medium;
                break;
            case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                accuracyTextRes = R.string.compass_accuracy_high;
                break;
            default:
                accuracyTextRes = R.string.compass_accuracy_unknown;
                break;
        }
        return getString(accuracyStringRes, getString(accuracyTextRes));
    }

    @NonNull
    private float[] lowPass(@NonNull float[] input, @Nullable float[] output) {
        if (output == null) {
            return input;
        }
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    private void updateCompassDegrees(float degrees) {
        compassDegreesTextView.setText(getString(R.string.compass_degrees, (int) degrees));
        compassDegreesImageView.rotationUpdate(360f - degrees, true);
    }
}
