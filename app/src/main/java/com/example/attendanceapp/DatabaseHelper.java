package com.example.attendanceapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context c;

    public static final String DB_NAME = "myDB.db";
    public static final String TABLE_ATTENDANCE = "tblAttendance";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMPID = "empid";
    public static final String COLUMN_FULLNAME = "fullname";
    public static final String COLUMN_TIMEIN = "timein";
    public static final String COLUMN_TIMEOUT = "timeout";
    public static final String COLUMN_DAILYHOURS = "dailyhours";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_REMARKS = "remarks";
    public static final String COLUMN_PHOTOPATH = "photopath";
    public static final String COLUMN_PHOTOPATHOUT = "photopathout";
    public static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 6);
        c = context;
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE " + TABLE_ATTENDANCE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_EMPID + " varchar, " +
                    COLUMN_FULLNAME + " varchar, " +
                    COLUMN_TIMEIN + " varchar, " +
                    COLUMN_TIMEOUT + " varchar, " +
                    COLUMN_DAILYHOURS + " varchar, " +
                    COLUMN_DATE + " varchar, " +
                    COLUMN_REMARKS + " varchar, " +
                    COLUMN_PHOTOPATH + " varchar, " +
                    COLUMN_PHOTOPATHOUT + " varchar, " +
                    COLUMN_STATUS + " tinyint)");
            Toast.makeText(c, "Attendance Table Created Successfully ", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("DATABASEHELPER ", "Attendance Table Creation Error ", e);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public void addAttendance(String fullname, String timein, String timeout, String date, String photopath, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("fullname", fullname);
        cv.put("timein", timein);
        cv.put("timeout", timeout);
        cv.put("date", date);
        cv.put("photopath", photopath);
        cv.put("status", status);

        if (db.insert(TABLE_ATTENDANCE, null, cv) > 0) {
            Toast.makeText(c.getApplicationContext(), "Saved.", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public void updateAttendance(String fullname, String timeout, String date, String photopathout, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("fullname", fullname);
        cv.put("timeout", timeout);
        cv.put("date", date);
        cv.put("photopathout", photopathout);
        cv.put("status", status);
        String none = "None";

        if (db.update(TABLE_ATTENDANCE, cv, " fullname = '" + fullname + "' AND date = '" + date + "' AND timeout = '" + none + "' ", null) > 0) {
            Toast.makeText(c.getApplicationContext(), "Saved.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(c.getApplicationContext(), "Data exists or no time in recorded.", Toast.LENGTH_LONG).show();
        }
        db.close();
    }

    /*
     * this method will give us all the name stored in sqlite
     ***/
    public Cursor getAttendance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " ORDER BY " + COLUMN_DATE + " DESC, " + COLUMN_TIMEIN + " DESC;", null);
        c.moveToFirst();
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     */
    public Cursor getUnsyncedAttendance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE status = 0", null);
        return c;
    }

    public boolean checkInsertAttendanceDuplicate(String fullname, String date) {
        boolean res = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " + COLUMN_FULLNAME + " = '" + fullname + "' AND " + COLUMN_DATE + " = '" + date + "' ", null);
        if (c.getCount() != 0) {
            res = true;
        }
        return res;
    }
}