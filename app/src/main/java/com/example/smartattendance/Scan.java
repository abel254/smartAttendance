package com.example.smartattendance;

import java.util.Comparator;

public class Scan{
    private String scanResults;
    private String time;
    private String date;
    private String dateDay;

    public Scan() {
    }


    public Scan(String scanResults, String time, String date, String dateDay) {
        this.scanResults = scanResults;
        this.time = time;
        this.date = date;
        this.dateDay = dateDay;
    }

    public String getScanResults() {
        return scanResults;
    }

    public void setScanResults(String scanResults) {
        this.scanResults = scanResults;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

   public static Comparator<Scan> sortedDate = new Comparator<Scan>() {
       @Override
       public int compare(Scan start1, Scan end2) {
           return start1.getDate().compareTo(end2.getDate());
       }
   };
}
