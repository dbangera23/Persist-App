package com.dmbangera.deanbangera.peristantmessage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Dean Bangera on 5/28/2016.
 * Handle the showing of Date Picker
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar currentDate = Calendar.getInstance();
        Calendar validDate = Calendar.getInstance();
        validDate.set(year, month, day);
        if (currentDate.after(validDate)) {
            Toast toast = Toast.makeText(view.getContext(), R.string.previous_date, Toast.LENGTH_LONG);
            toast.show();
        } else {
            RelativeLayout relativeLayout = (RelativeLayout) getActivity().findViewById(R.id.relativeLayout);
            ((TextView) relativeLayout.findViewById(R.id.scheduled_date)).setText(String.format(Locale.US, "%tm-%td-%tY", validDate, validDate, validDate));
        }
    }
}