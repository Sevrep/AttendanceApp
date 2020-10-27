package com.example.attendanceapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_EMPID;
import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_FULLNAME;
import static com.example.attendanceapp.RecyclerviewEmployee.EXTRA_PHOTO;

public class TimecardActivity extends AppCompatActivity {

    TextView tvfullname, tvempid;
    CircleImageView ivphoto;
    RecyclerView recyclerView;
    List<Timecard> timecardList;
    TimecardAdapter timecardAdapter;
    SharedPrefHandler sharedPrefHandler;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timecard);

        tvfullname = findViewById(R.id.tv_timecard_fullname);
        tvempid = findViewById(R.id.tv_timecard_id);
        ivphoto = findViewById(R.id.iv_timecard_photo);
        recyclerView = findViewById(R.id.rv_timecard);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        timecardList = new ArrayList<>();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadTimecard();
            }
        }, 1500);*/

        String fullname = Objects.requireNonNull(getIntent().getExtras()).getString(EXTRA_FULLNAME);
        String empid = getIntent().getExtras().getString(EXTRA_EMPID);
        String exByte = getIntent().getExtras().getString(EXTRA_PHOTO);

        byte[] decodedString = Base64.decode(exByte, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        tvfullname.setText(fullname);
        tvempid.setText(empid);
        ivphoto.setImageBitmap(decodedByte);

        SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);
        sharedPrefHandler.setSharedPref("empidtimecard", empid);

        loadTimecard();
    }

    private void loadTimecard() {
        sharedPrefHandler = new SharedPrefHandler(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sharedPrefHandler.getSharedPref("url_gettimecard") + sharedPrefHandler.getSharedPref("empidtimecard"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadOnlineList(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loadOfflineList();
                    }
                }
        );
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void loadOnlineList(String response) {
        Toast.makeText(getApplicationContext(), "Online timecard.", Toast.LENGTH_LONG).show();
        try {
            JSONArray array = new JSONArray(response);
            sharedPrefHandler.setSharedPref("offlinetimecard", array.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject timecard = array.getJSONObject(i);
                timecardList.add(new Timecard(
                        timecard.getString("empid"),
                        timecard.getString("timein"),
                        timecard.getString("timeout"),
                        timecard.getString("dailyhours"),
                        timecard.getString("date"),
                        timecard.getString("amonthno")
                ));
            }
            timecardAdapter = new TimecardAdapter(TimecardActivity.this, timecardList);
            recyclerView.setAdapter(timecardAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        Intent iRecycler = new Intent(TimecardActivity.this, RecyclerviewEmployee.class);
        startActivity(iRecycler);
        TimecardActivity.this.finish();
    }

}
