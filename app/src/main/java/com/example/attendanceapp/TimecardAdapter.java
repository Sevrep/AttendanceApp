package com.example.attendanceapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TimecardAdapter extends RecyclerView.Adapter<TimecardAdapter.TimecardViewHolder> {

    private Context mCtx;
    private List<Timecard> timecardList;

    public TimecardAdapter(Context mCtx, List<Timecard> timecardList) {
        this.mCtx = mCtx;
        this.timecardList = timecardList;
    }

    @NonNull
    @Override
    public TimecardAdapter.TimecardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.activity_timecard_list_layout, null);
        return new TimecardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimecardAdapter.TimecardViewHolder timecardViewHolder, int i) {

        String str = timecardList.get(i).getDate();
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(str);
            String formattedDate = new SimpleDateFormat("MMM. dd, yy EEE").format(date);
            timecardViewHolder.tv_date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat formatIn = new SimpleDateFormat("HH:mm:ss");
        String strCurrentIn = timecardList.get(i).getTimein();
        Date newIn = null;
        try {
            newIn = formatIn.parse(strCurrentIn);
            formatIn = new SimpleDateFormat("hh:mm a");
            String formattedIn = formatIn.format(newIn);
            String inFormattedIn = "IN: " + formattedIn;
            timecardViewHolder.tv_in.setText(inFormattedIn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (timecardList.get(i).getTimeout() == timecardList.get(i).getTimein()) {
            String outDotDot = "OUT: --:--:-- --";
            timecardViewHolder.tv_out.setText(outDotDot);
        } else {
            SimpleDateFormat formatOut = new SimpleDateFormat("HH:mm:ss");
            String strCurrentOut = timecardList.get(i).getTimeout();
            Date newOut = null;
            try {
                newOut = formatOut.parse(strCurrentOut);
                formatOut = new SimpleDateFormat("hh:mm a");
                String formattedOut = formatOut.format(newOut);
                String inFormattedOut = "OUT: " + formattedOut;
                timecardViewHolder.tv_out.setText(inFormattedOut);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public int getItemCount() {
        return timecardList.size();
    }

    class TimecardViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_date, tv_in, tv_out;

        public TimecardViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_date = itemView.findViewById(R.id.tv_timecard_date);
            tv_in = itemView.findViewById(R.id.tv_timecard_timein);
            tv_out = itemView.findViewById(R.id.tv_timecard_timeout);
        }
    }

}
