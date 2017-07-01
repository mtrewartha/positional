package io.trewartha.positional;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

import static io.trewartha.positional.common.LocationAwareFragment.REQUEST_CODE_GOOGLE_PLAY_SERVICES;

public class MainActivity extends AppCompatActivity {

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @BindView(R.id.main_view_pager) ViewPager viewPager;
    @BindView(R.id.main_bottom_navigation_view) BottomNavigationView bottomNavigationView;

    private MainFragmentPagerAdapter fragmentPagerAdapter;
    private MenuItem selectedMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);

        fragmentPagerAdapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        viewPager.addOnPageChangeListener(new PageChangeListener());

        bottomNavigationView.setOnNavigationItemSelectedListener(new NavigationItemSelectedListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GOOGLE_PLAY_SERVICES) {
            fragmentPagerAdapter.notifyDataSetChanged();
        }
    }

    private class NavigationItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.main_bottom_navigation_position_menu_item:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.main_bottom_navigation_compass_menu_item:
                    viewPager.setCurrentItem(1);
                    break;
            }
            return true;
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (selectedMenuItem != null) {
                selectedMenuItem.setChecked(false);
            } else {
                bottomNavigationView.getMenu().getItem(0).setChecked(false);
            }

            MenuItem newSelectedMenuItem = bottomNavigationView.getMenu().getItem(position);
            newSelectedMenuItem.setChecked(true);
            selectedMenuItem = newSelectedMenuItem;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
