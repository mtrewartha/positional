package io.trewartha.positional;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, LocationListener, GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_UPDATE_PRIORITY = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1;
    private static final long LOCATION_UPDATE_INTERVAL = 1000; // ms

    @BindView(R.id.elevation_text_view) TextView elevationTextView;
    @BindView(R.id.elevation_unit_text_view) TextView elevationUnitTextView;
    @BindView(R.id.coordinates_latitude_text_view) TextView latitudeTextView;
    @BindView(R.id.coordinates_longitude_text_view) TextView longitudeTextView;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = LocationRequest.create()
                .setPriority(LOCATION_UPDATE_PRIORITY)
                .setInterval(LOCATION_UPDATE_INTERVAL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "ACCESS_FINE_LOCATION permission granted");
                requestLocationUpdates();
            } else {
                Log.v(TAG, "ACCESS_FINE_LOCATION permission request cancelled");
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
        Log.v(TAG, "Google Play Services connection established");
        requestLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Google Play Services connection suspended");
        suspendLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "Google Play Services connection failed");
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
        elevationTextView.setText(String.format(Locale.getDefault(), "%.1f", location.getAltitude()));
        latitudeTextView.setText(String.format(Locale.getDefault(), "%.5f", location.getLatitude()));
        longitudeTextView.setText(String.format(Locale.getDefault(), "%.5f", location.getLongitude()));
    }

    private void requestAccessFineLocationPermission() {
        Log.v(TAG, "Requesting permission for ACCESS_FINE_LOCATION");
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ACCESS_FINE_LOCATION);
    }

    private void requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "ACCESS_FINE_LOCATION permission is needed");
            requestAccessFineLocationPermission();
        } else {
            Log.v(TAG, "Requesting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    private void suspendLocationUpdates() {
        Log.v(TAG, "Suspending location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }
}
