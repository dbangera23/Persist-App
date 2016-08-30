package com.dmbangera.deanbangera.peristantmessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by deanb on 6/6/2016.
 * Deals with resetting the message in a different location for dealing with screen burn
 */
public class ScreenBurnStarter extends BroadcastReceiver {
    private static final String PREFS_NAME = "MyPrefsFile";
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        int x;
        if(settings.getBoolean("Burn",false)){
            x = settings.getInt("X", 0) - 100;
            editor.putBoolean("Burn",false);
        }else{
            x = settings.getInt("X", 0) + 100;
            editor.putBoolean("Burn",true);
        }
        if(x<0){
            x=0;
        }
        editor.putInt("X",x);
        editor.apply();
        Intent i = new Intent(context, MessageService.class);
        context.startService(i);
    }
}
