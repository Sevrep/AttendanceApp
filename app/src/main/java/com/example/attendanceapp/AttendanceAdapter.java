package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class AttendanceAdapter extends ArrayAdapter<Attendance> {

    //storing all the names in the list
    private List<Attendance> attendanceList;

    //context object
    private Context context;

    //constructor
    public AttendanceAdapter(Context context, int resource, List<Attendance> attendanceList) {
        super(context, resource, attendanceList);
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //getting the layoutinflater
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //getting listview itmes
        View listViewItem = inflater.inflate(R.layout.activity_view_attendance_list_layout, null, true);
        TextView textViewName = listViewItem.findViewById(R.id.textViewName);
        TextView textViewIn = listViewItem.findViewById(R.id.textViewIn);
        TextView textViewOut = listViewItem.findViewById(R.id.textViewOut);
        TextView textViewDate = listViewItem.findViewById(R.id.textViewDate);
        ImageView imageViewPhotoPath = listViewItem.findViewById(R.id.imageViewPhotoPath);
        ImageView imageViewPhotoPathOut = listViewItem.findViewById(R.id.imageViewPhotoPathOut);
        ImageView imageViewStatus = listViewItem.findViewById(R.id.imageViewStatus);

        //getting the current name
        Attendance name = attendanceList.get(position);
        Attendance timein = attendanceList.get(position);
        Attendance timeout = attendanceList.get(position);
        Attendance date = attendanceList.get(position);
        Attendance photopath = attendanceList.get(position);
        Attendance photopathout = attendanceList.get(position);


        //setting the name to textview
        textViewName.setText(name.getAfullname());
        textViewIn.setText("In: " + timein.getTimein() + " ");
        textViewOut.setText("Out: " + timeout.getTimeout());
        textViewDate.setText("Date: " + date.getDate());

        try {
            Uri uri = Uri.parse(photopath.getPhotopath());
            if (photopath.getPhotopath() != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageViewPhotoPath.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
        }

        try {
            Uri uriOut = Uri.parse(photopathout.getPhotopathout());
            if (photopathout.getPhotopathout() != null) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uriOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageViewPhotoPathOut.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
        }

        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (name.getStatus() == 0)
            imageViewStatus.setBackgroundResource(R.drawable.ic_cloud_queue_black_24dp);
        else
            imageViewStatus.setBackgroundResource(R.drawable.ic_cloud_done_black_24dp);

        return listViewItem;
    }
}