package com.example.smartattendance.Authentication;

public class StudentDetails {
    public String idNuM, fullname, regnumber, email, password;

    public StudentDetails() {
    }

    public StudentDetails(String idNuM, String fullname, String regnumber, String email, String password) {
        this.idNuM = idNuM;
        this.fullname = fullname;
        this.regnumber = regnumber;
        this.email = email;
        this.password = password;
    }

    public String getIdNuM() {
        return idNuM;
    }

    public void setIdNuM(String idNuM) {
        this.idNuM = idNuM;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
