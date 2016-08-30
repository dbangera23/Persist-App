package com.dmbangera.deanbangera.peristantmessage;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
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
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Dean Bangera on 5/5/2016.
 * Handles setting of text in screen
 */
public class MessageService extends Service {
    private WindowManager windowManager;
    private TextView messageHead;
    private boolean canDraw = true;
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final int ONGOING_NOTIFICATION_ID = 1;
    private AlarmManager am;
    private PendingIntent final_intent;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                /** if not construct intent to request permission */
                Intent intentM = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                intentM.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /** request permission via start activity for result */
                canDraw = false;
                Toast toast = Toast.makeText(getApplicationContext(), R.string.PermissionMess, Toast.LENGTH_LONG);
                toast.show();
                startActivity(intentM);
            } else {
                canDraw = true;
            }
        }
        if (canDraw) {
            if (messageHead != null) {
                windowManager.removeView(messageHead);
            }
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            messageHead = new TextView(this);

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String text = settings.getString("message", "");
            if (text.isEmpty()) {
                stopSelf();
            }

            messageHead.setText(text);

            messageHead.setTextColor(Color.parseColor(settings.getString("Color", "Black")));
            int rotation = settings.getInt("RotSeek", 0);
            messageHead.setRotation(rotation);
            int size = settings.getInt("SizeSeek", 10);
            messageHead.setTextSize(size);
            String bkd_Color = settings.getString("bkd_Color", "Transparent");
            if (bkd_Color.equals("Transparent")) {
                messageHead.setBackgroundColor(Color.TRANSPARENT);
            } else {
                Spannable spanText = Spannable.Factory.getInstance().newSpannable(text);
                spanText.setSpan(new BackgroundColorSpan(Color.parseColor(bkd_Color)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                messageHead.setText(spanText);
            }
            if (rotation > 0) {
                messageHead.setPadding(size, (int) (size * 3.2), size, (int) (size * 3.2));
            }
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.START;
            params.x = settings.getInt("X", 0);
            params.y = settings.getInt("Y", 100);
            windowManager.addView(messageHead, params);
            Notification notification = new Notification.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.Message_set))
                    .setSmallIcon(R.drawable.ic_notification)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher))
                    .setPriority(Notification.PRIORITY_MIN)
                    .build();
            startForeground(ONGOING_NOTIFICATION_ID, notification);
            settings = PreferenceManager.getDefaultSharedPreferences(this);
            if (settings.getBoolean("burn_in_key", false)) {
                DisplayMetrics display = getResources().getDisplayMetrics();
                int x = display.widthPixels;
                if ((x + 100) >= settings.getInt("X", 0)) {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("Burn", true);
                    editor.apply();
                }
                am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(this, ScreenBurnStarter.class);
                final_intent = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
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
            if (am != null) {
                am.cancel(final_intent);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
