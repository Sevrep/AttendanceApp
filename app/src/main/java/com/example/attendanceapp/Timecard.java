package com.example.attendanceapp;

public class Timecard {

    String empid;
    String timein;
    String timeout;
    String dailyhours;
    String date;
    String amonthno;

    public Timecard(String empid, String timein, String timeout, String dailyhours, String date, String amonthno) {
        this.empid = empid;
        this.timein = timein;
        this.timeout = timeout;
        this.dailyhours = dailyhours;
        this.date = date;
        this.amonthno = amonthno;
    }

    public String getEmpid() {
        return empid;
    }

    public String getTimein() {
        return timein;
    }

    public String getTimeout() {
        return timeout;
    }

    public String getDailyhours() {
        return dailyhours;
    }

    public String getDate() {
        return date;
    }

    public String getAmonthno() {
        return amonthno;
    }
}
