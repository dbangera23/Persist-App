package com.dmbangera.deanbangera.peristantmessage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Dean Bangera on 12/23/2016.
 * Fragment to handle the view for image setting
 */

public class ImageFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefsFile";

    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        return view;
    }
}
