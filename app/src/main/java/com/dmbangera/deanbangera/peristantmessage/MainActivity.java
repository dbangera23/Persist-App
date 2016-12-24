package com.dmbangera.deanbangera.peristantmessage;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "MyPrefsFile";
    private DrawerLayout mDrawerLayout;
    private android.support.v4.app.FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                int itemFound = item.getItemId();
                if (itemFound == R.id.settings) {
                    Intent i = new Intent(getApplicationContext(), SettingsFragment.class);
                    startActivity(i);
                } else {
                    displayView(itemFound);
                }
                return false;
            }
        });

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name,
                R.string.app_name);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displayView(int itemId) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        switch (itemId) {
            case R.id.nav_message:
                fragmentTransaction.replace(R.id.containerView, new TabFragment()).commit();
                break;
            case R.id.nav_schedule:
                fragmentTransaction.replace(R.id.containerView, new scheduleFragment()).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
