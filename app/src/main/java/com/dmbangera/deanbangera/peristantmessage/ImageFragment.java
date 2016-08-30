package com.dmbangera.deanbangera.peristantmessage;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by deanb on 7/26/2016.
 * Fragment to handle images
 */
public class ImageFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_image, container, false);
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (view != null) {
            SeekBar size_seeker = (SeekBar) view.findViewById(R.id.size_seeker);
            if (size_seeker != null) {
                float saved_size = settings.getFloat("ImageSizeSeek", 1);
                final TextView sizeText = (TextView) view.findViewById(R.id.size);
                sizeText.setText(String.format(Locale.getDefault(), "%1f", (float) (saved_size / 10)));
                final ImageView image = (ImageView) view.findViewById(R.id.image_input);
                image.setScaleX(saved_size);
                image.setScaleY(saved_size);
                size_seeker.setMax(50);
                size_seeker.setProgress((int) (saved_size * 10));
                size_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        sizeText.setText(String.format(Locale.getDefault(), "%1f", (float) (i / 10)));
                        image.setScaleX(i / 10);
                        image.setScaleY(i / 10);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
            SeekBar rotation_seeker = (SeekBar) view.findViewById(R.id.rotation_seek);
            if (size_seeker != null) {
                int saved_rot = settings.getInt("ImageRotSeek", 0);
                final TextView rotText = (TextView) view.findViewById(R.id.rotation);
                rotText.setText(String.format(Locale.getDefault(), "%d", saved_rot));
                final ImageView image = (ImageView) view.findViewById(R.id.image_input);
                image.setRotation(saved_rot);
                rotation_seeker.setMax(359);
                rotation_seeker.setProgress(saved_rot);
                rotation_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        rotText.setText(String.format(Locale.getDefault(), "%d", i));
                        image.setRotation(i);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }
        }
        return view;
    }
}
