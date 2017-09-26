package com.dmbangera.deanbangera.peristantmessage;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screen_size = (getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
        if ((screen_size != Configuration.SCREENLAYOUT_SIZE_LARGE) && (screen_size !=- Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_main_);
        android.support.v4.app.FragmentManager mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.ask_permission)
                        .setMessage(R.string.ask_permission_dialog)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                askPermission(getApplicationContext());
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .show();
            }
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("changelog", false)) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("changelog", false);
            editor.apply();
            FragmentManager fm = getFragmentManager();
            Changelog dialogFragment = new Changelog();
            dialogFragment.show(fm, "");
        }
    }

    static void askPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                /* if not construct intent to request permission */
                Toast toast = Toast.makeText(context.getApplicationContext(), R.string.PermissionMess, Toast.LENGTH_LONG);
                toast.show();
                Intent intentM = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                intentM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /* request permission via start activity for result */
                context.startActivity(intentM);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.about:
                i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;
            case R.id.ShareID:
                i = new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_TEXT, getString(R.string.checkout_app) + "https://play.google.com/store/apps/details?id=com.dmbangera.deanbangera.peristantmessage");
                startActivity(Intent.createChooser(i, getString(R.string.share_prompt)));
                return true;
            case R.id.RateID:
                i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("market://details?id=" + getPackageName()));
                startActivity(i);
                return true;
            case R.id.FeedbackID:
                i = new Intent(Intent.ACTION_SENDTO);
                i.setData(Uri.parse("mailto:"));
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"dmbangeradev@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, R.string.feedback);
                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivity(i);
                }
                return true;
            case R.id.settings:
                i = new Intent(this, SettingsFragment.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
