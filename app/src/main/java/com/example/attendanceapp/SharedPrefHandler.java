package com.example.attendanceapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHandler {
    public static final String SHAREDPREFSIDENTIFIER = "USER_DEFAULTS";
    private final Context mContext;

    public SharedPrefHandler(Context context) {
        mContext = context;
    }



    public void setSharedPref(String identifier, String value) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.putString(identifier, value);
        editor.commit();
    }

    public String getSharedPref(String identifier) {
        SharedPreferences prefs = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE);
        String restoredText = prefs.getString(identifier, null);
        if (restoredText != null) {
            return restoredText;
        } else return null;
    }



    public void removeSharedPref(String identifier) {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.remove(identifier);
        editor.apply();
    }


    public void clear() {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(SHAREDPREFSIDENTIFIER, mContext.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }

}
