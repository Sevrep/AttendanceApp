package com.example.attendanceapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfirmActivity extends AppCompatActivity {

    //database helper object
    private DatabaseHelper db;

    //View objects
    private ListView listViewAttendances;

    //List to store all the names
    private List<Attendance> attendanceList;

    //adapterobject for list view
    private AttendanceAdapter attendanceAdapter;

    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    private TextView tv_inout, tv_fullname, tv_time, tv_date, tv_empid;
    private ImageView iv_preview;
    String attendancePhotoPath, inout, mCurrentPhotoPath, fullname, timein, timeout, date, showTime, showDate, time, empid, timestatus, datecheck;
    Uri uri;

    String aYear, aMonth, aMonthNo;
    String pDay = "", pMonth = "", pYear = "", pDate = "", pMonthWord = "";
    String day, month, year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        tv_inout = findViewById(R.id.tv_confirm_inout);
        iv_preview = findViewById(R.id.iv_confirm_preview);
        tv_fullname = findViewById(R.id.tv_confirm_fullname);
        tv_time = findViewById(R.id.tv_confirm_time);
        tv_date = findViewById(R.id.tv_confirm_date);
        tv_empid = findViewById(R.id.tv_confirm_empid);

        inout = getIntent().getExtras().getString("inout");
        fullname = getIntent().getExtras().getString("fullname");
        empid = getIntent().getExtras().getString("empid");
        date = getIntent().getExtras().getString("date");
        timestatus = getIntent().getExtras().getString("timestatus");
        datecheck = getIntent().getExtras().getString("datecheck");
        mCurrentPhotoPath = "file:///" + getIntent().getStringExtra("uriString");
        showTime = getIntent().getExtras().getString("showTime");
        showDate = getIntent().getExtras().getString("showDate");

        if (inout.equals("TIME IN")) {
            timein = getIntent().getExtras().getString("time");
            time = timein;
        }
        if (inout.equals("TIME OUT")) {
            timeout = getIntent().getExtras().getString("time");
            time = timeout;
        }
        if (inout.equals("ABSENT")) {
            timein = getIntent().getExtras().getString("time");
            time = timein;
        }

        /*tv_time.setText(showTime);
        tv_inout.setText(inout);*/
        tv_fullname.setText(fullname);
        tv_date.setText(showDate);
        tv_empid.setText(empid);
        //tv_image.setText(mCurrentPhotoPath);

        if (timestatus.equals("3")) {
            tv_time.setText(inout); // show absent in large font
            tv_inout.setText(""); // inout text will be blank
        } else {
            tv_time.setText(showTime);
            tv_inout.setText(inout);
        }

        uri = Uri.parse(mCurrentPhotoPath);
        iv_preview.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPhotoPath != null) {
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    iv_preview.setImageBitmap(bitmap);
                }
            }
        });

        db = new DatabaseHelper(this);
        //attendanceList = new ArrayList<>();

        //listViewAttendances = findViewById(R.id.lv_confirm_attendances);

        //calling the method to load all the stored names
        //loadAttendances();

        aYear = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());
        aMonth = new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime());
        aMonthNo = new SimpleDateFormat("MM").format(Calendar.getInstance().getTime());

        day = new SimpleDateFormat("d").format(Calendar.getInstance().getTime());
        month = new SimpleDateFormat("M").format(Calendar.getInstance().getTime());
        year = new SimpleDateFormat("yyyy").format(Calendar.getInstance().getTime());

        int intDay = Integer.parseInt(day);
        int intMonth = Integer.parseInt(month);
        int intYear = Integer.parseInt(year);

        if ((intDay > 10) && (intDay < 26)) {
            pDay = "30";
            pMonth = new SimpleDateFormat("MM").format(Calendar.getInstance().getTime());
            pYear = year;
            pMonthWord = new DateFormatSymbols().getMonths()[Integer.parseInt(pMonth) - 1];
            pDate = pYear + "-" + pMonth + "-" + pDay;
        } else {
            pDay = "15";
            if ((intDay > 25) && (intDay < 32)) {
                if (intMonth == 12) {
                    pMonth = "01";
                    pMonthWord = new DateFormatSymbols().getMonths()[Integer.parseInt(pMonth) - 1];
                    pYear = Integer.toString(intYear + 1);
                } else {
                    int monthPlus = intMonth + 1;
                    if ((monthPlus > 0) && (monthPlus < 10)) {
                        pMonth = "0" + monthPlus;
                    } else {
                        pMonth = Integer.toString(monthPlus);
                    }
                    pYear = year;
                    pMonthWord = new DateFormatSymbols().getMonths()[monthPlus];
                }
            } else {
                pMonth = new SimpleDateFormat("MM").format(Calendar.getInstance().getTime());
                pYear = year;
                pMonthWord = new DateFormatSymbols().getMonths()[Integer.parseInt(pMonth) - 1];
            }
            pDate = pYear + "-" + pMonth + "-" + pDay;
        }

    }

    public void yesItIs(View v) {
        saveNameToServer();
    }

    private void saveNameToLocalStorage(String fullname, String timein, String timeout, String date, String photopath, int status) {
        if (inout.equals("TIME IN")) {
            timeout = "None";
        }

        if (timein == null) {
            timein = "None";
        }

        if (timestatus.equals("3")) {
            timein = "Absent";
            timeout = "Absent";
        }

        if (db.checkInsertAttendanceDuplicate(fullname, date)) {
            Toast.makeText(getApplicationContext(), fullname + " has already timed in this " + date, Toast.LENGTH_LONG).show();
        } else {
            db.addAttendance(fullname, timein, timeout, date, photopath, status);
        }
    }

    private void updateNameToLocalStorage(String fullname, String timeout, String date, String photopathout, int status) {
        db.updateAttendance(fullname, timeout, date, photopathout, status);
    }

    public void saveNameToServer() {
        SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                sharedPrefHandler.getSharedPref("url_timein"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) { // if there is a success
                                if (inout.equals("TIME IN") || inout.equals("ABSENT")) { // storing the name to sqlite with status synced
                                    //saveNameToLocalStorage(fullname, timein, timeout, date, mCurrentPhotoPath, NAME_SYNCED_WITH_SERVER);
                                    saveNameToLocalStorage(
                                            fullname,
                                            jsonObject.getString("timeph"),
                                            timeout,
                                            jsonObject.getString("dateph"),
                                            mCurrentPhotoPath,
                                            NAME_SYNCED_WITH_SERVER);
                                } else if (inout.equals("TIME OUT")) {
                                    //updateNameToLocalStorage(fullname, timeout, date, mCurrentPhotoPath, NAME_SYNCED_WITH_SERVER);
                                    updateNameToLocalStorage(
                                            fullname,
                                            jsonObject.getString("timeph"),
                                            jsonObject.getString("dateph"),
                                            mCurrentPhotoPath,
                                            NAME_SYNCED_WITH_SERVER);
                                }
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                                goToEmployeeList();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        jsonObject.getString("message"),
                                        Toast.LENGTH_LONG).show();
                                new AlertDialog.Builder(ConfirmActivity.this)
                                        .setTitle("Error")
                                        .setMessage(jsonObject.getString("message"))
                                        .setCancelable(false)
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                goToEmployeeList();
                                            }
                                        })
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (inout.equals("TIME IN") || inout.equals("ABSENT")) { // storing the name to sqlite with status unsynced
                            saveNameToLocalStorage(
                                    fullname,
                                    timein,
                                    timeout,
                                    date,
                                    mCurrentPhotoPath,
                                    NAME_NOT_SYNCED_WITH_SERVER);
                        } else if (inout.equals("TIME OUT")) {
                            updateNameToLocalStorage(
                                    fullname,
                                    timeout,
                                    date,
                                    mCurrentPhotoPath,
                                    NAME_NOT_SYNCED_WITH_SERVER);
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("fullname", fullname);
                params.put("empid", empid);

                if (inout.equals("TIME IN")) {
                    params.put("timein", time);
                }
                if (inout.equals("TIME OUT")) {
                    params.put("timeout", time);
                }
                params.put("date", date);

                params.put("timestatus", timestatus);
                params.put("datecheck", datecheck);

                // added month year monthno
                params.put("ayear", aYear);
                params.put("amonth", aMonth);
                params.put("amonthno", aMonthNo);

                // added month year paydate
                params.put("pdate", pDate);
                params.put("pyear", pYear);
                params.put("pmonth", pMonthWord);
                params.put("pmonthno", pMonth);

                // added absent option
                if (inout.equals("ABSENT")) {
                    params.put("timein", time);
                }

                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void onBackPressed() {
        Intent iRecycler = new Intent(ConfirmActivity.this, RecyclerviewEmployee.class);
        startActivity(iRecycler);
        ConfirmActivity.this.finish();
    }

    public void goToEmployeeList() {
        Intent iRecycler = new Intent(ConfirmActivity.this, RecyclerviewEmployee.class);
        startActivity(iRecycler);
        ConfirmActivity.this.finish();
    }




}
