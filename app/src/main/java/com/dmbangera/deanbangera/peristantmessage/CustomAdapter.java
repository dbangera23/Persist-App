package com.dmbangera.deanbangera.peristantmessage;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Dean Bangera on 12/25/2016.
 * Adapter to handle the recyclerView in scheduling
 */

class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private static Context mContext;

    private List<String> mDataSet;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cardTitle;
        private final TextView cardDate;
        private final TextView cardTime;

        ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            cardTitle = (TextView) v.findViewById(R.id.card_title);
            cardDate = (TextView) v.findViewById(R.id.card_date);
            cardDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = ((Activity) mContext).getFragmentManager();
                    DatePickerFragment datePicker = new DatePickerFragment();
                    datePicker.show(manager, "showDate");
                }
            });
            cardTime = (TextView) v.findViewById(R.id.card_time);
            cardTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager manager = ((Activity) mContext).getFragmentManager();
                    DialogFragment newFragment = new TimePickerFragment();
                    newFragment.show(manager, "timePicker");
                }
            });


        }

        TextView getCardTitle() {
            return cardTitle;
        }

        TextView getCardDate() {
            return cardDate;
        }

        TextView getCardTime() {
            return cardTime;
        }
    }

    /**
     * Initialize the data set of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    CustomAdapter(List<String> dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.schedule_row_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your data set at this position and replace the contents of the view
        // with that element
        viewHolder.getCardTitle().setText(mDataSet.get(position));
        viewHolder.getCardDate().setText(DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()));
        viewHolder.getCardTime().setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date()));
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }
}