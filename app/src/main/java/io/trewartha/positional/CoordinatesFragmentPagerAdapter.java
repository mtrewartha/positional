package io.trewartha.positional;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CoordinatesFragmentPagerAdapter extends FragmentPagerAdapter {

    @NonNull private Fragment[] fragments;

    public CoordinatesFragmentPagerAdapter(
            @NonNull FragmentManager fragmentManager,
            @NonNull Fragment[] fragments
    ) {
        super(fragmentManager);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
