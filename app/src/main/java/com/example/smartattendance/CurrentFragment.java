package com.example.smartattendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class CurrentFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("scan_StudentResults");
    RecyclerView recyclerView;
    MyAdapter adapter;
    Button save;
    ArrayList<String> scanList;
    View view;

    public CurrentFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current , container, false);


        save = view.findViewById(R.id.save_pdf);
        recyclerView = view.findViewById(R.id.recylcerview_student);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        final FirebaseRecyclerOptions<Scan> options =
                new FirebaseRecyclerOptions.Builder<Scan>()
                        .setQuery(myRef, Scan.class)
                        .build();
        adapter = new MyAdapter(options);
        recyclerView.setAdapter(adapter);

        scanList = new ArrayList<>();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

              for (DataSnapshot dsp : snapshot.getChildren()){
                  Scan scan = dsp.getValue(Scan.class);
                  final String scanResults = scan.getScanResults();
                  scanList.add(scanResults);

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                    requestPermissions(permission, 1000);

                                } else {
                                    savePdf(scanResults);
                                }

                            } else {
                                savePdf(scanResults);
                            }
                        }
                    });
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    private void savePdf(String text) {
        Document doc = new Document();
        String mfile = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
        String mfilePath = Environment.getExternalStorageDirectory()+"/"+mfile+".pdf";
        Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

        try {
            PdfWriter.getInstance(doc, new FileOutputStream(mfilePath));
            doc.open();
            doc.addAuthor("abeli");
            Chunk chunk = new Chunk(text, smallBold);
            Paragraph paragraph = new Paragraph(chunk);
            doc.add(paragraph);
            doc.close();
            Toast.makeText(getContext(), ""+mfile+".pdf"+"saved to "+mfilePath, Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Toast.makeText(getContext(), "This is error msg: "+e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG ,"Erro msg: " + e.getMessage());
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();

    }
    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
/*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1000:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    savePdf(options);
                }else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT);
                }
        }
    }

 */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.current_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

