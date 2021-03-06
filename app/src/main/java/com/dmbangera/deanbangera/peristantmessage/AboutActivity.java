package com.dmbangera.deanbangera.peristantmessage;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Dean Bangera on 5/9/2016.
 * The activity to show the about page and anything related
 */
public class AboutActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView verView = findViewById(R.id.VersionName);
        if (verView != null)
            verView.setText(BuildConfig.VERSION_NAME);
        Button aboutDemo =  findViewById(R.id.about_Demo);
        if (aboutDemo != null) {
            SpannableString content = new SpannableString(getString(R.string.about_demo));
            content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
            aboutDemo.setText(content);
            aboutDemo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/f4i4-5mmtng")));
                }
            });
        }
        Button changelog = findViewById(R.id.about_changelog);
        if (changelog != null) {
            changelog.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    FragmentManager fm = getFragmentManager();
                    Changelog dialogFragment = new Changelog();
                    dialogFragment.show(fm, "");
                }
            });
        }
    }
}
