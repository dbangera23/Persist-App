package com.dmbangera.deanbangera.peristantmessage;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vansuita.pickimage.PickImageDialog;
import com.vansuita.pickimage.PickSetup;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.listeners.IPickResult;

import java.util.Locale;

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
        final ImageView EImage = view.findViewById(R.id.image);
        EImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        EImage.setAdjustViewBounds(true);
        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        FrameLayout imageListener = view.findViewById(R.id.image_listener);
        String savedURI = settings.getString("URI", "");
        if (!savedURI.isEmpty()) {
            EImage.setImageURI(Uri.parse(savedURI));
        }


        final SeekBar opacity_seek = view.findViewById(R.id.opacity_seek);
        if (opacity_seek != null) {
            opacity_seek.setMax(100);
            float saved_opacity = settings.getFloat("image_trans", 1f);
            opacity_seek.setProgress((int) saved_opacity * 100);
            final TextView opacityText = view.findViewById(R.id.opacity);
            opacityText.setText(String.format(Locale.getDefault(), "%.02f", saved_opacity));
            EImage.setAlpha(saved_opacity);
            opacity_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float opacity = (float) (progress / 100.00);
                    opacityText.setText(String.format(Locale.getDefault(), "%.02f", opacity));
                    EImage.setAlpha(opacity);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        final SeekBar size_seek = view.findViewById(R.id.size_seeker);
        if (size_seek != null) {
            size_seek.setMax(19);
            float saved_size = settings.getFloat("image_size", .9f);
            if (saved_size == 2.0f)
                saved_size = 1.9f;
            size_seek.setProgress((int) (saved_size * 10));
            changeImageViewSize(EImage, saved_size);
            final TextView sizeText = view.findViewById(R.id.size);
            sizeText.setText(String.format(Locale.getDefault(), "%.01f", saved_size + .1f));
            size_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    float progressSet = (float) ((progress / 10.0) + .1f);
                    sizeText.setText(String.format(Locale.getDefault(), "%.01f", progressSet));
                    changeImageViewSize(EImage, progressSet);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        imageListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickImageDialog.on(getActivity(), new PickSetup()).setOnPickResult(new IPickResult() {
                    @Override
                    public void onPickResult(PickResult r) {
                        //Mandatory to refresh image from Uri.
                        EImage.setImageURI(null);
                        //Setting the real returned image.
                        Uri uri = r.getUri();
                        EImage.setImageURI(uri);
                        changeImageViewSize(EImage, 1);
                        TextView sizeText = view.findViewById(R.id.size);
                        sizeText.setText(String.format(Locale.getDefault(), "%.01f", 1.0f));
                        if (size_seek != null) {
                            size_seek.setProgress(9);
                        }
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("URI", uri.toString());
                        editor.apply();
                    }
                });
            }
        });

        SeekBar rot_seek = view.findViewById(R.id.rotation_seek);
        if (rot_seek != null) {
            rot_seek.setMax(359);
            int saved_rot = settings.getInt("image_rot", 0);
            rot_seek.setProgress(saved_rot);
            final TextView rotText = view.findViewById(R.id.rotation);
            rotText.setText(String.format(Locale.getDefault(), "%d", saved_rot));
            EImage.setRotation(saved_rot);
            rot_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    rotText.setText(String.format(Locale.getDefault(), "%d", progress));
                    EImage.setRotation(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }

        Button image_set_button = view.findViewById(R.id.image_set_button);
        image_set_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EImage.getDrawable() != null) {
                    SharedPreferences.Editor editor = settings.edit();
                    TextView opacityText = view.findViewById(R.id.opacity);
                    editor.putFloat("opacity", Float.parseFloat(opacityText.getText().toString()));

                    TextView scaleText = view.findViewById(R.id.size);
                    editor.putFloat("image_size", Float.parseFloat(scaleText.getText().toString()));

                    TextView rotation = view.findViewById(R.id.rotation);
                    editor.putInt("RotSeek", Integer.parseInt(rotation.getText().toString()));

                    editor.putBoolean("textBased", false);
                    editor.apply();

                    Intent i = new Intent(view.getContext(), setPersistService.class);
                    getActivity().stopService(i);
                    getActivity().startService(i);
                    Snackbar snackbar = Snackbar
                            .make(view, getString(R.string.SetImage), Snackbar.LENGTH_SHORT)
                            .setAction(getString(R.string.undo), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(view.getContext(), setPersistService.class);
                                    getActivity().stopService(intent);
                                    Snackbar snackbar = Snackbar.make(view, R.string.DeleteImage, Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });

                    snackbar.show();
                } else {
                    Snackbar snackbar = Snackbar.make(view, R.string.EmptyImage, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });
        Button image_loc_button = view.findViewById(R.id.image_loc_button);
        image_loc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("locTextBased", false);
                editor.apply();
                Intent intent = new Intent(view.getContext(), LocationActivity.class);
                startActivity(intent);
            }
        });
        Button image_del_button = view.findViewById(R.id.image_del_button);
        image_del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EImage.setImageResource(0);
                ViewGroup.LayoutParams params = EImage.getLayoutParams();
                params.width = EImage.getBackground().getIntrinsicWidth();
                params.height = EImage.getBackground().getIntrinsicWidth();
                EImage.setLayoutParams(params);
                Intent intent = new Intent(view.getContext(), setPersistService.class);
                getActivity().stopService(intent);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("URI", "");
                editor.apply();
            }
        });
        return view;
    }

    private void changeImageViewSize(ImageView EImage, float saved_size) {
        Drawable d = EImage.getDrawable();
        if (d != null) {
            ViewGroup.LayoutParams params = EImage.getLayoutParams();
            params.width = (int) (EImage.getDrawable().getIntrinsicWidth() * saved_size);
            params.height = (int) (EImage.getDrawable().getIntrinsicHeight() * saved_size);
            EImage.setLayoutParams(params);
        }
    }
}
