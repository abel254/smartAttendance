package com.example.smartattendance.Authentication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.smartattendance.BitmapDatabase;
import com.example.smartattendance.R;
import com.example.smartattendance.Upload;
import com.github.mikephil.charting.formatter.IFillFormatter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StudentRegistration extends AppCompatActivity implements View.OnClickListener {
    StudentDetails member;
    Upload upload;
    String combined_details;
    String fullname, regNumber, studentEmail, studentPassword;
    private EditText fullnameEdt, studentEmailEdt, studentPasswordEdt, regNumberEdt;
    private Button signUpButton;
    private ProgressBar progressBar;
    private TextView registerLoginTv;
    private long maxid = 0;
    private DatabaseReference reff;
    private StorageReference mStorageRef;
    ImageView imageView;
    private Bitmap bitmap;
    String regNumberPattern = "[A-Z0-9_.+-]+/[0-9]+";
    private static final int REQUEST_PERMISSION = 0;


    private FirebaseAuth mAuth;

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

        member = new StudentDetails();
        upload = new Upload();

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }else {

            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }else {

            }
            if (!permissions.isEmpty()){
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, REQUEST_PERMISSION);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION:{
                for (int i = 0; i < permissions.length; i++){
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        System.out.println("Permissions -->" + "Permission Granted; " + permissions[i]);
                    }else if (grantResults[i] == PackageManager.PERMISSION_DENIED){
                        System.out.println("Permission --> " + "Permission Denied: " + permissions[i]);
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
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

        if (regNumber.isEmpty()){
            regNumberEdt.setError("Registration number is required");
            regNumberEdt.requestFocus();
            return;
        }
        if (!regNumber.matches(regNumberPattern)) {
            regNumberEdt.setError("Please provide valid registration number");
            Toast.makeText(getApplicationContext(), "e.g. C025-01-0974/2017", Toast.LENGTH_LONG).show();
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
        }catch (Exception e) {
            e.printStackTrace();
        }

        //authentication
        mAuth.createUserWithEmailAndPassword(studentEmail, studentPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull final Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Student user = new Student(fullname, studentEmail, regNumber);

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
                                        savePureDetails();
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
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        DatabaseReference imagestore = FirebaseDatabase.getInstance().getReference("Image");

                        String uploadId = imagestore.push().getKey();

                        upload.setFullname(fullname);
                        upload.setmImageUrl(String.valueOf(uri));
                        upload.setRegnumber(regNumber);
                        upload.setEmail(studentEmail);
                        imagestore.child(uploadId).setValue(upload);
                    }
                });
            }
        });
    }

    public void savePureDetails(){
        member.setIdNuM(String.valueOf(maxid + 1));
        member.setFullname(fullname);
        member.setRegnumber(regNumber);
        member.setEmail(studentEmail);
        member.setPassword(studentPassword);
        reff.child(String.valueOf(maxid + 1)).setValue(member);

    }

}
