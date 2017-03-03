package io.trewartha.positional.position;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CoordinatesFragmentPagerAdapter extends FragmentPagerAdapter {

    public CoordinatesFragmentPagerAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new DegreesDecimalFragment();
            case 1:
                return new DegreesMinutesSecondsFragment();
            case 2:
                return new UTMFragment();
            default:
                return new MGRSFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
