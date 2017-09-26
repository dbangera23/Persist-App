package com.dmbangera.deanbangera.peristantmessage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dean Bangera on 5/9/2016.
 * The Fragment to handle the tabbing between messages and images
 */

public class TabFragment extends Fragment {

    private static final int int_items = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.tab_layout, container, false);
        TabLayout tabLayout = x.findViewById(R.id.tabs);
        ViewPager viewPager = x.findViewById(R.id.viewpager);

        /*
         *Set an Adapter for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        /*
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.setupWithViewPager(viewPager);
        return x;

    }

    private class MyAdapter extends FragmentPagerAdapter {

        MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MessageFragment();
                case 1:
                    return new ImageFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return int_items;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.Text);
                case 1:
                    return getString(R.string.image);
            }
            return null;
        }
    }

}