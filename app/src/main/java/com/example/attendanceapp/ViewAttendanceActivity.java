package com.example.attendanceapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private ListView listViewAttendances;
    private List<Attendance> attendanceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);

        initViews();
        loadAttendances();

    }

    private void initViews() {
        db = new DatabaseHelper(this);
        attendanceList = new ArrayList<>();
        listViewAttendances = findViewById(R.id.lv_viewattendance_attendances);
    }

    private void loadAttendances() {
        attendanceList.clear();
        Cursor cursor = db.getAttendance();
        if (cursor.moveToFirst()) {
            do {
                Attendance attendance = new Attendance(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FULLNAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMEIN)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMEOUT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHOTOPATH)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PHOTOPATHOUT)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS))
                );
                attendanceList.add(attendance);
            } while (cursor.moveToNext());
        }

        AttendanceAdapter attendanceAdapter = new AttendanceAdapter(this, R.layout.activity_view_attendance, attendanceList);
        listViewAttendances.setAdapter(attendanceAdapter);
    }

    @Override
    public void onBackPressed() {
        Intent iViewRecycler = new Intent(ViewAttendanceActivity.this, RecyclerviewEmployee.class);
        startActivity(iViewRecycler);
        ViewAttendanceActivity.this.finish();
    }
}

