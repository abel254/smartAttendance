package com.example.smartattendance;

public class BitmapDetails {
    int id;
    private String fullname, regnumber, email;
    private byte[] image;

    public BitmapDetails() {
    }

    public BitmapDetails(int id, String fullname, String regnumber,  byte[] image, String email) {
        this.id = id;
        this.fullname = fullname;
        this.regnumber = regnumber;
        this.image = image;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
