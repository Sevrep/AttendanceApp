package com.example.attendanceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_DATECHECK;
import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_EMPID;
import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_FULLNAME;
import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_TIMESTATUS;

public class CameraActivity extends AppCompatActivity {

    Camera camera;
    FrameLayout frameLayout;
    ShowCamera showCamera;
    Button bt_timein, bt_timeout, bt_absent;
    TextView tv_fullname, tv_date;
    String inout = "", time, date, showDate, showTime, fullname, mCurrentPhotoPath, empid, timestatus, datecheck;
    String uriString;

    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        frameLayout = findViewById(R.id.fl_camera_cameraview);
        tv_fullname = findViewById(R.id.tv_camera_fullname);
        tv_date = findViewById(R.id.tv_camera_date);
        bt_timein = findViewById(R.id.bt_camera_timein);
        bt_timeout = findViewById(R.id.bt_camera_timeout);
        bt_absent = findViewById(R.id.bt_camera_absent);

        camera = Camera.open(); // possible crash if permission not granted
        showCamera = new ShowCamera(this, camera);
        frameLayout.addView(showCamera);

        fullname = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_FULLNAME);
        empid = getIntent().getExtras().getString(EXTRA_EMPID);
        timestatus = getIntent().getExtras().getString(EXTRA_TIMESTATUS);
        datecheck = getIntent().getExtras().getString(EXTRA_DATECHECK);

        time = new SimpleDateFormat("HH:mm:ss", Locale.US).format(Calendar.getInstance().getTime());
        date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        showDate = new SimpleDateFormat("EEEE, d MMMM").format(Calendar.getInstance().getTime());
        showTime = simpleDateFormat.format(calendar.getTime());

        /*
            "yyyy.MM.dd G 'at' HH:mm:ss z" ---- 2001.07.04 AD at 12:08:56 PDT
            "hh 'o''clock' a, zzzz" ----------- 12 o'clock PM, Pacific Daylight Time
            "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ"------- 2001-07-04T12:08:56.235-0700
            "yyMMddHHmmssZ"-------------------- 010704120856-0700
            "K:mm a, z" ----------------------- 0:08 PM, PDT
            "h:mm a" -------------------------- 12:08 PM
            "EEE, MMM d, ''yy" ---------------- Wed, Jul 4, '01
        */

        tv_fullname.setText(fullname);
        tv_date.setText(showDate);

        bt_timein.setEnabled(false);
        bt_timeout.setEnabled(false);
        bt_absent.setEnabled(false);

        // TESTING button control using timestatus
        if (!isNetworkAvailable()) {
            bt_timein.setEnabled(true);
            bt_timeout.setEnabled(true);
            bt_absent.setEnabled(true);
        } else {
            if (date.equals(datecheck)) {
                switch (timestatus) {
                    case "1": // Has time in
                        bt_timein.setVisibility(View.GONE);
                        bt_timein.setEnabled(false);
                        bt_absent.setVisibility(View.GONE);
                        bt_absent.setEnabled(false);
                        bt_timeout.setVisibility(View.VISIBLE); // timeout button available
                        bt_timeout.setEnabled(true);
                        //timestatus = "2";
                        break;
                    case "2": // Has time in and time out
                        bt_timein.setVisibility(View.GONE);
                        bt_timein.setEnabled(false);
                        bt_timeout.setVisibility(View.GONE);
                        bt_timeout.setEnabled(false);
                        bt_absent.setVisibility(View.GONE);
                        bt_absent.setEnabled(false);
                        break;
                    case "3": // Employee is absent
                        bt_timein.setVisibility(View.GONE);
                        bt_timein.setEnabled(false);
                        bt_timeout.setVisibility(View.GONE);
                        bt_timeout.setEnabled(false);
                        bt_absent.setVisibility(View.VISIBLE);
                        bt_absent.setEnabled(false);
                        bt_absent.setText("IS ABSENT");
                        bt_absent.setTextColor(Color.rgb(255, 0, 0));
                        break;
                    default:
                }
            } else { // New day
                bt_timein.setEnabled(true);
                bt_absent.setEnabled(true);
                bt_timeout.setVisibility(View.GONE);
                bt_timeout.setEnabled(false);
                //timestatus = "1";
                datecheck = date;
            }
        }

        // WORKING but app unusable once user change date settings
        /*if (!isNetworkAvailable()) {
            bt_timein.setEnabled(true);
            bt_timeout.setEnabled(true);
            bt_absent.setEnabled(true);
        } else {
            if (date.equals(datecheck)) {
                switch (timestatus) {
                    case "1": // Has time in
                        bt_timein.setVisibility(View.GONE);
                        bt_timein.setEnabled(false);
                        bt_absent.setVisibility(View.GONE);
                        bt_absent.setEnabled(false);
                        bt_timeout.setVisibility(View.VISIBLE);
                        bt_timeout.setEnabled(true);
                        //timestatus = "2";
                        break;
                    case "2": // Has time in and time out
                        bt_timein.setVisibility(View.GONE);
                        bt_timein.setEnabled(false);
                        bt_timeout.setVisibility(View.GONE);
                        bt_timeout.setEnabled(false);
                        bt_absent.setVisibility(View.GONE);
                        bt_absent.setEnabled(false);
                        break;
                    case "3": // Employee is absent
                        bt_timein.setVisibility(View.GONE);
                        bt_timein.setEnabled(false);
                        bt_timeout.setVisibility(View.GONE);
                        bt_timeout.setEnabled(false);
                        bt_absent.setVisibility(View.VISIBLE);
                        bt_absent.setEnabled(false);
                        bt_absent.setText("IS ABSENT");
                        bt_absent.setTextColor(Color.rgb(255, 0, 0));
                        break;
                    default:
                }
            } else { // New day
                bt_timein.setEnabled(true);
                bt_absent.setEnabled(true);
                bt_timeout.setVisibility(View.GONE);
                bt_timeout.setEnabled(false);
                //timestatus = "1";
                datecheck = date;
            }
        }*/

    }

    Camera.PictureCallback mPictureCallBack = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File picture_file = getOutputMediaFile();
            if (picture_file == null) {
                return;
            } else {
                try {
                    FileOutputStream fos = new FileOutputStream(picture_file);
                    fos.write(data);
                    fos.close();
                    goToConfirm();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private File getOutputMediaFile() {
        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED)) {
            return null;
        } else {
            File folder_gui = new File(Environment.getExternalStorageDirectory() + File.separator + "GUI");
            if (!folder_gui.exists()) {
                folder_gui.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = fullname + "_" + timeStamp + "_";
            File outputFile = new File(folder_gui, imageFileName + ".jpg");
            mCurrentPhotoPath = outputFile.getAbsolutePath(); // save this to use in the intent
            uriString = outputFile.getPath(); // uri = Uri.parse(outputFile.getPath());
            return outputFile;
        }
    }

    public void timeinImage(View v) {
        if (camera != null) {
            camera.takePicture(null, null, mPictureCallBack);
            inout = "TIME IN";
        }
        timestatus = "1";
    }

    public void timeoutImage(View v) {
        if (camera != null) {
            camera.takePicture(null, null, mPictureCallBack);
            inout = "TIME OUT";
        }
        timestatus = "2";
    }

    public void goToConfirm() {
        Intent iConfirm = new Intent(CameraActivity.this, ConfirmActivity.class);
        iConfirm.putExtra("fullname", fullname);
        iConfirm.putExtra("empid", empid);
        iConfirm.putExtra("time", time);
        iConfirm.putExtra("date", date);
        iConfirm.putExtra("showTime", showTime);
        iConfirm.putExtra("showDate", showDate);
        iConfirm.putExtra("inout", inout);
        iConfirm.putExtra("timestatus", timestatus);
        iConfirm.putExtra("datecheck", datecheck);
        iConfirm.putExtra("uriString", uriString);
        startActivity(iConfirm);
        CameraActivity.this.finish();
    }

    public void onBackPressed() {
        Intent iRecycler = new Intent(CameraActivity.this, RecyclerviewEmployee.class);
        startActivity(iRecycler);
        CameraActivity.this.finish();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void markAbsent(View view) {
        inout = "ABSENT";
        timestatus = "3";
        Intent iConfirm = new Intent(CameraActivity.this, ConfirmActivity.class);
        iConfirm.putExtra("fullname", fullname);
        iConfirm.putExtra("empid", empid);
        iConfirm.putExtra("time", time);
        iConfirm.putExtra("date", date);
        iConfirm.putExtra("showTime", showTime);
        iConfirm.putExtra("showDate", showDate);
        iConfirm.putExtra("inout", inout);
        iConfirm.putExtra("timestatus", timestatus);
        iConfirm.putExtra("datecheck", datecheck);
        iConfirm.putExtra("uriString", uriString);
        startActivity(iConfirm);
        CameraActivity.this.finish();
    }

}
