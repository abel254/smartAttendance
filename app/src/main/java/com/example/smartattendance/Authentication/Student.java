package com.example.smartattendance.Authentication;

public class Student {
    public String fullname, email, regNumber;

    public Student() {
    }

    public Student(String fullname, String email, String regNumber) {
        this.fullname = fullname;
        this.email = email;
        this.regNumber = regNumber;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
}
