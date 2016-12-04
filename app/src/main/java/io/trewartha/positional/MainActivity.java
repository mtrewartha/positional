package io.trewartha.positional;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleApiClient.ConnectionCallbacks, CompoundButton.OnCheckedChangeListener {

    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final long LOCATION_UPDATE_INTERVAL = 1000; // ms

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
    @BindView(R.id.provider_value_text_view) TextView providerValueTextView;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.screen_lock_switch) Switch screenLockSwitch;

    private boolean useDecimalDegrees;
    private boolean useMetricUnits;
    private boolean screenLock;
    private GoogleApiClient googleAPIClient;
    private Location location;
    private LocationRequest locationRequest;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initializeNightMode();
        ButterKnife.bind(this);

        googleAPIClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LOCATION_UPDATE_PRIORITY)
                .setInterval(LOCATION_UPDATE_INTERVAL);

        sharedPreferences = getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE);
        useMetricUnits = sharedPreferences.getBoolean(getString(R.string.settings_metric_units_key), false);
        useDecimalDegrees = sharedPreferences.getBoolean(getString(R.string.settings_decimal_degrees_key), false);
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false);

        populateLocationViews(useMetricUnits, useDecimalDegrees, location);
        screenLockSwitch.setOnCheckedChangeListener(this);
        screenLockSwitch.setChecked(screenLock);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initializeNightMode();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION) {
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
                                requestAccessFineLocationPermission();
                            }
                        })
                        .show();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        FirebaseCrash.log("Google Play Services connection established");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        FirebaseCrash.log("Google Play Services connection suspended");
        suspendLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash.log("Google Play Services connection failed");
        new AlertDialog.Builder(this)
                .setTitle(R.string.google_play_services_connection_failed_title)
                .setMessage(R.string.google_play_services_connection_failed_message)
                .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // TODO: Log this to Firebase
                        MainActivity.this.finish();
                    }
                })
                .show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            progressBar.setVisibility(View.VISIBLE);
        } else if (location.hasAltitude()) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            populateLocationViews(useMetricUnits, useDecimalDegrees, location);
        }
        this.location = location;
    }

    @OnClick({R.id.elevation_unit_text_view, R.id.speed_unit_text_view, R.id.accuracy_unit_text_view})
    public void onDistanceUnitClicked() {
        useMetricUnits = !useMetricUnits;
        populateLocationViews(useMetricUnits, useDecimalDegrees, location);
        setBooleanPreference(getString(R.string.settings_metric_units_key), useMetricUnits);
    }

    @OnClick(R.id.coordinates_layout)
    public void onCoordinatesClicked() {
        useDecimalDegrees = !useDecimalDegrees;
        populateLocationViews(useMetricUnits, useDecimalDegrees, location);
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

    private void populateLocationViews(boolean useMetricUnits, boolean useDecimalDegrees, @Nullable Location location) {
        final String accuracy, bearing, elevation, latitude, longitude, provider, satellites, speed, distanceUnit, speedUnit;
        if (location == null) {
            accuracy = "0";
            bearing = "0";
            elevation = "0";
            latitude = "0";
            longitude = "0";
            provider = "";
            satellites = "0";
            speed = "0";
            distanceUnit = getString(R.string.unit_feet);
            speedUnit = getString(R.string.unit_mph);
        } else {
            bearing = String.format(Locale.getDefault(), "%.0f", location.getBearing());
            provider = location.getProvider();
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
        providerValueTextView.setText(provider);
        satellitesValueTextView.setText(satellites);
        speedValueTextView.setText(speed);
        speedUnitTextView.setText(speedUnit);
    }

    private void requestAccessFineLocationPermission() {
        FirebaseCrash.log("Requesting permission for ACCESS_FINE_LOCATION");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            FirebaseCrash.log("ACCESS_FINE_LOCATION permission is needed");
            requestAccessFineLocationPermission();
        } else {
            FirebaseCrash.log("Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleAPIClient, locationRequest, this);
        }
    }

    private void setBooleanPreference(@NonNull String key, boolean value) {
        FirebaseCrash.log("Saving " + key + " preference as " + value);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void suspendLocationUpdates() {
        FirebaseCrash.log("Suspending location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleAPIClient, this);
    }
}
