package com.dmbangera.deanbangera.peristantmessage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isTablet(this))
            setContentView(R.layout.tablet_view);
        else
            setContentView(R.layout.activity_main);
        //Set main Fragment initially
        messageFragment mf = new messageFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, mf);
        fragmentTransaction.commit();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (!isTablet(this)) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            if (drawer != null) {
                drawer.addDrawerListener(toggle);
            }
            toggle.syncState();
        }
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null)
            navigationView.setNavigationItemSelectedListener(this);
        setTitle(getString(R.string.Text));
    }

    @SuppressWarnings("unused")
    public void setButton(View view) {
        EditText ETextView = (EditText) findViewById(R.id.input);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String message = "";
        if (ETextView != null)
            message = ETextView.getText().toString();
        if (message.isEmpty()) {
            message = settings.getString("message", "");
        }
        if (!message.isEmpty()) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("message", message);
            SeekBar Size_seeker = (SeekBar) findViewById(R.id.size_seeker);
            if (Size_seeker != null) {
                editor.putInt("SizeSeek", Size_seeker.getProgress() + 10);
            }
            SeekBar rot_seeker = (SeekBar) findViewById(R.id.rotation_seek);
            if (rot_seeker != null) {
                editor.putInt("RotSeek", rot_seeker.getProgress());
            }
            Spinner Color_spinner = (Spinner) findViewById(R.id.Txt_Color);
            if (Color_spinner != null) {
                editor.putString("Color", (String) Color_spinner.getSelectedItem());
            }
            Spinner bkd_color_spinner = (Spinner) findViewById(R.id.bkd_Color);
            if (bkd_color_spinner != null) {
                editor.putString("bkd_Color", (String) bkd_color_spinner.getSelectedItem());
            }
            editor.apply();
            Intent i = new Intent(view.getContext(), MessageService.class);
            startService(i);
            Snackbar snackbar = Snackbar
                    .make(view, "Message Set", Snackbar.LENGTH_SHORT)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(view.getContext(), MessageService.class);
                            stopService(intent);
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

    @SuppressWarnings("unused")
    public void locButton(View view) {
        Intent intent = new Intent(view.getContext(), LocationActivity.class);
        startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void delButton(View view) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        Intent intent = new Intent(view.getContext(), MessageService.class);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("message", "");
        editor.apply();
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_message:
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                setTitle(getString(R.string.Text));
                messageFragment mf = new messageFragment();
                fragmentTransaction.replace(R.id.mainFrame, mf);
                fragmentTransaction.commit();
                break;
            case R.id.nav_schedule:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                setTitle(getString(R.string.schedule));
                scheduleFragment sf = new scheduleFragment();
                fragmentTransaction.replace(R.id.mainFrame, sf);
                fragmentTransaction.commit();
                break;
            case R.id.nav_picture:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                setTitle(getString(R.string.images));

                break;
            case R.id.settings:
                // Display the fragment as the main content.
                Intent i = new Intent(this, SettingsFragment.class);
                startActivity(i);
                break;
            default:
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null)
            drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
