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
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LocationListener, CompoundButton.OnCheckedChangeListener {

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
    @BindView(R.id.provider_status_value_text_view) TextView providerStatusValueTextView;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.screen_lock_switch) Switch screenLockSwitch;

    private boolean useDecimalDegrees;
    private boolean useMetricUnits;
    private boolean screenLock;
    private LocationManager locationManager;
    private Location location;
    private int providerStatus;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initializeNightMode();
        ButterKnife.bind(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        providerStatus = LocationProvider.TEMPORARILY_UNAVAILABLE;
        sharedPreferences = getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE);
        useMetricUnits = sharedPreferences.getBoolean(getString(R.string.settings_metric_units_key), false);
        useDecimalDegrees = sharedPreferences.getBoolean(getString(R.string.settings_decimal_degrees_key), false);
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false);

        populateLocationViews(useMetricUnits, useDecimalDegrees, LocationProvider.TEMPORARILY_UNAVAILABLE, location);
        screenLockSwitch.setOnCheckedChangeListener(this);
        screenLockSwitch.setChecked(screenLock);
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
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                FirebaseCrash.log("ACCESS_FINE_LOCATION permission granted");
                requestLocationUpdates();
            } else {
                FirebaseCrash.log("ACCESS_FINE_LOCATION permission request cancelled");
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
        if (location == null) {
            progressBar.setVisibility(View.VISIBLE);
        } else if (location.hasAltitude()) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            populateLocationViews(useMetricUnits, useDecimalDegrees, providerStatus, location);
        }
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (LocationManager.GPS_PROVIDER.equals(provider)) {
            providerStatus = status;
            populateLocationViews(useMetricUnits, useDecimalDegrees, providerStatus, location);
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
        populateLocationViews(useMetricUnits, useDecimalDegrees, providerStatus, location);
        setBooleanPreference(getString(R.string.settings_metric_units_key), useMetricUnits);
    }

    @OnClick(R.id.coordinates_layout)
    public void onCoordinatesClicked() {
        useDecimalDegrees = !useDecimalDegrees;
        populateLocationViews(useMetricUnits, useDecimalDegrees, providerStatus, location);
        setBooleanPreference(getString(R.string.settings_decimal_degrees_key), useDecimalDegrees);
    }

    @OnClick(R.id.screen_lock_switch)
    public void onScreenLockClicked() {
        screenLock = screenLockSwitch.isChecked();
        setBooleanPreference(getString(R.string.settings_screen_lock_key), screenLock);
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

    private void populateLocationViews(boolean useMetricUnits, boolean useDecimalDegrees, int providerStatus, @Nullable Location location) {
        final String accuracy, bearing, elevation, latitude, longitude, status, satellites, speed, distanceUnit, speedUnit;
        switch (providerStatus) {
            case LocationProvider.OUT_OF_SERVICE:
                status = getString(R.string.provider_status_out_of_service);
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                status = getString(R.string.provider_status_temporarily_unavailable);
                break;
            default: // AVAILABLE
                status = getString(R.string.provider_status_available);
                break;
        }
        if (location == null) {
            accuracy = "0";
            bearing = "0";
            elevation = "0";
            latitude = "0";
            longitude = "0";
            satellites = "0";
            speed = "0";
            distanceUnit = getString(R.string.unit_feet);
            speedUnit = getString(R.string.unit_mph);
        } else {
            bearing = String.format(Locale.getDefault(), "%.0f", location.getBearing());
            final Bundle locationExtras = location.getExtras();
            if (locationExtras == null) {
                satellites = "0";
            } else {
                satellites = String.format(Locale.getDefault(), "%d", locationExtras.getInt("satellites", 0));
            }
            if (useDecimalDegrees) {
                latitude = String.format(Locale.getDefault(), "%+03.5f", location.getLatitude());
                longitude = String.format(Locale.getDefault(), "%+03.5f", location.getLongitude());
            } else {
                latitude = UnitConverter.getLatitudeAsDMS(location, 2);
                longitude = UnitConverter.getLongitudeAsDMS(location, 2);
            }
            if (useMetricUnits) {
                accuracy = String.format(Locale.getDefault(), "%,d", (int) location.getAccuracy());
                elevation = String.format(Locale.getDefault(), "%,d", (int) location.getAltitude());
                speed = String.format(Locale.getDefault(), "%.1f", location.getSpeed());
                distanceUnit = getString(R.string.unit_meters);
                speedUnit = getString(R.string.unit_mps);
            } else {
                accuracy = String.format(Locale.getDefault(), "%,d", (int) UnitConverter.metersToFeet(location.getAccuracy()));
                elevation = String.format(Locale.getDefault(), "%,d", (int) UnitConverter.metersToFeet(location.getAltitude()));
                speed = String.format(Locale.getDefault(), "%.1f", UnitConverter.metersPerSecondToMilesPerHour(location.getSpeed()));
                distanceUnit = getString(R.string.unit_feet);
                speedUnit = getString(R.string.unit_mph);
            }
        }
        accuracyValueTextView.setText(accuracy);
        accuracyUnitTextView.setText(distanceUnit);
        bearingValueTextView.setText(bearing);
        elevationValueTextView.setText(elevation);
        elevationUnitTextView.setText(distanceUnit);
        latitudeValueTextView.setText(latitude);
        longitudeValueTextView.setText(longitude);
        providerStatusValueTextView.setText(status);
        satellitesValueTextView.setText(satellites);
        speedValueTextView.setText(speed);
        speedUnitTextView.setText(speedUnit);
    }

    private boolean haveLocationPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        FirebaseCrash.log("Requesting permission for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION");
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
        }, REQUEST_CODE_LOCATION_PERMISSIONS);
    }

    private void requestLocationUpdates() {
        if (haveLocationPermissions()) {
            FirebaseCrash.log("Requesting location updates");
            //noinspection MissingPermission
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0.0f, this);
        } else {
            FirebaseCrash.log("Location permissions are needed");
            requestLocationPermissions();
        }
    }

    private void suspendLocationUpdates() {
        if (haveLocationPermissions()) {
            FirebaseCrash.log("Suspending location updates");
            //noinspection MissingPermission
            locationManager.removeUpdates(this);
        } else {
            FirebaseCrash.log("Location permissions are needed");
            requestLocationPermissions();
        }
    }

    private void setBooleanPreference(@NonNull String key, boolean value) {
        FirebaseCrash.log("Saving " + key + " preference as " + value);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }
}
