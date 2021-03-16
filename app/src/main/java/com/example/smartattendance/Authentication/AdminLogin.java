package com.example.smartattendance.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smartattendance.AdimModel;
import com.example.smartattendance.AdminMain;
import com.example.smartattendance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminLogin extends AppCompatActivity implements View.OnClickListener {
    Button login;
    EditText usernameEdt, passwordEdt;
    private DatabaseReference reference;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_admin_login);

        usernameEdt = findViewById(R.id.admin_username);
        passwordEdt = findViewById(R.id.admin_password);

        login = findViewById(R.id.login_button);
        login.setOnClickListener(this);

        progressBar = findViewById(R.id.progress_bar);

    }


    @Override
    public void onClick(View v) {
        adminLogin();

    }

    public void adminLogin(){
        String username = usernameEdt.getText().toString().trim();
        final String password = passwordEdt.getText().toString().trim();

        if (username.isEmpty()){
            usernameEdt.setError("username required");
            usernameEdt.requestFocus();
            return;
        }

        if (password.isEmpty()){
            passwordEdt.setError("password required");
            passwordEdt.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEdt.setError("Minimum password length is 6 characters");
            passwordEdt.requestFocus();
            return;
        }

        reference = FirebaseDatabase.getInstance().getReference("AdminCredentials");
        reference.child(username).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String pass = snapshot.child("password").getValue(String.class);
                    if (pass.equals(password)){
                        startActivity(new Intent(AdminLogin.this, AdminMain.class));
                        finish();
                        progressBar.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Wrong username", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkNetwork(){
        boolean WIFI = false;
        boolean MOBILE_DATA = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo info : networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))
                if (info.isConnected())
                    WIFI = true;
                if (info.getTypeName().equalsIgnoreCase("MOBILE_DATA"))
                    if (info.isConnected())
                        MOBILE_DATA = true;
        }
        return WIFI || MOBILE_DATA;
    }
}
