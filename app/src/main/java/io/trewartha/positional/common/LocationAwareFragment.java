package io.trewartha.positional.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import io.trewartha.positional.Log;
import io.trewartha.positional.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static android.support.v4.content.PermissionChecker.checkSelfPermission;

public abstract class LocationAwareFragment extends Fragment implements LocationListener {

    public static final int REQUEST_CODE_GOOGLE_PLAY_SERVICES = 1;
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 2;

    private static final String TAG = LocationAwareFragment.class.getSimpleName();

    private GoogleApiClient googleAPIClient;
    private long locationUpdateInterval = 1000; // ms
    private int locationUpdatePriority = LocationRequest.PRIORITY_HIGH_ACCURACY;

    @Override
    public abstract void onLocationChanged(Location location);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        GoogleConnectionCallbacks googleConnectionCallbacks = new GoogleConnectionCallbacks();
        googleAPIClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(googleConnectionCallbacks)
                .addOnConnectionFailedListener(googleConnectionCallbacks)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        suspendLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (permissions.length > 0 && grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                Log.info(TAG, "Location permissions granted");
                requestLocationUpdates();
            } else {
                Log.info(TAG, "Location permissions request cancelled");
                new AlertDialog.Builder(getContext())
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

    public final void setLocationUpdateInterval(long locationUpdateIntervalMs) {
        this.locationUpdateInterval = locationUpdateIntervalMs;
        requestLocationUpdates();
    }

    public final void setLocationUpdatePriority(int locationUpdatePriority) {
        this.locationUpdatePriority = locationUpdatePriority;
        requestLocationUpdates();
    }

    private void connectToGooglePlayServices() {
        Log.info(TAG, "Connecting to Google Play Services");
        googleAPIClient.connect();
    }

    private boolean haveLocationPermissions() {
        Context context = getContext();
        int coarsePermission = checkSelfPermission(context, ACCESS_COARSE_LOCATION);
        int finePermission = checkSelfPermission(context, ACCESS_FINE_LOCATION);
        return coarsePermission == PERMISSION_GRANTED && finePermission == PERMISSION_GRANTED;
    }

    private void requestLocationPermissions() {
        Log.info(TAG, "Requesting permission for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION");
        requestPermissions(new String[]{
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION,
        }, REQUEST_CODE_LOCATION_PERMISSIONS);
    }

    private void suspendLocationUpdates() {
        Log.info(TAG, "Suspending location updates");
        if (googleAPIClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleAPIClient, this);
        }
    }

    @SuppressLint("MissingPermission")
    private void requestLocationUpdates() {
        if (!haveLocationPermissions()) {
            requestLocationPermissions();
        } else if (!googleAPIClient.isConnected() && !googleAPIClient.isConnecting()) {
            connectToGooglePlayServices();
        } else if (!googleAPIClient.isConnecting()) {
            Log.info(TAG, "Requesting location updates");
            LocationServices.FusedLocationApi.removeLocationUpdates(googleAPIClient, this);
            final LocationRequest locationRequest = LocationRequest.create()
                    .setPriority(locationUpdatePriority)
                    .setInterval(locationUpdateInterval);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleAPIClient, locationRequest, this);
        } else {
            Log.info(TAG, "Location updates will resume once the Google API client is connected");
        }
    }

    private class GoogleConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.info(TAG, "Google Play Services connection established");
            requestLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.info(TAG, "Google Play Services connection suspended");
            suspendLocationUpdates();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.error(TAG, "Google Play Services connection failed (error code " + connectionResult.getErrorCode() + ")");
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(getActivity(), REQUEST_CODE_GOOGLE_PLAY_SERVICES);
                } catch (IntentSender.SendIntentException e) {
                    Log.error(TAG, "There was a fatal exception when trying to connect to the Google Play Services", e);
                    showPlayServicesFatalDialog();
                }
            } else if (connectionResult.getErrorCode() == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                showPlayServicesUpdateDialog();
            } else {
                showPlayServicesFatalDialog();
            }
        }

        private void showPlayServicesFatalDialog() {
            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.google_play_services_connection_failed_title)
                    .setMessage(R.string.google_play_services_connection_failed_message)
                    .setPositiveButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    })
                    .show();
        }

        private void showPlayServicesUpdateDialog() {
            GooglePlayServicesUtil.showErrorDialogFragment(ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED, getActivity(), LocationAwareFragment.this, REQUEST_CODE_GOOGLE_PLAY_SERVICES, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
        }
    }
}
