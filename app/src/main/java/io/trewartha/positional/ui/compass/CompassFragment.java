package io.trewartha.positional.ui.compass;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import io.trewartha.positional.R;
import io.trewartha.positional.ui.LocationAwareFragment;

public class CompassFragment extends LocationAwareFragment {

    private static final float ALPHA = 0.10f; // if ALPHA = 1 OR 0, no filter applies
    private static final long LOCATION_UPDATE_INTERVAL = 30 * 60 * 1000; // 30 mins in ms
    private static final long LOCATION_UPDATE_MAX_WAIT_TIME = 10000; // ms

    private TextView accelerometerAccuracyTextView;
    private CompassView compassBackgroundView;
    private TextView compassDegreesTextView;
    private CompassView compassNeedleView;
    private TextView declinationTextView;
    private TextView magnetometerAccuracyTextView;
    private TextView modeTextView;

    private float[] accelerometerReading;
    private Sensor accelerometerSensor;
    private int declination;
    private CompassSensorsListener compassSensorsListener;
    private float[] magnetometerReading;
    private Sensor magnetometerSensor;
    private final float[] mR = new float[9];
    private final float[] orientation = new float[3];
    private int screenOrientation;
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferencesChangeListener;
    private String sharedPreferencesKeyCompassMode;
    private boolean useMagneticNorth;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        accelerometerReading = new float[3];
        magnetometerReading = new float[3];
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        screenOrientation = display.getRotation();

        sharedPreferencesKeyCompassMode = getString(R.string.settings_compass_mode_key);
        sharedPreferences = context.getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE);
        sharedPreferencesChangeListener = (sharedPreferences, key) -> {
            if (key.equals(sharedPreferencesKeyCompassMode))
                useMagneticNorth(shouldUseMagneticNorth());
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.compass_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        accelerometerAccuracyTextView = view.findViewById(R.id.compass_accuracy_accelerometer_text_view);
        compassBackgroundView = view.findViewById(R.id.compass_background_view);
        compassDegreesTextView = view.findViewById(R.id.compass_degrees_text_view);
        compassNeedleView = view.findViewById(R.id.compass_needle_view);
        declinationTextView = view.findViewById(R.id.compass_declination_text_view);
        magnetometerAccuracyTextView = view.findViewById(R.id.compass_accuracy_magnetometer_text_view);
        modeTextView = view.findViewById(R.id.compass_mode_text_view);

        if (accelerometerSensor == null) {
            accelerometerAccuracyTextView.setText(getString(R.string.compass_accuracy_accelerometer, getString(R.string.compass_accuracy_no_sensor_found)));
            magnetometerAccuracyTextView.setText(getString(R.string.compass_accuracy_magnetometer, getString(R.string.compass_accuracy_not_applicable)));
        }
        if (magnetometerSensor == null) {
            accelerometerAccuracyTextView.setText(getString(R.string.compass_accuracy_accelerometer, getString(R.string.compass_accuracy_not_applicable)));
            magnetometerAccuracyTextView.setText(getString(R.string.compass_accuracy_magnetometer, getString(R.string.compass_accuracy_no_sensor_found)));
        }
        if (accelerometerSensor == null || magnetometerSensor == null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.compass_sensor_missing_title)
                    .setMessage(R.string.compass_sensor_missing_message)
                    .setPositiveButton(R.string.compass_sensor_missing_button_positive, null)
                    .show();
        } else {
            accelerometerAccuracyTextView.setText(getString(R.string.compass_accuracy_accelerometer, getString(R.string.compass_accuracy_unknown)));
            magnetometerAccuracyTextView.setText(getString(R.string.compass_accuracy_magnetometer, getString(R.string.compass_accuracy_unknown)));
        }

        useMagneticNorth(shouldUseMagneticNorth());
        modeTextView.setOnClickListener(v -> onModeClick());

        declinationTextView.setText(R.string.compass_declination_unknown);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (accelerometerSensor != null && magnetometerSensor != null) {
            compassSensorsListener = new CompassSensorsListener();
            sensorManager.registerListener(compassSensorsListener, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
            sensorManager.registerListener(compassSensorsListener, magnetometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(compassSensorsListener, accelerometerSensor);
        sensorManager.unregisterListener(compassSensorsListener, magnetometerSensor);
        compassSensorsListener = null;
    }

    @Override
    public void onDetach() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesChangeListener);
        super.onDetach();
    }

    @Override
    public void onLocationChanged(Location location) {
        String declinationText;
        if (location == null) {
            declination = 0;
            declinationText = getString(R.string.compass_declination_unknown);
        } else {
            declination = (int) getDeclination(location);
            declinationText = String.valueOf(declination);
        }
        declinationTextView.setText(getString(R.string.compass_declination, declinationText));
    }

    @Override
    public long getLocationUpdateInterval() {
        return LOCATION_UPDATE_INTERVAL;
    }

    @Override
    public long getLocationUpdateMaxWaitTime() {
        return LOCATION_UPDATE_MAX_WAIT_TIME;
    }

    @Override
    public int getLocationUpdatePriority() {
        return LocationRequest.PRIORITY_LOW_POWER;
    }

    private float getDeclination(Location location) {
        return new GeomagneticField(
                (float) location.getLatitude(),
                (float) location.getLongitude(),
                (float) location.getAltitude(),
                location.getTime()
        ).getDeclination();
    }

    private void useMagneticNorth(boolean useMagneticNorth) {
        this.useMagneticNorth = useMagneticNorth;

        String preferenceValue;
        if (useMagneticNorth)
            preferenceValue = getString(R.string.settings_compass_mode_magnetic_value);
        else
            preferenceValue = getString(R.string.settings_compass_mode_true_value);
        sharedPreferences.edit()
                .putString(getString(R.string.settings_compass_mode_key), preferenceValue)
                .apply();

        int modeName = useMagneticNorth ?
                R.string.compass_mode_magnetic_north :
                R.string.compass_mode_true_north;
        modeTextView.setText(modeName);
    }

    private boolean shouldUseMagneticNorth() {
        return sharedPreferences
                .getString(sharedPreferencesKeyCompassMode, getString(R.string.settings_compass_mode_true_value))
                .equals(getString(R.string.settings_compass_mode_magnetic_value));
    }

    private void onModeClick() {
        Snackbar.make(
                getView().findViewById(R.id.coordinatorLayout),
                R.string.compass_mode_change_snackbar,
                Snackbar.LENGTH_LONG
        ).show();
    }

    private class CompassSensorsListener implements SensorEventListener {

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
                float degrees = (float) (Math.toDegrees(orientation[0]) + 360 + getScreenOrientationCompassOffset()) % 360;
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

        private int getScreenOrientationCompassOffset() {
            switch (screenOrientation) {
                case Surface.ROTATION_0:
                    return 0;
                case Surface.ROTATION_90:
                    return 90;
                case Surface.ROTATION_180:
                    return 180;
                case Surface.ROTATION_270:
                    return 270;
                default:
                    return 0;
            }
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
            if (isAdded()) {
                int declination = useMagneticNorth ? 0 : CompassFragment.this.declination;
                compassDegreesTextView.setText(getString(R.string.compass_degrees, (int) degrees - declination));
                float newDegrees = 360f - degrees + declination;
                compassBackgroundView.rotationUpdate(newDegrees, true);
                compassNeedleView.rotationUpdate(newDegrees, true);
            }
        }
    }
}
