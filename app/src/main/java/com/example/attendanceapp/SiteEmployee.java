package com.example.attendanceapp;

public class SiteEmployee {
    private int id;
    private String projectsite;
    private String empid;
    private String name;
    private String timestatus;
    private String datecheck;
    private String image;

    public SiteEmployee(int id, String projectsite, String empid, String name, String timestatus, String datecheck, String image) {
        this.id = id;
        this.projectsite = projectsite;
        this.empid = empid;
        this.name = name;
        this.timestatus = timestatus;
        this.datecheck = datecheck;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getProjectsite() {
        return projectsite;
    }

    public String getEmpid() {
        return empid;
    }

    public String getName() {
        return name;
    }

    public String getTimestatus() {
        return timestatus;
    }

    public String getDatecheck() {
        return datecheck;
    }

    public String getImage() {
        return image;
    }
}
