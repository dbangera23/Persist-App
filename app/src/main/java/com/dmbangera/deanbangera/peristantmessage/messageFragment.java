package com.dmbangera.deanbangera.peristantmessage;


import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class messageFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String PREFS_NAME = "MyPrefsFile";

    public messageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_message, container, false);
        SharedPreferences settings = this.getActivity().getSharedPreferences(PREFS_NAME, 0);
        if(settings.getBoolean("changelog",false)){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("changelog",false);
            editor.apply();
            FragmentManager fm = this.getActivity().getFragmentManager();
            Changelog dialogFragment = new Changelog();
            dialogFragment.show(fm, "");
        }
        if (view != null) {
            EditText ETextView = (EditText) view.findViewById(R.id.input);
            if (ETextView != null)
                ETextView.setText(settings.getString("message", ""));
            Spinner Color_spinner = (Spinner) view.findViewById(R.id.Txt_Color);
            if (Color_spinner != null) {
                Color_spinner.setOnItemSelectedListener(this);
                String c = settings.getString("Color", "Black");
                String[] rainbow = getResources().getStringArray(R.array.rainbow);
                for (int walk = 0; walk < rainbow.length; walk++) {
                    if (rainbow[walk].equals(c)) {
                        Color_spinner.setSelection(walk);
                    }
                }
            }
            Spinner bkd_color_spinner = (Spinner) view.findViewById(R.id.bkd_Color);
            if (bkd_color_spinner != null) {
                bkd_color_spinner.setOnItemSelectedListener(this);
                String c = settings.getString("bkd_Color", "Transparent");
                String[] rainbow = getResources().getStringArray(R.array.rainbowBKD);
                for (int walk = 0; walk < rainbow.length; walk++) {
                    if (rainbow[walk].equals(c)) {
                        bkd_color_spinner.setSelection(walk);
                    }
                }
            }
            // Spinner elements
            SeekBar size_seeker = (SeekBar) view.findViewById(R.id.size_seeker);
            if (size_seeker != null) {
                int saved_size = settings.getInt("SizeSeek", 10);
                final TextView sizeText = (TextView) view.findViewById(R.id.size);
                final EditText inputText = (EditText) view.findViewById(R.id.input);
                sizeText.setText(String.format(Locale.getDefault(), "%d", saved_size));
                inputText.setTextSize(saved_size);
                size_seeker.setMax(90);
                size_seeker.setProgress(saved_size - 10);
                size_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sizeText.setText(String.format(Locale.getDefault(), "%d", progress + 10));
                        inputText.setTextSize(progress + 10);
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
                int saved_rot = settings.getInt("RotSeek", 0);
                final TextView rotText = (TextView) view.findViewById(R.id.rotation);
                final EditText inputText = (EditText) view.findViewById(R.id.input);
                rotText.setText(String.format(Locale.getDefault(), "%d", saved_rot));
                inputText.setRotation(saved_rot);
                rotation_seeker.setMax(359);
                rotation_seeker.setProgress(saved_rot);
                rotation_seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        rotText.setText(String.format(Locale.getDefault(), "%d", progress));
                        inputText.setRotation(progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            }
            Button set_button = (Button) view.findViewById(R.id.message_set_button);
            if(set_button!=null){
                set_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
            Button loc_button = (Button) view.findViewById(R.id.message_set_button);
            if(loc_button!=null){
                loc_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
            Button del_button = (Button) view.findViewById(R.id.message_set_button);
            if(del_button!=null){
                del_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });
            }
        }

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        view = getView();
        if (view != null) {
            EditText ETextView = (EditText) view.findViewById(R.id.input);
            if (parent.getId() == R.id.bkd_Color) {
                if (item.equals("Transparent")) {
                    ETextView.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    ETextView.setBackgroundColor(Color.parseColor(item));
                }
            } else {
                ETextView.setTextColor(Color.parseColor(item));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
