package com.dmbangera.deanbangera.peristantmessage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Dean Bangera on 5/31/2016.
 * Handle setting up and running the scheduled messages
 */
public class ScheduledMessageRunner extends BroadcastReceiver {
    private static final String PREFS_NAME = "MyPrefsFile";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("message", intent.getStringExtra("Full_Message"));
        editor.putString("scheduled_text", "");
        editor.putString("scheduled_time", "");
        editor.apply();
        Intent i = new Intent(context, MessageService.class);
        scheduleFragment.setFinal_intent(null);
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (settings.getBoolean("notification_bool", false)) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.schedule_m_set))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher));
            notification.setPriority(Notification.PRIORITY_DEFAULT);
            String sound = settings.getString("notification_sound", "None");
            if (!sound.equals("None")) {
                if (sound.equals("Default ringtone")) {
                    notification.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                } else {
                    notification.setSound(Uri.parse(sound));
                }
            }
            if (settings.getBoolean("notification_vibrate", false)) {
                notification.setVibrate(new long[]{0, 1000});
            }
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(2, notification.build());
        }
        context.startService(i);
    }
}
