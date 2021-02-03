package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartattendance.Authentication.StudentLogin;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class StudentMain extends AppCompatActivity {
    BitmapDatabase database;
    ArrayList<BitmapDetails> details;
    TextView studentFullName;
    String fullname, regnumber, email;
    byte[] image;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.studenttoolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(StudentMain.this, StudentLogin.class));
                finish();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        studentFullName = findViewById(R.id.student_fullname);

        database = new BitmapDatabase(this, "BitmapDB.sqlite", null, 1);
        details = new ArrayList<>();

        Cursor cursor = database.getData("SELECT * FROM BITMAP");
        details.clear();

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            fullname = cursor.getString(1);
            regnumber = cursor.getString(2);
            image = cursor.getBlob(3);
            email = cursor.getString(4);

            details.add(new BitmapDetails(id, fullname, regnumber, image, email));

        }

        studentFullName.setText(email);
    }
}
