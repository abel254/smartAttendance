package com.example.smartattendance;

public class Scan {
    private String scanResults;

    public Scan() {
    }

    public Scan(String scanResults) {
        this.scanResults = scanResults;
    }

    public String getScanResults() {
        return scanResults;
    }

    public void setScanResults(String scanResults) {
        this.scanResults = scanResults;
    }
}
