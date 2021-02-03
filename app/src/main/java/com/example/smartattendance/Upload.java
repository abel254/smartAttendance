package com.example.smartattendance;

public class Upload {
    private String fullname;
    private String regnumber;
    private String mImageUrl;

    public Upload() {
    }

    public Upload(String fullname, String regnumber, String mImageUrl) {
        this.fullname = fullname;
        this.regnumber = regnumber;
        this.mImageUrl = mImageUrl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRegnumber() {
        return regnumber;
    }

    public void setRegnumber(String regnumber) {
        this.regnumber = regnumber;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }


}
