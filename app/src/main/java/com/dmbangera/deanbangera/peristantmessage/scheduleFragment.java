package com.dmbangera.deanbangera.peristantmessage;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class scheduleFragment extends Fragment {
    private static AlarmManager am;
    private static PendingIntent final_intent;
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
        String scheduledText = settings.getString("scheduled_text", "");
        String scheduleTime = settings.getString("scheduled_time", "");
        if (!scheduledText.isEmpty() && (!scheduleTime.contains(getString(R.string.none)))) {
            ((TextView) rootView.findViewById(R.id.input2)).setText(scheduledText);
            ((TextView) rootView.findViewById(R.id.scheduled_date)).setText(scheduleTime.substring(0, 10));
            ((TextView) rootView.findViewById(R.id.scheduled_time)).setText(scheduleTime.substring(11, 16));
            ((TextView) rootView.findViewById(R.id.schBool)).setText(R.string.schBoolON);
        } else {
            ((TextView) rootView.findViewById(R.id.schBool)).setText(R.string.schBoolOFF);
            ((TextView) rootView.findViewById(R.id.scheduled_date)).setText(R.string.none);
            ((TextView) rootView.findViewById(R.id.scheduled_time)).setText(R.string.none);
        }
        final ImageView dateButton = (ImageView) rootView.findViewById(R.id.schedule_date_set);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = rootView.getContext();
                FragmentManager manager = ((Activity) context).getFragmentManager();
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(manager, "datePicker");
            }
        });
        ImageView timeButton = (ImageView) rootView.findViewById(R.id.schedule_time_set);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = rootView.getContext();
                FragmentManager manager = ((Activity) context).getFragmentManager();
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(manager, "timePicker");
            }
        });
        Button scheduleSetButton = (Button) rootView.findViewById(R.id.schButton);
        scheduleSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set the freaking schedule!
                EditText messageView = (EditText) rootView.findViewById(R.id.input2);
                String text = messageView.getText().toString();
                if (text.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(rootView, R.string.EmptyMess, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(rootView.getContext())) {
                        /** if not construct intent to request permission */
                        Intent intentM = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + rootView.getContext().getPackageName()));
                        intentM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        /** request permission via start activity for result */
                        Toast toast = Toast.makeText(rootView.getContext(), R.string.PermissionMess, Toast.LENGTH_LONG);
                        toast.show();
                        rootView.getContext().startActivity(intentM);
                    }
                }
                TextView dateText = (TextView) rootView.findViewById(R.id.scheduled_date);
                if (dateText.getText().toString().equals(getString(R.string.none))) {
                    Snackbar snackbar = Snackbar.make(rootView, R.string.no_date, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                TextView HourView = (TextView) rootView.findViewById(R.id.scheduled_time);
                if (HourView.getText().toString().equals(getString(R.string.none))) {
                    Snackbar snackbar = Snackbar.make(rootView, R.string.no_time, Snackbar.LENGTH_SHORT);
                    snackbar.show();
                    return;
                }
                Calendar currentDate = Calendar.getInstance();
                Calendar validDate = Calendar.getInstance();
                String date = dateText.getText().toString();
                String time = HourView.getText().toString();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy:hh:mm", Locale.US);
                try{
                    String full = date + ":" + time;
                    validDate.setTime(dateFormat.parse(full));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                if (currentDate.get(Calendar.DATE) == validDate.get(Calendar.DATE)) {
                    if ((currentDate.get(Calendar.HOUR) >= validDate.get(Calendar.HOUR))) {
                        if (currentDate.get(Calendar.MINUTE) > validDate.get(Calendar.MINUTE)) {
                            Snackbar snackbar = Snackbar.make(rootView, R.string.previous_datetime, Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            return;
                        }
                    }
                }
                Context context = rootView.getContext();
                am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, ScheduledMessageRunner.class);
                Bundle b = new Bundle();
                b.putString("Full_Message", text);
                i.putExtras(b);
                final_intent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                am.set(AlarmManager.RTC, validDate.getTimeInMillis(), final_intent);
                SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("scheduled_text", text);
                editor.putString("scheduled_time", date + ":" + time);
                editor.apply();
                Snackbar snackbar = Snackbar.make(rootView, R.string.message_scheduled, Snackbar.LENGTH_SHORT);
                snackbar.show();
                ((TextView) rootView.findViewById(R.id.schBool)).setText(R.string.schBoolON);
            }
        });
        Button scheduleDelButton = (Button) rootView.findViewById(R.id.un_schButton);
        scheduleDelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (am != null) {
                    if (final_intent != null) {
                        am.cancel(final_intent);
                        SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("scheduled_text", "");
                        editor.putString("scheduled_time", "");
                        editor.apply();
                        Snackbar snackbar = Snackbar.make(rootView, R.string.cancelled_message, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        ((TextView) rootView.findViewById(R.id.schBool)).setText(R.string.schBoolOFF);
                        ((TextView) rootView.findViewById(R.id.scheduled_date)).setText(R.string.none);
                        ((TextView) rootView.findViewById(R.id.scheduled_time)).setText(R.string.none);
                    } else {
                        Snackbar snackbar = Snackbar.make(rootView, R.string.none_scheduled, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        final_intent = null;
                    }
                }
            }
        });
        return rootView;
    }

    public static void setFinal_intent(PendingIntent final_intent) {
        scheduleFragment.final_intent = final_intent;
    }

    public static void setAm(AlarmManager am) {
        scheduleFragment.am = am;
    }
}
