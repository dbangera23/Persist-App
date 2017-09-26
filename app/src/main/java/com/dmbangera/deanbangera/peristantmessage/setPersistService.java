package com.dmbangera.deanbangera.peristantmessage;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Dean Bangera on 5/5/2016.
 * Handles setting of text in screen
 */
public class setPersistService extends Service {
    private static WindowManager windowManager;
    private TextView messageHead;
    private ImageView imageHead;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final int ONGOING_NOTIFICATION_ID = 1;
    private AlarmManager am;
    private PendingIntent final_intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Boolean canDraw = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            MainActivity.askPermission(this);
            stopSelf();
        } else {
            canDraw = true;
        }
        if (canDraw) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            boolean textBased = settings.getBoolean("textBased", true);
            int rotation = settings.getInt("RotSeek", 0);
            if (textBased) {
                messageHead = new TextView(this);

                String text = settings.getString("message", "");
                if (text.isEmpty()) {
                    stopSelf();
                }

                messageHead.setText(text);
                messageHead.setTextColor(Color.parseColor(settings.getString("Color", "Black")));

                messageHead.setRotation(rotation);
                int size = settings.getInt("SizeSeek", 10);
                messageHead.setTextSize(size);
                String bkd_Color = settings.getString("bkd_Color", getString(R.string.transparent));
                if (bkd_Color.equals(getString(R.string.transparent))) {
                    messageHead.setBackgroundColor(Color.TRANSPARENT);
                } else {
                    Spannable spanText = Spannable.Factory.getInstance().newSpannable(text);
                    spanText.setSpan(new BackgroundColorSpan(Color.parseColor(bkd_Color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    messageHead.setText(spanText);
                }
                if (rotation > 0)
                    messageHead.setPadding(size, (int) (size * 3.2), size, (int) (size * 3.2));
            } else {
                imageHead = new ImageView(this);
                imageHead.setImageURI(null);
                //Setting the real returned image.
                String uri = settings.getString("URI", "");
                if (uri.isEmpty())
                    stopSelf();
                imageHead.setImageURI(Uri.parse(uri));
                if (imageHead.getDrawable() == null)
                    stopSelf();
                float opacity = settings.getFloat("opacity", 0.0f);
                imageHead.setAlpha(opacity);
                imageHead.setRotation(rotation);
            }
            WindowManager.LayoutParams params;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        PixelFormat.TRANSLUCENT);
            } else {
                //noinspection deprecation
                params = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                        PixelFormat.TRANSLUCENT);
            }
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = settings.getInt("X", 0);
            params.y = settings.getInt("Y", 100);
            if (textBased) {
                params.width = WindowManager.LayoutParams.WRAP_CONTENT;
                params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                windowManager.addView(messageHead, params);
            } else {
                float scale = settings.getFloat("image_size", 1.0f);
                imageHead.setAdjustViewBounds(true);
                params.width = (int) (imageHead.getDrawable().getIntrinsicWidth() * scale);             //Adjust scale here since params was instantiated here
                params.height = (int) (imageHead.getDrawable().getIntrinsicHeight() * scale);
                windowManager.addView(imageHead, params);
            }

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            String id = "persist_normal_channel";
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, id)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.Message_set))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(resultPendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel(id, getString(R.string.app_name), NotificationManager.IMPORTANCE_MIN);
                channel.setDescription(getString(R.string.Message_set));
                notificationManager.createNotificationChannel(channel);
                channel.setImportance(NotificationManager.IMPORTANCE_MIN);
            }

          /*  Intent deleteIntent = new Intent(getApplicationContext(), DeleteActivity.class);
            PendingIntent deletePendingIntent = PendingIntent.getService(getApplicationContext(), 2, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.addAction(R.drawable.ic_delete_black_24dp, getString(R.string.delete), deletePendingIntent);
            Intent copyIntent = new Intent(getApplicationContext(), CopyActivity.class);
            PendingIntent copyPendingIntent = PendingIntent.getService(getApplicationContext(), 1, copyIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            builder.addAction(R.drawable.ic_content_copy_black_24dp, getString(R.string.copy), copyPendingIntent);*/

            Notification notification = builder.build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            if (settings.getBoolean("burn_in_key", false)) {
                am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(this, ScreenBurnStarter.class);
                final_intent = PendingIntent.getBroadcast(this, 2, i, PendingIntent.FLAG_CANCEL_CURRENT);
                Calendar cal = Calendar.getInstance();
                am.set(AlarmManager.RTC, cal.getTimeInMillis() + 60 * 60 * 1000, final_intent);
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageHead != null) {
            windowManager.removeView(messageHead);
        }
        if (imageHead != null) {
            windowManager.removeView(imageHead);
        }
        if (am != null) {
            am.cancel(final_intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
