package io.trewartha.positional;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LocationListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_LOCATION_PERMISSIONS = 1;

    @BindView(R.id.accuracy_value_text_view) TextView accuracyValueTextView;
    @BindView(R.id.accuracy_unit_text_view) TextView accuracyUnitTextView;
    @BindView(R.id.elevation_value_text_view) TextView elevationValueTextView;
    @BindView(R.id.elevation_unit_text_view) TextView elevationUnitTextView;
    @BindView(R.id.coordinates_latitude_value_text_view) TextView latitudeValueTextView;
    @BindView(R.id.coordinates_longitude_value_text_view) TextView longitudeValueTextView;
    @BindView(R.id.speed_value_text_view) TextView speedValueTextView;
    @BindView(R.id.speed_unit_text_view) TextView speedUnitTextView;
    @BindView(R.id.bearing_value_text_view) TextView bearingValueTextView;
    @BindView(R.id.bearing_unit_text_view) TextView bearingUnitTextView;
    @BindView(R.id.satellites_value_text_view) TextView satellitesValueTextView;
    @BindView(R.id.gps_status_value_text_view) TextView providerStatusValueTextView;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.screen_lock_switch) ImageView screenLockSwitch;

    private boolean useDecimalDegrees;
    private boolean useMetricUnits;
    private boolean screenLock;
    private LocationFormatter locationFormatter;
    private LocationManager locationManager;
    private Location location;
    private int providerStatus;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_cascade);
        initializeNightMode();
        ButterKnife.bind(this);

        locationFormatter = new LocationFormatter(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        providerStatus = LocationProvider.TEMPORARILY_UNAVAILABLE;
        sharedPreferences = getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE);
        useMetricUnits = sharedPreferences.getBoolean(getString(R.string.settings_metric_units_key), false);
        useDecimalDegrees = sharedPreferences.getBoolean(getString(R.string.settings_decimal_degrees_key), false);
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false);

        populateLocationViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        suspendLocationUpdates();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initializeNightMode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.info(TAG, "Location permissions granted");
                requestLocationUpdates();
            } else {
                Log.info(TAG, "Location permissions request cancelled");
                new AlertDialog.Builder(this)
                        .setTitle(R.string.access_fine_location_permission_explanation_title)
                        .setMessage(R.string.access_fine_location_permission_explanation_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestLocationPermissions();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location == null) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            logLocation(location);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            populateLocationViews();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            providerStatus = status;
            populateLocationViews();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        requestLocationUpdates();
    }

    @Override
    public void onProviderDisabled(String provider) {
        suspendLocationUpdates();
    }

    @OnClick({R.id.elevation_unit_text_view, R.id.speed_unit_text_view, R.id.accuracy_unit_text_view})
    public void onDistanceUnitClicked() {
        useMetricUnits = !useMetricUnits;
        populateLocationViews();
        setBooleanPreference(getString(R.string.settings_metric_units_key), useMetricUnits);
    }

    @OnClick(R.id.coordinates_layout)
    public void onCoordinatesClicked() {
        useDecimalDegrees = !useDecimalDegrees;
        populateLocationViews();
        setBooleanPreference(getString(R.string.settings_decimal_degrees_key), useDecimalDegrees);
    }

    @OnClick(R.id.screen_lock_switch)
    public void onScreenLockClicked() {
        screenLock = !screenLock;
        setBooleanPreference(getString(R.string.settings_screen_lock_key), screenLock);
        final int textRes = screenLock ? R.string.screen_lock_on : R.string.screen_lock_off;
        Toast.makeText(this, textRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (compoundButton.getId() == R.id.screen_lock_switch) {
            setBooleanPreference(getString(R.string.settings_screen_lock_key), checked);
            lockScreen(checked);
        }
    }

    private void initializeNightMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    private void lockScreen(boolean lock) {
        if (lock) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    private void populateLocationViews() {
        accuracyValueTextView.setText(locationFormatter.getAccuracy(location, useMetricUnits));
        bearingValueTextView.setText(locationFormatter.getBearing(location));
        elevationValueTextView.setText(locationFormatter.getElevation(location, useMetricUnits));
        latitudeValueTextView.setText(locationFormatter.getLatitude(location, useDecimalDegrees));
        longitudeValueTextView.setText(locationFormatter.getLongitude(location, useDecimalDegrees));
        providerStatusValueTextView.setText(locationFormatter.getProviderStatus(providerStatus));
        satellitesValueTextView.setText(locationFormatter.getSatellites(location));
        speedValueTextView.setText(locationFormatter.getSpeed(location, useMetricUnits));

        accuracyUnitTextView.setText(locationFormatter.getDistanceUnit(useMetricUnits));
        elevationUnitTextView.setText(locationFormatter.getDistanceUnit(useMetricUnits));
        speedUnitTextView.setText(locationFormatter.getSpeedUnit(useMetricUnits));
    }

    private boolean haveLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        Log.info(TAG, "Requesting permission for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, REQUEST_CODE_LOCATION_PERMISSIONS);
    }

    private void requestLocationUpdates() {
        if (haveLocationPermissions()) {
            Log.info(TAG, "Requesting location updates");
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
        } else {
            Log.info(TAG, "Location permissions are needed");
            requestLocationPermissions();
        }
    }

    private void suspendLocationUpdates() {
        if (haveLocationPermissions()) {
            Log.info(TAG, "Suspending location updates");
            //noinspection MissingPermission
            locationManager.removeUpdates(this);
        } else {
            Log.info(TAG, "Location permissions are needed");
            requestLocationPermissions();
        }
    }

    private void setBooleanPreference(@NonNull String key, boolean value) {
        Log.info(TAG, "Saving " + key + " preference as " + value);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void logLocation(@NonNull Location location) {
        Log.debug(TAG, "Location received:");
        Log.debug(TAG, "       Latitude: " + location.getLatitude());
        Log.debug(TAG, "      Longitude: " + location.getLongitude());
        Log.debug(TAG, "       Accuracy: " + (location.hasAccuracy() ? location.getAccuracy() + " m" : "none"));
        Log.debug(TAG, "        Bearing: " + (location.hasBearing() ? location.getBearing() + "°" : "none"));
        Log.debug(TAG, "      Elevation: " + (location.hasAltitude() ? location.getAltitude() + " m/s" : "none"));
        Log.debug(TAG, "          Speed: " + (location.hasSpeed() ? location.getSpeed() + " m/s" : "none"));
    }
}
