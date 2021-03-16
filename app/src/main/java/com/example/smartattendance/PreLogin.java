package com.example.smartattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.smartattendance.Authentication.AdminLogin;
import com.example.smartattendance.Authentication.StudentLogin;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PreLogin extends AppCompatActivity {
    Button admin, student;
    AdimModel adminLogin;
    private DatabaseReference reference;
    String username = "admin";
    String password = "admin123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pre_login);

        admin = findViewById(R.id.admin);
        student = findViewById(R.id.student);

        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("AdminCredentials");
                adminLogin = new AdimModel(username, password);
                reference.child(username).setValue(adminLogin);
                startActivity(new Intent(PreLogin.this, AdminLogin.class));

            }
        });
        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PreLogin.this, StudentLogin.class));

            }
        });
    }
}
