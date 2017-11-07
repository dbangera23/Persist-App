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
                "3.0.3\n" +
                        "Bug fixes on Image support. Google photos was throwing an error\n" +
                        "3.0.2\n" +
                        "Bug fixes on image support & text view\n");
        return builder.create();
    }
}
