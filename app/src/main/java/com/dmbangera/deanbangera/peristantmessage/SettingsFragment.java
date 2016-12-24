package com.dmbangera.deanbangera.peristantmessage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Dean Bangera on 6/6/2016.
 * Fragment to deal with preferences/settings
 */
public class SettingsFragment extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private static final String PREFS_NAME = "MyPrefsFile";

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("burn_in_key")) {
                if (sharedPreferences.getBoolean(key, false)) {
                    Context context = getActivity().getApplicationContext();
                    SharedPreferences setting = context.getSharedPreferences(PREFS_NAME, 0);
                    if (!setting.getString("message", "").isEmpty()) {
                        Intent i = new Intent(context, MessageService.class);
                        context.startService(i);
                    }
                }
            }
            if (key.equals("notification_vibrate")) {
                if (sharedPreferences.getBoolean(key, false)) {
                    if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                            Manifest.permission.VIBRATE) == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.VIBRATE},
                                0);
                    }
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();
        }
    }
}
