package com.example.smartattendance.Authentication;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartattendance.BitmapDatabase;
import com.example.smartattendance.R;
import com.example.smartattendance.Upload;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;

public class StudentRegistration extends AppCompatActivity implements View.OnClickListener {
    StudentDetails member;
    String combined_details;
    String fullname, regNumber, studentEmail, studentPassword;
    private EditText fullnameEdt, studentEmailEdt, studentPasswordEdt, regNumberEdt;
    private Button signUpButton;
    private ProgressBar progressBar;
    private TextView registerLoginTv;
    private long maxid = 0;
    private DatabaseReference reff, mDatabaseRef;
    private StorageReference mStorageRef;
    ImageView imageView;
    private Bitmap bitmap;

    private FirebaseAuth mAuth;
    private BitmapDatabase mySqlBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_student_registration);

        mAuth = FirebaseAuth.getInstance();

        signUpButton = findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(this);

        registerLoginTv = findViewById(R.id.register_login);
        registerLoginTv.setOnClickListener(this);

        fullnameEdt = findViewById(R.id.fullname);
        studentEmailEdt = findViewById(R.id.student_email);
        studentPasswordEdt = findViewById(R.id.student_password);
        regNumberEdt = findViewById(R.id.reg_number);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.bitmap_image);

       mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mySqlBitmap = new BitmapDatabase(this, "BitmapDB.sqlite", null, 1);
        mySqlBitmap.queryData("CREATE TABLE IF NOT EXISTS BITMAP (id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, reg VARCHAR, email VARCHAR, image BLOG)");

        member = new StudentDetails();

        reff = FirebaseDatabase.getInstance().getReference().child("Member");
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    maxid = (snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_login:
                Intent intent = new Intent(StudentRegistration.this, StudentLogin.class);
                intent.putExtra("studentfullname", fullname);
                startActivity(intent);
                finish();
                break;

            case R.id.signup_button:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        fullname = fullnameEdt.getText().toString().trim();
        studentEmail = studentEmailEdt.getText().toString().trim();
        studentPassword = studentPasswordEdt.getText().toString().trim();
        regNumber = regNumberEdt.getText().toString().trim();

        combined_details = fullname + " " + regNumber;

        if (fullname.isEmpty()) {
            fullnameEdt.setError("Full name is required");
            fullnameEdt.requestFocus();
            return;
        }

        if (regNumber.isEmpty()) {
            regNumberEdt.setError("registration number is required");
            regNumberEdt.requestFocus();
            return;
        }

        if (studentEmail.isEmpty()) {
            studentEmailEdt.setError("Email is required");
            studentEmailEdt.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(studentEmail).matches()) {
            studentEmailEdt.setError("please provide valid email!");
            studentEmailEdt.requestFocus();
            return;
        }

        if (studentPassword.isEmpty()) {
            studentPasswordEdt.setError("Password is required");
            studentPasswordEdt.requestFocus();
            return;
        }

        if (studentPassword.length() < 6) {
            studentPasswordEdt.setError("Minimum password length is 6 characters");
            studentPasswordEdt.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

//saving pure details to firebase
        member.setFullname(fullname);
        member.setRegnumber(regNumber);
        member.setEmail(studentEmail);
        member.setPassword(studentPassword);
        reff.child(String.valueOf(maxid + 1)).setValue(member);


//generate QR Code
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = qrCodeWriter.encode(combined_details
                    , BarcodeFormat.QR_CODE,
                    300, 300);
            bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            for (int x = 0; x < 300; x++){
                for (int y = 0; y < 300; y++){
                    bitmap.setPixel(x, y, bitMatrix.get(x, y)? Color.BLACK : Color.WHITE);
                }
            }

            imageView.setImageBitmap(bitmap);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Saving data to SQLiteDatabase
        try {
            mySqlBitmap.insertData(fullname, regNumber, imageViewToByte(imageView), studentEmail);
        }catch (Exception e){
            e.printStackTrace();
        }

        //authentication
        mAuth.createUserWithEmailAndPassword(studentEmail, studentPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Student user = new Student(fullname, studentEmail);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "User has been registered successfully", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);

                                        startActivity(new Intent(StudentRegistration.this, StudentLogin.class));
                                        uploadImage();
                                        finish();

                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to Register! Try again!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Register! Try again!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void uploadImage() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "qrcode", null);
        final Uri imageUri = Uri.parse(path);

        final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+".jpeg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Upload upload = new Upload(fullname, regNumber, taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(upload);
            }
        });
    }


    private byte[] imageViewToByte(ImageView image){
        Bitmap myBitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
