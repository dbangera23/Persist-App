package com.dmbangera.deanbangera.peristantmessage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.OnColorSelectedListener;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefsFile";

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_message, container, false);
        final SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        final EditText ETextView = (EditText) view.findViewById(R.id.input);
        final Button Color_button = (Button) view.findViewById(R.id.Txt_Color);
        final Button bkd_color_button = (Button) view.findViewById(R.id.bkd_Color);
        if (ETextView != null) {
            ETextView.setText(settings.getString("message", ""));
            if (Color_button != null) {
                int c = Color.parseColor(settings.getString("Color", "Black"));
                Color_button.setTextColor(c);
                ETextView.setTextColor(c);
                Color_button.setText(String.format("#%06X", (0xFFFFFF & c)));
                Color_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View innerView) {
                        new ChromaDialog.Builder()
                                .initialColor(Color.parseColor(Color_button.getText().toString()))
                                .colorMode(ColorMode.RGB) // RGB, ARGB, HVS, CMYK, CMYK255, HSL
                                .indicatorMode(IndicatorMode.HEX) //HEX or DECIMAL; Note that (HSV || HSL || CMYK) && IndicatorMode.HEX is a bad idea
                                .onColorSelected(new OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(@ColorInt int color) {
                                        Color_button.setTextColor(color);
                                        Color_button.setText(String.format("#%06X", (0xFFFFFF & color)));
                                        ETextView.setTextColor(color);
                                    }
                                })
                                .create()
                                .show(getFragmentManager(), "ChromaDialog");
                    }
                });
            }
            if (bkd_color_button != null) {
                String c = settings.getString("bkd_Color", getString(R.string.transparent));
                if (c.equals(getString(R.string.transparent))) {
                    bkd_color_button.setTextColor(Color.BLACK);
                    ETextView.setBackgroundColor(Color.TRANSPARENT);
                    bkd_color_button.setText(getString(R.string.transparent));
                } else {
                    int backColor = Color.parseColor(c);
                    bkd_color_button.setTextColor(backColor);
                    bkd_color_button.setText(String.format("#%08X", backColor));
                    ETextView.setBackgroundColor(backColor);
                }
                bkd_color_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View innerView) {
                        String temp = bkd_color_button.getText().toString();
                        if (bkd_color_button.getText().toString().equals(getString(R.string.transparent)))
                            temp = "#00000000";
                        new ChromaDialog.Builder()
                                .initialColor(Color.parseColor(temp))
                                .colorMode(ColorMode.ARGB) // RGB, ARGB, HVS, CMYK, CMYK255, HSL
                                .indicatorMode(IndicatorMode.HEX) //HEX or DECIMAL; Note that (HSV || HSL || CMYK) && IndicatorMode.HEX is a bad idea
                                .onColorSelected(new OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(@ColorInt int color) {
                                        if (Color.alpha(color) == 0) {
                                            bkd_color_button.setText(R.string.transparent);
                                            bkd_color_button.setTextColor(Color.BLACK);
                                        } else {
                                            bkd_color_button.setText(String.format("#%08X", color));
                                            bkd_color_button.setTextColor(color);
                                        }
                                        ETextView.setBackgroundColor(color);
                                    }
                                })
                                .create()
                                .show(getFragmentManager(), "ChromaDialog");
                        Toast.makeText(getContext(), R.string.a_0, Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        // Spinner elements
        SeekBar size_seeker = (SeekBar) view.findViewById(R.id.size_seeker);
        if (size_seeker != null) {
            int saved_size = settings.getInt("SizeSeek", 10);
            final TextView sizeText = (TextView) view.findViewById(R.id.size);
            sizeText.setText(String.format(Locale.getDefault(), "%d", saved_size));
            if (ETextView != null) {
                ETextView.setTextSize(saved_size);
                size_seeker.setMax(90);
                size_seeker.setProgress(saved_size - 10);
                size_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sizeText.setText(String.format(Locale.getDefault(), "%d", progress + 10));
                        ETextView.setTextSize(progress + 10);
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
        SeekBar rotation_seeker = (SeekBar) view.findViewById(R.id.rotation_seek);
        if (size_seeker != null) {
            int saved_rot = settings.getInt("RotSeek", 0);
            final TextView rotText = (TextView) view.findViewById(R.id.rotation);
            rotText.setText(String.format(Locale.getDefault(), "%d", saved_rot));
            if (ETextView != null) {
                ETextView.setRotation(saved_rot);
                rotation_seeker.setMax(359);
                rotation_seeker.setProgress(saved_rot);
                rotation_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        rotText.setText(String.format(Locale.getDefault(), "%d", progress));
                        ETextView.setRotation(progress);
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
        Button set_button = (Button) view.findViewById(R.id.message_set_button);
        if (set_button != null) {
            set_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View innerView) {
                    String message = "";
                    if (ETextView != null)
                        message = ETextView.getText().toString();
                    if (message.isEmpty()) {
                        message = settings.getString("message", "");
                    }
                    if (!message.isEmpty()) {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("message", message);
                        SeekBar Size_seeker = (SeekBar) view.findViewById(R.id.size_seeker);
                        if (Size_seeker != null) {
                            editor.putInt("SizeSeek", Size_seeker.getProgress() + 10);
                        }
                        SeekBar rot_seeker = (SeekBar) view.findViewById(R.id.rotation_seek);
                        if (rot_seeker != null) {
                            editor.putInt("RotSeek", rot_seeker.getProgress());
                        }

                        if (Color_button != null) {
                            editor.putString("Color", Color_button.getText().toString());
                        }
                        if (bkd_color_button != null) {
                            editor.putString("bkd_Color", bkd_color_button.getText().toString());
                        }
                        editor.apply();
                        Intent i = new Intent(view.getContext(), MessageService.class);
                        getActivity().startService(i);
                        Snackbar snackbar = Snackbar
                                .make(view, "Message Set", Snackbar.LENGTH_SHORT)
                                .setAction("UNDO", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(view.getContext(), MessageService.class);
                                        getActivity().stopService(intent);
                                        Snackbar snackbar = Snackbar.make(view, R.string.DeleteMess, Snackbar.LENGTH_SHORT);
                                        snackbar.show();
                                    }
                                });

                        snackbar.show();
                    } else {
                        Snackbar snackbar = Snackbar.make(view, R.string.EmptyMess, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                }
            });
        }
        Button loc_button = (Button) view.findViewById(R.id.message_loc_button);
        if (loc_button != null) {
            loc_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View innerView) {
                    Intent intent = new Intent(view.getContext(), LocationActivity.class);
                    startActivity(intent);
                }
            });
        }
        Button del_button = (Button) view.findViewById(R.id.message_del_button);
        if (del_button != null) {
            del_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View innerView) {
                    Intent intent = new Intent(view.getContext(), MessageService.class);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("message", "");
                    editor.apply();
                    getActivity().stopService(intent);
                }
            });
        }

        return view;
    }
}
