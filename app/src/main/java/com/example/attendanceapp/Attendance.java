package com.example.attendanceapp;

public class Attendance {

    private String afullname;
    private String timein;
    private String timeout;
    private String date;
    private String photopath;
    private String photopathout;
    private int status;

    public Attendance(String afullname, String timein, String timeout, String date, String photopath, String photopathout, int status) {
        this.afullname = afullname;
        this.timein = timein;
        this.timeout = timeout;
        this.date = date;
        this.photopath = photopath;
        this.photopathout = photopathout;
        this.status = status;
    }

    public String getAfullname() {
        return afullname;
    }

    public String getTimein() {
        return timein;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getDate() {
        return date;
    }

    public String getPhotopath() {
        return photopath;
    }

    public String getPhotopathout() {
        return photopathout;
    }

    public int getStatus() {
        return status;
    }

}
