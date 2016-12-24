package com.dmbangera.deanbangera.peristantmessage;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Dean Bangera on 5/28/2016.
 * Handle showing the time picker
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (DateFormat.is24HourFormat(getActivity()))
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    true);
        else
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    false);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.relativeLayout);
        ((TextView) relativeLayout.findViewById(R.id.scheduled_time)).setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
    }
}