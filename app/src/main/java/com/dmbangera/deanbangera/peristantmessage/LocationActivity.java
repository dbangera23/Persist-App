package com.dmbangera.deanbangera.peristantmessage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Dean Bangera on 5/6/2016.
 * Handles Setting of location
 */
public class LocationActivity extends Activity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private TextView textview;
    private FrameLayout.LayoutParams params;
    private int TSize;
    private int yDisplace;
    private boolean intentStart = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.location);
        Toast toast = Toast.makeText(getApplicationContext(), R.string.LocationMess, Toast.LENGTH_SHORT);
        toast.show();

        final FrameLayout Frame_layout = (FrameLayout) findViewById(R.id.locationParent);
        if (Frame_layout != null) {
            Frame_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        textview = new TextView(getApplicationContext());
                        String message = settings.getString("message", "");
                        if (message.isEmpty()) {
                            message = "Text";
                        } else {
                            intentStart = true;
                        }
                        textview.setText(message);
                        textview.setTextColor(Color.parseColor(settings.getString("Color", "Black")));
                        String bkd_Color = settings.getString("bkd_Color", "Transparent");
                        if (bkd_Color.equals("Transparent")) {
                            textview.setBackgroundColor(Color.TRANSPARENT);
                        } else {
                            textview.setBackgroundColor(Color.parseColor(bkd_Color));
                        }
                        textview.setTextSize(settings.getInt("SizeSeek", 10));
                        TSize = (int) textview.getTextSize();
                        textview.setRotation(settings.getInt("RotSeek",0));
                        Point size = new Point();
                        getWindowManager().getDefaultDisplay().getSize(size);
                        yDisplace = size.y / 18;
                        int x = (int) event.getX() - TSize;
                        int y = (int) event.getY() - TSize - (yDisplace);
                        if (x < 0)
                            x = 0;
                        if (y < 0)
                            y = 0;
                        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.START);
                        params.leftMargin = x;
                        params.topMargin = y;
                        textview.setLayoutParams(params);
                        ((FrameLayout) findViewById(R.id.locationParent)).addView(textview, params);
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            int x = (int) event.getX() - TSize;
                            int y = (int) event.getY() - TSize - (yDisplace);
                            if (x < 0)
                                x = 0;
                            if (y < 0)
                                y = 0;
                            params.leftMargin = x;
                            params.topMargin = y;
                            textview.setLayoutParams(params);
                        } else {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int xFinal = (int) event.getX() - TSize;
                                int yFinal = (int) event.getY() - TSize - (yDisplace);
                                if (xFinal < 0)
                                    xFinal = 0;
                                if (yFinal < 0)
                                    yFinal = 0;
                                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putInt("X", xFinal);
                                editor.putInt("Y", yFinal);
                                editor.apply();
                                if (intentStart) {
                                    Intent i = new Intent(v.getContext(), MessageService.class);
                                    stopService(i);
                                    startService(i);
                                }
                                finish();
                            }
                        }
                    }
                    return true;
                }
            });
        }
    }
}
