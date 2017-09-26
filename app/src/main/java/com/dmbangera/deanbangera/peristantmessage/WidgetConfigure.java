package com.dmbangera.deanbangera.peristantmessage;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by Dean Bangera on 6/11/2016.
 * Widget configuration
 */
public class WidgetConfigure extends Activity implements AdapterView.OnItemSelectedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.widget_config);
        setResult(RESULT_CANCELED);
        Button WidgetSet = findViewById(R.id.WidgetSet);
        final Spinner Color_spinner = findViewById(R.id.Widget_Txt_Color);
        final Spinner bkd_color_spinner = findViewById(R.id.Widget_bkd_Color);
        final SeekBar size_seeker = findViewById(R.id.Widget_size_seeker);
        WidgetSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = findViewById(R.id.Widget_input);
                String message = input.getText().toString();
                if (message.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, R.string.EmptyMess, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                } else {
                    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
                    Intent intent = getIntent();
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        mAppWidgetId = extras.getInt(
                                AppWidgetManager.EXTRA_APPWIDGET_ID,
                                AppWidgetManager.INVALID_APPWIDGET_ID);
                    }
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(v.getContext());
                    RemoteViews views = new RemoteViews(v.getContext().getPackageName(),
                            R.layout.appwidget);
                    views.setTextViewText(R.id.widgetText, message);
                    views.setTextColor(R.id.widgetText, Color.parseColor((String) Color_spinner.getSelectedItem()));                               //UPDATE
                    views.setTextViewTextSize(R.id.widgetText, TypedValue.COMPLEX_UNIT_SP, size_seeker.getProgress() + 10);
                    String color = (String) bkd_color_spinner.getSelectedItem();
                    if (!color.equals("Transparent"))
                        views.setInt(R.id.widgetText, "setBackgroundColor", Color.parseColor(color));
                    appWidgetManager.updateAppWidget(mAppWidgetId, views);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            }
        });

        if (Color_spinner != null) {
            Color_spinner.setOnItemSelectedListener(this);
            Color_spinner.setSelection(1);
        }

        if (bkd_color_spinner != null) {
            bkd_color_spinner.setOnItemSelectedListener(this);
            bkd_color_spinner.setSelection(0);
        }

        if (size_seeker != null) {
            final TextView sizeText = findViewById(R.id.Widget_size);
            final EditText inputText = findViewById(R.id.Widget_input);
            sizeText.setText(String.format(Locale.getDefault(), "%d", 10));
            inputText.setTextSize(10);
            size_seeker.setMax(90);
            size_seeker.setProgress(0);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if (view != null) {
            EditText ETextView = findViewById(R.id.Widget_input);
            if (parent.getId() == R.id.Widget_bkd_Color) {
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
