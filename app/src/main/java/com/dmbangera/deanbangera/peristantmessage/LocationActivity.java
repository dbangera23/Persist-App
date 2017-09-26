package com.dmbangera.deanbangera.peristantmessage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Dean Bangera on 5/6/2016.
 * Handles Setting of location
 */
public class LocationActivity extends Activity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private TextView textView;
    private ImageView imageView;
    private FrameLayout.LayoutParams params;
    private boolean intentStart = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.location);
        Toast toast = Toast.makeText(this, R.string.LocationMess, Toast.LENGTH_SHORT);
        toast.show();
        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        final Boolean locTextBased = settings.getBoolean("locTextBased", true);
        params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.START);
        if (locTextBased) {
            String message = settings.getString("message", "");
            if (message.isEmpty()) {
                message = "Text";
            }else{
                intentStart = true;
            }
            textView = new TextView(this);
            textView.setText(message);
            textView.setTextColor(Color.parseColor(settings.getString("Color", "Black")));
            String bkd_Color = settings.getString("bkd_Color", "Transparent");
            if (bkd_Color.equals("Transparent")) {
                textView.setBackgroundColor(Color.TRANSPARENT);
            } else {
                textView.setBackgroundColor(Color.parseColor(bkd_Color));
            }
            textView.setTextSize(settings.getInt("SizeSeek", 10));

            textView.setRotation(settings.getInt("RotSeek", 0));
        } else {
            imageView = new ImageView(this);
            String uri = settings.getString("URI", "");
            if (uri.isEmpty()) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_insert_photo_black_24dp));
            } else {
                imageView.setImageURI(Uri.parse(uri));
                intentStart = true;
            }
            float scale = settings.getFloat("image_size", 1.0f);
            params.width = (int) (imageView.getDrawable().getIntrinsicWidth() * scale);
            params.height = (int) (imageView.getDrawable().getIntrinsicHeight() * scale);
            float opacity = settings.getFloat("opacity", 0.0f);
            imageView.setAlpha(opacity);
            int rotation = settings.getInt("RotSeek", 0);
            imageView.setRotation(rotation);
        }


        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);


        final FrameLayout Frame_layout = findViewById(R.id.locationParent);
        if (Frame_layout != null) {
            Frame_layout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        int x = (int) event.getX();// - TSize;
                        int y = (int) event.getY();// - TSize - (yDisplace);
                        if (x < 0)
                            x = 0;
                        if (y < 0)
                            y = 0;

                        params.leftMargin = x;
                        params.topMargin = y;
                        if(locTextBased){
                            textView.setLayoutParams(params);
                            ((FrameLayout) findViewById(R.id.locationParent)).addView(textView, params);
                        }else{
                            imageView.setLayoutParams(params);
                            ((FrameLayout) findViewById(R.id.locationParent)).addView(imageView, params);
                        }
                    } else {
                        if (event.getAction() == MotionEvent.ACTION_MOVE) {
                            int x = (int) event.getX();// - TSize;
                            int y = (int) event.getY();// - TSize - (yDisplace);
                            if (x < 0)
                                x = 0;
                            if (y < 0)
                                y = 0;
                            params.leftMargin = x;
                            params.topMargin = y;
                            if(locTextBased){
                                textView.setLayoutParams(params);
                            }else{
                                imageView.setLayoutParams(params);
                            }
                        } else {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int xFinal = (int) event.getX();// - TSize;
                                int yFinal = (int) event.getY();// - TSize - (yDisplace);
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
                                    Intent i = new Intent(v.getContext(), setPersistService.class);
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