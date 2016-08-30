package com.dmbangera.deanbangera.peristantmessage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
            if(mAction.equals(Intent.ACTION_MY_PACKAGE_REPLACED)){
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("changelog",true);
                editor.apply();
            }
            String message = settings.getString("message", "");
            if (!message.isEmpty()) {
                Intent Service = new Intent(context, MessageService.class);
                context.startService(Service);
            }
            String scheduledText = settings.getString("scheduled_text","");
            if(!scheduledText.isEmpty()){
                Intent i = new Intent(context, ScheduledMessageRunner.class);
                Bundle b = new Bundle();
                b.putString("Full_Message", scheduledText);
                i.putExtras(b);
                PendingIntent final_intent = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Calendar validDate = Calendar.getInstance();
                String full = settings.getString("scheduled_time","");
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy:hh:mm", Locale.US);
                try{
                    validDate.setTime(dateFormat.parse(full));
                }catch (ParseException e){
                    e.printStackTrace();
                }
                am.set(AlarmManager.RTC, validDate.getTimeInMillis(), final_intent);
                scheduleFragment.setAm(am);
                scheduleFragment.setFinal_intent(final_intent);
            }
        }
    }
}
