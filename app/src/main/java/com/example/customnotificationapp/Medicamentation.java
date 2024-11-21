package com.example.customnotificationapp;

public class Medicamentation {
    private String medName;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public Medicamentation(String medName, int year, int month, int day, int hour, int minute){
        this.medName = medName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public int getYear() {
        return year;
    }

    public String getMedName() {
        return medName;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getMonth() {
        return month;
    }
}
