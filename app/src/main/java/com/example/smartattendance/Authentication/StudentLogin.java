package com.example.smartattendance.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartattendance.R;
import com.example.smartattendance.StudentMain;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentLogin extends AppCompatActivity implements View.OnClickListener {
    private TextView register;
    private EditText emailEdt, passwordEdt;
    private Button login;
    String studentfullname;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_student_login);

        register = findViewById(R.id.register);
        register.setOnClickListener(this);

        login = findViewById(R.id.login_button);
        login.setOnClickListener(this);

        emailEdt = findViewById(R.id.email);
        passwordEdt = findViewById(R.id.password);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();

  //fetching fullname from registration
        Intent intent = getIntent();
        studentfullname = intent.getStringExtra("studentfullname");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.register:
                startActivity(new Intent(this, StudentRegistration.class));
                finish();
                break;
            case R.id.login_button:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = emailEdt.getText().toString().trim();
        String password = passwordEdt.getText().toString().trim();

        if (email.isEmpty()){
            emailEdt.setError("Email is required");
            emailEdt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdt.setError("Please enter valid email!");
            emailEdt.requestFocus();
            return;
        }

        if (password.isEmpty()){
            passwordEdt.setError("password is required");
            passwordEdt.requestFocus();
            return;
        }

        if (password.length() < 6){
            passwordEdt.setError("Min password length is 6 characters");
            passwordEdt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()){
                        startActivity(new Intent(StudentLogin.this, StudentMain.class));
                        finish();
                    }else {
                        user.sendEmailVerification();
                        Toast.makeText(StudentLogin.this, "Check your email for verification", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }

                }else {
                    Toast.makeText(StudentLogin.this, "Failed to login! Please check your credentials", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
