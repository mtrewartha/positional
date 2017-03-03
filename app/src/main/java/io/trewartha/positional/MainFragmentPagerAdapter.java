package io.trewartha.positional;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.trewartha.positional.compass.CompassFragment;
import io.trewartha.positional.position.PositionFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    public MainFragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PositionFragment();
            case 1:
                return new CompassFragment();
            default:
                return new PositionFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
