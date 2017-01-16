package com.dmbangera.deanbangera.peristantmessage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

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
        FrameLayout imageListener = (FrameLayout) view.findViewById(R.id.image_listener);
        imageListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.on(getActivity(), new PickSetup()).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        ImageView imageView = (ImageView) view.findViewById(R.id.image);

                        //If you want the Bitmap.
                        //  imageView.setImageBitmap(r.getBitmap());

                        //If you want the Uri.
                        //Mandatory to refresh image from Uri.
                        imageView.setImageURI(null);
                        //Setting the real returned image.
                        imageView.setImageURI(r.getUri());
                    }
                });
            }
        });

        return view;
    }

}
