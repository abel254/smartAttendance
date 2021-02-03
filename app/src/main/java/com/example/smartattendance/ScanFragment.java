package com.example.smartattendance;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.icu.number.ScientificNotation;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.Result;

import java.util.ArrayList;

public class ScanFragment extends Fragment {
    TextView resultsData;
    CodeScanner codeScanner;
    CodeScannerView codeScannerView;
    //ArrayList<Scan> scan;

    DatabaseReference databaseScan;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();

        view = inflater.inflate(R.layout.fragment_scan, container, false);

        databaseScan = FirebaseDatabase.getInstance().getReference("scan_StudentResults");
        //scan = new ArrayList<>();

        codeScannerView = view.findViewById(R.id.scanner_view);
        resultsData = view.findViewById(R.id.results_tv);

        codeScanner = new CodeScanner(activity, codeScannerView);
        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultsData.setText(result.getText());
                        String id = databaseScan.push().getKey();
                        String scanResults = result.getText();
                        //save data to firebase
                        Scan scan = new Scan(scanResults);
                        databaseScan.child(id).setValue(scan);
                        //databaseScan.setValue(result.getText());

                        Toast.makeText(getContext(), "Student registered for class successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        codeScannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeScanner.startPreview();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    public void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}

