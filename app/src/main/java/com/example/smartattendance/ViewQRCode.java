package com.example.smartattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.Preference;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ViewQRCode extends AppCompatActivity implements View.OnClickListener {
    Bitmap bitmap;
    public ImageView qrImageView;
    ImageView share, download;
    String email, fullname, qrCodeUrl;
    TextView studentEmail;
    FileOutputStream outputStream;

    private static final int REQUEST_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_qrcode);

        studentEmail = findViewById(R.id.studentEmail);
        qrImageView = findViewById(R.id.clear_qrCode);
        share = findViewById(R.id.share);
        share.setOnClickListener(this);
        download = findViewById(R.id.download);
        download.setOnClickListener(this);

        Intent intent = getIntent();
        fullname = intent.getStringExtra("fullname");
        email = intent.getStringExtra("email");
        qrCodeUrl = intent.getStringExtra("qrcode");

        studentEmail.setText(email);
        Picasso.get().load(qrCodeUrl).into(qrImageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            List<String> permissions = new ArrayList<>();
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                // preferenceUtility.setString("storage", true);
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {
                //preferenceUtility.setString("storage", "true");
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_PERMISSION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                        REQUEST_PERMISSION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Permissions -->" + "Permission Granted; " + permissions[i]);
                    } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
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
            case R.id.share:
                final Intent emailIntent1;

                BitmapDrawable drawable = ((BitmapDrawable) qrImageView.getDrawable());
                Bitmap bitmap = drawable.getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "title", null);
                Uri bitmapUri = Uri.parse(path);

                emailIntent1 = new Intent(Intent.ACTION_SEND);
                emailIntent1.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                emailIntent1.putExtra(Intent.EXTRA_SUBJECT, fullname + " QR Code");
                emailIntent1.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                emailIntent1.setType("image/*");
                startActivity(emailIntent1);
                break;

            case R.id.download:
                BitmapDrawable bitmapDrawable = (BitmapDrawable) qrImageView.getDrawable();
                Bitmap downloadBitmap = bitmapDrawable.getBitmap();

                File filePath = Environment.getExternalStorageDirectory();
                File dir = new File(filePath.getAbsolutePath() + "/SmartAttendance" + "/StudentQRCode Images");
                if (!dir.exists()){
                    dir.mkdir();
                }
                File file = new File(dir, fullname+".jpg");
                try {
                    outputStream = new FileOutputStream(file);
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }
                downloadBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Toast.makeText(getApplicationContext(), "QRCode image saved at " + Environment.getExternalStorageDirectory() + "/SmartAttendance" + "/StudentQRCode Images", Toast.LENGTH_LONG).show();
                try {
                    outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}