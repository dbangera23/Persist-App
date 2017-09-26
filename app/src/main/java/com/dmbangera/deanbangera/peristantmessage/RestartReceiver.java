package com.dmbangera.deanbangera.peristantmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Dean Bangera on 5/9/2016.
 * Handles the setting of message after boot
 */
public class RestartReceiver extends BroadcastReceiver {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onReceive(Context context, Intent intent) {
        String mAction = intent.getAction();
        if ((mAction.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) || (mAction.equals(Intent.ACTION_BOOT_COMPLETED))) {
            SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            if (mAction.equals(Intent.ACTION_MY_PACKAGE_REPLACED)) {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("changelog", true);
                editor.apply();
            }
            if (!(settings.getString("message", "") + settings.getString("URI", "")).isEmpty()) {
                Intent Service = new Intent(context, setPersistService.class);
                context.startService(Service);
            }
        }
    }
}
