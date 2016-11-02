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

    private static final String ALTITUDE_UNIT_FEET = "feet";
    private static final String ALTITUDE_UNIT_METERS = "meters";
    private static final String COORDINATES_FORMAT_DD = "dd";
    private static final String COORDINATES_FORMAT_DMS = "dms";
    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;
    private static final int LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final long LOCATION_UPDATE_INTERVAL = 1000; // ms

    @BindView(R.id.accuracy_text_view) TextView accuracyTextView;
    @BindView(R.id.accuracy_unit_text_view) TextView accuracyUnitTextView;
    @BindView(R.id.altitude_text_view) TextView altitudeTextView;
    @BindView(R.id.altitude_unit_text_view) TextView altitudeUnitTextView;
    @BindView(R.id.coordinates_latitude_text_view) TextView latitudeTextView;
    @BindView(R.id.coordinates_longitude_text_view) TextView longitudeTextView;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.screen_lock_switch) Switch screenLockSwitch;

    private String altitudeUnit;
    private String coordinatesFormat;
    private boolean screenLock;
    private GoogleApiClient googleAPIClient;
    private Location location;
    private LocationRequest locationRequest;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        altitudeUnit = sharedPreferences.getString(getString(R.string.settings_altitude_unit_key), getString(R.string.settings_altitude_unit_default));
        coordinatesFormat = sharedPreferences.getString(getString(R.string.settings_coordinates_format_key), getString(R.string.settings_coordinates_format_default));
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), Boolean.parseBoolean(getString(R.string.settings_screen_lock_default)));

        populateLocationViews(altitudeUnit, coordinatesFormat, location);
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
            populateLocationViews(altitudeUnit, coordinatesFormat, location);
        }
        this.location = location;
    }

    @OnClick(R.id.altitude_unit_text_view)
    public void onAltitudeUnitClicked() {
        altitudeUnit = altitudeUnit.equals(ALTITUDE_UNIT_FEET) ? ALTITUDE_UNIT_METERS : ALTITUDE_UNIT_FEET;
        FirebaseCrash.log("Switching altitude unit to " + altitudeUnit);
        populateLocationViews(altitudeUnit, coordinatesFormat, location);
        setStringPreference(getString(R.string.settings_altitude_unit_key), altitudeUnit);
    }

    @OnClick(R.id.coordinates_layout)
    public void onCoordinatesClicked() {
        coordinatesFormat = coordinatesFormat.equals(COORDINATES_FORMAT_DD) ? COORDINATES_FORMAT_DMS : COORDINATES_FORMAT_DD;
        FirebaseCrash.log("Switching coordinates format to " + coordinatesFormat);
        populateLocationViews(altitudeUnit, coordinatesFormat, location);
        setStringPreference(getString(R.string.settings_coordinates_format_key), coordinatesFormat);
    }

    @OnClick(R.id.screen_lock_switch)
    public void onScreenLockClicked() {
        screenLock = screenLockSwitch.isChecked();
        FirebaseCrash.log("Switching screen lock to " + altitudeUnit);
        saveScreenLockPreference(screenLock);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (compoundButton.getId() == R.id.screen_lock_switch) {
            saveScreenLockPreference(checked);
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

    private void populateLocationViews(@Nullable String altitudeUnit, @Nullable String coordinatesFormat, @Nullable Location location) {
        final String accuracyText;
        final String altitudeText;
        final String latitudeText;
        final String longitudeText;
        final String unitText;
        if (location == null) {
            accuracyText = "0";
            altitudeText = "0";
            latitudeText = "0";
            longitudeText = "0";
            unitText = getString(R.string.unit_feet);
        } else {
            if (coordinatesFormat == null || coordinatesFormat.equals(COORDINATES_FORMAT_DD)) {
                latitudeText = String.format(Locale.getDefault(), "%.5f", location.getLatitude());
                longitudeText = String.format(Locale.getDefault(), "%.5f", location.getLongitude());
            } else {
                latitudeText = UnitConverter.getLatitudeAsDMS(location, 2);
                longitudeText = UnitConverter.getLongitudeAsDMS(location, 2);
            }
            if (altitudeUnit == null || altitudeUnit.equals(ALTITUDE_UNIT_FEET)) {
                accuracyText = String.format(Locale.getDefault(), "%,d", (int) UnitConverter.metersToFeet(location.getAccuracy()));
                altitudeText = String.format(Locale.getDefault(), "%,d", (int) UnitConverter.metersToFeet(location.getAltitude()));
                unitText = getString(R.string.unit_feet);
            } else {
                accuracyText = String.format(Locale.getDefault(), "%,d", (int) (location.getAccuracy()));
                altitudeText = String.format(Locale.getDefault(), "%,d", (int) (location.getAltitude()));
                unitText = getString(R.string.unit_meters);
            }
        }
        accuracyTextView.setText(accuracyText);
        accuracyUnitTextView.setText(unitText);
        altitudeTextView.setText(altitudeText);
        altitudeUnitTextView.setText(unitText);
        latitudeTextView.setText(latitudeText);
        longitudeTextView.setText(longitudeText);
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

    private void saveScreenLockPreference(boolean screenLock) {
        FirebaseCrash.log("Saving screen lock preference as " + screenLock);
        sharedPreferences.edit().putBoolean(getString(R.string.settings_screen_lock_key), screenLock).apply();
    }

    private void setStringPreference(@NonNull String key, @NonNull String value) {
        FirebaseCrash.log("Saving " + key + " preference as " + value);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private void suspendLocationUpdates() {
        FirebaseCrash.log("Suspending location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleAPIClient, this);
    }
}
