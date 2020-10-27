package com.example.attendanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RecyclerviewEmployee extends AppCompatActivity implements SiteEmployeeAdapter.OnItemClickListener {

    public static final String EXTRA_FULLNAME = "fullname";
    public static final String EXTRA_EMPID = "empid";
    public static final String EXTRA_PHOTO = "photo";
    public static final String EXTRA_TIMESTATUS = "timestatus";
    public static final String EXTRA_DATECHECK = "datecheck";

    List<SiteEmployee> siteEmployeeList;
    RecyclerView recyclerView;
    TextView datetoday, projectsite, oic;
    String date;
    String earlyIn = "07:00:00 AM";
    String lateOut = "06:30:00 PM";
    SiteEmployeeAdapter adapter;

    //Test edittext
    EditText et_early, et_late;

    Button bt_logout;

    SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview_employee);

        SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(this);

        date = new SimpleDateFormat("EEE, d MMM yyyy").format(Calendar.getInstance().getTime());

        datetoday = findViewById(R.id.tv_recyclerview_todayis);
        String todayDate = date;
        datetoday.setText(todayDate);

        projectsite = findViewById(R.id.tv_recyclerview_project);
        String projectProjectsite = "Project: " + sharedPrefHandler.getSharedPref("projectsite");
        projectsite.setText(projectProjectsite);

        oic = findViewById(R.id.tv_recyclerview_oic);
        String oicManager = "OIC: " + sharedPrefHandler.getSharedPref("manager");
        oic.setText(oicManager);

        recyclerView = findViewById(R.id.rv_recyclerview_employeelist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        siteEmployeeList = new ArrayList<>();

        bt_logout = findViewById(R.id.bt_rv_logout);
        if (isNetworkAvailable()) {
            bt_logout.setVisibility(View.VISIBLE);
        } else {
            bt_logout.setVisibility(View.GONE);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadSiteEmployees();
            }
        }, 2000);

        // Test edittext for early/late timechecks
        et_early = findViewById(R.id.test_in);
        et_late = findViewById(R.id.test_out);

    }

    private void loadSiteEmployees() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, sharedPrefHandler.getSharedPref("url_getemployees") + sharedPrefHandler.getSharedPref("projectsite"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loadOnlineList(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadOfflineList();
                    }
                }
        );
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void refreshList() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onItemClick(int position) {
        /* //WORKING app usage time constraints
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");

        String appTime = simpleDateFormat.format(calendar.getTime());

        try {
            Date early = simpleDateFormat.parse(earlyIn);
            Date late = simpleDateFormat.parse(lateOut);
            Date employeeTime = simpleDateFormat.parse(appTime);

            if (employeeTime.compareTo(late) > 0) {
                Toast.makeText(this, "It's already late.", Toast.LENGTH_LONG).show();
            } else if (employeeTime.compareTo(early) < 0) {
                Toast.makeText(this, "It's too early.", Toast.LENGTH_LONG).show();
            } else {
                goToCamera(position);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        goToCamera(position);
    }

    @Override
    public void onMoreClick(int position) {
        goToTimecard(position);
    }

    private void goToTimecard(int position) {
        Intent iTimecard = new Intent(RecyclerviewEmployee.this, TimecardActivity.class);
        SiteEmployee clickedEmployee = siteEmployeeList.get(position);
        iTimecard.putExtra(EXTRA_FULLNAME, clickedEmployee.getName());
        iTimecard.putExtra(EXTRA_EMPID, clickedEmployee.getEmpid());
        iTimecard.putExtra(EXTRA_PHOTO, clickedEmployee.getImage());
        startActivity(iTimecard);
        RecyclerviewEmployee.this.finish();
    }

    public void goToCamera(int position) {
        Intent iCamera = new Intent(RecyclerviewEmployee.this, CameraActivity.class);
        SiteEmployee clickedEmployee = siteEmployeeList.get(position);
        iCamera.putExtra(EXTRA_FULLNAME, clickedEmployee.getName());
        iCamera.putExtra(EXTRA_EMPID, clickedEmployee.getEmpid());
        iCamera.putExtra("timestatus", clickedEmployee.getTimestatus());
        iCamera.putExtra("datecheck", clickedEmployee.getDatecheck());
        startActivity(iCamera);
        RecyclerviewEmployee.this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                SiteEmployeeAdapter adapter = new SiteEmployeeAdapter(RecyclerviewEmployee.this, siteEmployeeList);
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    public void logout(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Logging out?")
                .setMessage("Are you sure you want to logout? (You won't be able to use this app if you are offline.)")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        SharedPrefHandler sharedPrefHandler = new SharedPrefHandler(RecyclerviewEmployee.this);
                        Toast.makeText(getApplicationContext(), "Goodbye " + sharedPrefHandler.getSharedPref("manager") + ".", Toast.LENGTH_LONG).show();
                        sharedPrefHandler.removeSharedPref("projectsite");
                        sharedPrefHandler.removeSharedPref("password");
                        sharedPrefHandler.removeSharedPref("manager");
                        Intent iMain = new Intent(RecyclerviewEmployee.this, MainActivity.class);
                        startActivity(iMain);
                        RecyclerviewEmployee.this.finish();
                    }
                }).create().show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // Test onClick for timechecks
    public void saveTime(View view) {
        earlyIn = et_early.getText().toString();
        lateOut = et_late.getText().toString();

        et_early.setHint(earlyIn);
        et_early.setText("");

        et_late.setHint(lateOut);
        et_late.setText("");

        Toast.makeText(this, "IN: " + earlyIn + "\n" + "OUT: " + lateOut, Toast.LENGTH_LONG).show();
    }

    public void checkLogs(View view) {
        Intent iViewAttendance = new Intent(RecyclerviewEmployee.this, ViewAttendanceActivity.class);
        startActivity(iViewAttendance);
        RecyclerviewEmployee.this.finish();
    }

    public void loadOnlineList(String response) {
        Toast.makeText(getApplicationContext(), "Online employee list.", Toast.LENGTH_LONG).show();
        try {
            JSONArray array = new JSONArray(response);
            sharedPrefHandler.setSharedPref("offlinejsonarray", array.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject employee = array.getJSONObject(i);
                siteEmployeeList.add(new SiteEmployee(
                        employee.getInt("id"),
                        employee.getString("projectsite"),
                        employee.getString("empid"),
                        employee.getString("fullname"),
                        employee.getString("timestatus"),
                        employee.getString("datecheck"),
                        employee.getString("image")
                ));
            }
            adapter = new SiteEmployeeAdapter(RecyclerviewEmployee.this, siteEmployeeList);
            adapter.showShimmer = false;
            recyclerView.setAdapter(adapter);
            SiteEmployeeAdapter.setOnItemClickListener(RecyclerviewEmployee.this);
            refreshList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadOfflineList() {
        Toast.makeText(getApplicationContext(), "OFFLINE. Loading recent employee list.", Toast.LENGTH_LONG).show();
        try {
            String offlineArray = sharedPrefHandler.getSharedPref("offlinejsonarray");
            JSONArray newArray = new JSONArray(offlineArray);

            for (int i = 0; i < newArray.length(); i++) {
                JSONObject employee = newArray.getJSONObject(i);
                siteEmployeeList.add(new SiteEmployee(
                        employee.getInt("id"),
                        employee.getString("projectsite"),
                        employee.getString("empid"),
                        employee.getString("fullname"),
                        employee.getString("timestatus"),
                        employee.getString("datecheck"),
                        employee.getString("image")
                ));
            }

            adapter = new SiteEmployeeAdapter(RecyclerviewEmployee.this, siteEmployeeList);
            adapter.showShimmer = false;
            recyclerView.setAdapter(adapter);
            SiteEmployeeAdapter.setOnItemClickListener(RecyclerviewEmployee.this);
            refreshList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
