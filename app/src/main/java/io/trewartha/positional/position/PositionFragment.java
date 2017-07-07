package io.trewartha.positional.position;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.trewartha.positional.CoordinatesFormat;
import io.trewartha.positional.Log;
import io.trewartha.positional.R;
import io.trewartha.positional.common.LocationAwareFragment;

public class PositionFragment extends LocationAwareFragment implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = PositionFragment.class.getSimpleName();

    @BindView(R.id.coordinates_view_pager) ViewPager coordinatesViewPager;
    @BindView(R.id.accuracy_value_text_view) TextView accuracyValueTextView;
    @BindView(R.id.accuracy_unit_text_view) TextView accuracyUnitTextView;
    @BindView(R.id.elevation_value_text_view) TextView elevationValueTextView;
    @BindView(R.id.elevation_unit_text_view) TextView elevationUnitTextView;
    @BindView(R.id.speed_value_text_view) TextView speedValueTextView;
    @BindView(R.id.speed_unit_text_view) TextView speedUnitTextView;
    @BindView(R.id.bearing_value_text_view) TextView bearingValueTextView;
    @BindView(R.id.bearing_unit_text_view) TextView bearingUnitTextView;

    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.screen_lock_switch) ImageView screenLockSwitch;
    @BindView(R.id.copy_button) ImageView copyButton;

    private List<CoordinatesFragment> coordinatesFragments;
    private CoordinatesFormat coordinatesFormat;

    private Location location;
    private LocationFormatter locationFormatter;
    private boolean screenLock;
    private SharedPreferences sharedPreferences;
    private boolean useMetricUnits;
    private Unbinder viewUnbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        coordinatesFragments = new LinkedList<>();
        locationFormatter = new LocationFormatter(context);
        sharedPreferences = context.getSharedPreferences(getString(R.string.settings_filename), Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.position_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewUnbinder = ButterKnife.bind(this, view);

        useMetricUnits = sharedPreferences.getBoolean(getString(R.string.settings_metric_units_key), false);
        coordinatesFormat = CoordinatesFormat.valueOf(
                sharedPreferences.getString(getString(R.string.settings_coordinates_format_key), CoordinatesFormat.DMS.name())
        );
        screenLock = sharedPreferences.getBoolean(getString(R.string.settings_screen_lock_key), false);
        screenLockSwitch.setSelected(screenLock);

        final CoordinatesFragmentPagerAdapter coordinatesPagerAdapter = new CoordinatesFragmentPagerAdapter(
                getChildFragmentManager()
        );
        coordinatesViewPager.setAdapter(coordinatesPagerAdapter);
        coordinatesViewPager.setOffscreenPageLimit(coordinatesPagerAdapter.getCount());
        coordinatesViewPager.setCurrentItem(getCoordinatesFragmentIndex(coordinatesFormat), true);
        coordinatesViewPager.addOnPageChangeListener(new CoordinatesPageChangeListener());

        updateLocationViews(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewUnbinder.unbind();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof CoordinatesFragment) {
            coordinatesFragments.add((CoordinatesFragment) fragment);
        }
    }

    @Override
    public void onLocationChanged(@Nullable Location location) {
        this.location = location;
        if (location == null) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            logLocation(location);
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            updateLocationViews(location);
        }
    }

    @OnClick(R.id.copy_button)
    public void onCopyClicked() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.setTitle(R.string.settings_copy_coordinates_title);
        bottomSheetDialog.setContentView(R.layout.coordinates_copy_fragment);

        CoordinatesCopier coordinatesCopier = new CoordinatesCopier(new OnCoordinatesCopiedListener() {
            @Override
            public void onCopy() {
                bottomSheetDialog.dismiss();
            }
        });

        View bothTextView = bottomSheetDialog.findViewById(R.id.coordinates_copy_both_text_view);
        View latitudeTextView = bottomSheetDialog.findViewById(R.id.coordinates_copy_latitude_text_view);
        View longitudeTextView = bottomSheetDialog.findViewById(R.id.coordinates_copy_longitude_text_view);

        if (bothTextView == null || latitudeTextView == null || longitudeTextView == null) {
            Toast.makeText(getContext(), R.string.copied_coordinates_failure, Toast.LENGTH_SHORT).show();
            return;
        }

        bothTextView.setOnClickListener(coordinatesCopier);
        latitudeTextView.setOnClickListener(coordinatesCopier);
        longitudeTextView.setOnClickListener(coordinatesCopier);

        bottomSheetDialog.show();
    }

    @OnClick({R.id.elevation_unit_text_view, R.id.speed_unit_text_view, R.id.accuracy_unit_text_view})
    public void onDistanceUnitClicked() {
        useMetricUnits = !useMetricUnits;
        updateLocationViews(location);
        setBooleanPreference(getString(R.string.settings_metric_units_key), useMetricUnits);
    }

    @OnClick(R.id.screen_lock_switch)
    public void onScreenLockClicked() {
        screenLock = !screenLock;
        screenLockSwitch.setSelected(screenLock);
        setBooleanPreference(getString(R.string.settings_screen_lock_key), screenLock);
        final int textRes = screenLock ? R.string.screen_lock_on : R.string.screen_lock_off;
        Toast.makeText(getContext(), textRes, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        if (compoundButton.getId() == R.id.screen_lock_switch) {
            setBooleanPreference(getString(R.string.settings_screen_lock_key), checked);
            lockScreen(checked);
        }
    }

    private int getCoordinatesFragmentIndex(@NonNull CoordinatesFormat coordinatesFormat) {
        switch (coordinatesFormat) {
            case DECIMAL:
                return 0;
            case DMS:
                return 1;
            case UTM:
                return 2;
            case MGRS:
                return 3;
            default:
                return 0;
        }
    }

    private void lockScreen(boolean lock) {
        if (lock) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
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

    private void updateLocationViews(@Nullable Location location) {
        updateCoordinatesFragments(location);
        accuracyValueTextView.setText(locationFormatter.getAccuracy(location, useMetricUnits));
        accuracyUnitTextView.setText(locationFormatter.getDistanceUnit(useMetricUnits));
        bearingValueTextView.setText(locationFormatter.getBearing(location));
        elevationValueTextView.setText(locationFormatter.getElevation(location, useMetricUnits));
        elevationUnitTextView.setText(locationFormatter.getDistanceUnit(useMetricUnits));
        speedValueTextView.setText(locationFormatter.getSpeed(location, useMetricUnits));
        speedUnitTextView.setText(locationFormatter.getSpeedUnit(useMetricUnits));
    }

    private void setBooleanPreference(@NonNull String key, boolean value) {
        Log.info(TAG, "Saving " + key + " preference as " + value);
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    private void setStringPreference(@NonNull String key, @Nullable String value) {
        Log.info(TAG, "Saving " + key + " preference as " + value);
        sharedPreferences.edit().putString(key, value).apply();
    }

    private void updateCoordinatesFragments(@Nullable Location location) {
        final double latitude, longitude;
        if (location == null) {
            latitude = 0.0;
            longitude = 0.0;
        } else {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        for (CoordinatesFragment coordinatesFragment : coordinatesFragments) {
            coordinatesFragment.setCoordinates(latitude, longitude);
        }
    }

    private class CoordinatesPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Don't do anything here
        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    coordinatesFormat = CoordinatesFormat.DECIMAL;
                    break;
                case 1:
                    coordinatesFormat = CoordinatesFormat.DMS;
                    break;
                case 2:
                    coordinatesFormat = CoordinatesFormat.UTM;
                    break;
                case 3:
                    coordinatesFormat = CoordinatesFormat.MGRS;
                    break;
                default:
                    coordinatesFormat = CoordinatesFormat.DECIMAL;
                    break;
            }
            setStringPreference(getString(R.string.settings_coordinates_format_key), coordinatesFormat.name());
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Don't do anything here
        }
    }

    private class CoordinatesCopier implements View.OnClickListener {

        private OnCoordinatesCopiedListener onCoordinatesCopiedListener;

        CoordinatesCopier(@Nullable OnCoordinatesCopiedListener onCoordinatesCopiedListener) {
            this.onCoordinatesCopiedListener = onCoordinatesCopiedListener;
        }

        @Override
        public void onClick(View v) {
            if (location == null) {
                Toast.makeText(getContext(), R.string.copied_coordinates_failure, Toast.LENGTH_SHORT).show();
                return;
            }

            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            String clipDataLabel = getString(R.string.copied_coordinates_label);
            String clipDataText = "";
            String toastText = "";

            switch (v.getId()) {
                case R.id.coordinates_copy_both_text_view:
                    clipDataText = String.format(Locale.US, "%f, %f", location.getLatitude(), location.getLongitude());
                    toastText = getString(R.string.copied_coordinates_both_success);
                    break;
                case R.id.coordinates_copy_latitude_text_view:
                    clipDataText = String.format(Locale.US, "%f", location.getLatitude());
                    toastText = getString(R.string.copied_coordinates_latitude_success);
                    break;
                case R.id.coordinates_copy_longitude_text_view:
                    clipDataText = String.format(Locale.US, "%f", location.getLongitude());
                    toastText = getString(R.string.copied_coordinates_longitude_success);
                    break;
            }

            ClipData coordinatesClipData = ClipData.newPlainText(clipDataLabel, clipDataText);
            clipboardManager.setPrimaryClip(coordinatesClipData);

            Toast.makeText(getContext(), toastText, Toast.LENGTH_SHORT).show();

            if (onCoordinatesCopiedListener != null) {
                onCoordinatesCopiedListener.onCopy();
            }
        }
    }

    private interface OnCoordinatesCopiedListener {
        void onCopy();
    }
}
