package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.Preference;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewQRCode extends AppCompatActivity implements View.OnClickListener {
    public ImageView qrImage;
    ImageView share;
    Bitmap bitmap;
    String email, fullname;
    TextView studentEmail;

    private static final int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qrcode);

        studentEmail = findViewById(R.id.studentEmail);
        qrImage = findViewById(R.id.clear_qrCode);
        share = findViewById(R.id.share);
        share.setOnClickListener(this);

        Intent intent = getIntent();
        fullname = intent.getStringExtra("fullname");
        email = intent.getStringExtra("email");
        studentEmail.setText(email);

        byte[] image = intent.getByteArrayExtra("clear_QRCode");
        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        qrImage.setImageBitmap(bitmap);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }else {
               // preferenceUtility.setString("storage", true);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }else {
                //preferenceUtility.setString("storage", "true");
            }
            if (!permissions.isEmpty()){
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSION);
            }
        }


    }

    @Override
    public void onClick(View v) {
        final Intent emailIntent1;

        if (v == share){
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

               String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
               Uri screenshortUri = Uri.parse(path);

               emailIntent1 = new Intent(Intent.ACTION_SEND);
               emailIntent1.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
               emailIntent1.putExtra(Intent.EXTRA_SUBJECT,fullname+" QR Code");
               emailIntent1.putExtra(Intent.EXTRA_STREAM, screenshortUri);
               emailIntent1.setType("image/*");

               //startActivity(Intent.createChooser(emailIntent1, "Send email using"));
            startActivity(emailIntent1);

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
}
