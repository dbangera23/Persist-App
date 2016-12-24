package com.dmbangera.deanbangera.peristantmessage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by Dean Bangera on 5/25/2016.
 * Changelog view
 */
public class Changelog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Changelog");
        builder.setMessage(
                "2.5\n" +
                        "Fixed rotation clipping bug\n" +
                        "Added in android N support\n" +
                        "2.4\n" +
                        "Added widget\n" +
                        "2.3\n" +
                        "Added rotation to text\n" +
                        "Added changelog to be shown\n");
        return builder.create();
    }
}
