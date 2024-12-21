package com.example.customnotificationapp;

public class Medication {
    private String medName;
    private final int year;
    private final int month;
    private final int day;
    private int hour;
    private int minute;
    private final int id;

    public Medication(String medName, int year, int month, int day, int hour, int minute, int id){
        this.medName = medName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.id = id;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
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

    public int getId() {
        return id;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }
}
