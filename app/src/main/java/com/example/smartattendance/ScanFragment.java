package com.example.smartattendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.icu.number.ScientificNotation;
import android.icu.util.LocaleData;
import android.icu.util.ULocale;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ScanFragment extends Fragment {
    TextView resultsData;
    DatabaseReference databaseScan, morning, midday, evening, room;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private long maxId = 0;
    CameraSource cameraSource;
    private String scanResults;
    private String time, idTime, date, dateDay;
    private String encrypedResult;

    DatabaseReference roomReference;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final Activity activity = getActivity();

        view = inflater.inflate(R.layout.fragment_scan, container, false);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
        time = simpleDateFormat.format(new Date());
        SimpleDateFormat idDateformat = new SimpleDateFormat("hh");
        idTime = idDateformat.format(new Date());
        SimpleDateFormat dateDayFormat = new SimpleDateFormat("d");
        dateDay = dateDayFormat.format(new Date());
        //c = Calendar.getInstance();
        //date = DateFormat.getDateInstance().format(c.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy");
        date = dateFormat.format(new Date());

        databaseScan = FirebaseDatabase.getInstance().getReference("scan_StudentResults");
        databaseScan.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   maxId = (snapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        room = FirebaseDatabase.getInstance().getReference("RC13");
        morning = room.child("Introduction_to_Java");
        midday = room.child("Distributed_Systems");
        evening = room.child("Computer_Networks");

        surfaceView = view.findViewById(R.id.camera);
        resultsData = view.findViewById(R.id.results_tv);

        barcodeDetector = new BarcodeDetector.Builder(getContext()).
                setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource = new CameraSource.Builder(getContext(), barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(holder);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                if (qrcode.size() != 0){
                    resultsData.post(new Runnable() {
                        @Override
                        public void run() {
                            resultsData.setText(qrcode.valueAt(0).displayValue);

                            Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);

                            //MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.beep);
                            //mediaPlayer.start();

                            scanResults = qrcode.valueAt(0).displayValue;

                            try {
                                encrypedResult = AESUtils.encrypt(scanResults);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String combined = idTime+encrypedResult;
                            Scan scan = new Scan(scanResults, time, date, dateDay);
                            databaseScan.child(combined).setValue(scan);

                            //Setting up the timetable
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                            Date currentLocalTime = cal.getTime();
                            DateFormat dateFormat1 = new SimpleDateFormat("hh aaa");
                            String localTime = dateFormat1.format(currentLocalTime);

                            if (localTime.equals("10 AM"))
                                morning.child(combined).setValue(scan);
                            if (localTime.equals("11 AM"))
                                midday.child(combined).setValue(scan);
                            if (localTime.equals("1 AM"))
                                evening.child(combined).setValue(scan);
                            
                        }
                    });

                }
            }
        });

        return view;
    }
}

