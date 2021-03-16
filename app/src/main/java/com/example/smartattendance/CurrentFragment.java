package com.example.smartattendance;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartattendance.Authentication.StudentDetails;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.components.Legend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static android.content.ContentValues.TAG;

public class CurrentFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("scan_StudentResults");
    RecyclerView recyclerView;
    RecylerViewCurrent recylerViewCurrent;
    ProgressBar mProgressBar;
    List<Scan> scanList;
    Button save;
    View view;

    private File pdfFile;
    ArrayList<String> myList1;
    String detailsf, timef, datef, dateDayf;
    PdfPTable table;

    ArrayList<String> scanResults = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> date = new ArrayList<String>();

    ArrayList<String> dates = new ArrayList<>();

    public CurrentFragment(){

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_current , container, false);


        save = view.findViewById(R.id.save_pdf);
        recyclerView = view.findViewById(R.id.recylcerview_student);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myList1 = new ArrayList<>();

        scanList = new ArrayList<>();

        mProgressBar = view.findViewById(R.id.progress_circleCurrent);

        table = new PdfPTable(3);
        Font heading = new Font(Font.FontFamily.TIMES_ROMAN, 21.0f, Font.BOLD, BaseColor.BLACK);
        final PdfPCell[] cell1 = {new PdfPCell(new Phrase(Element.ALIGN_CENTER, "Student Details", heading))};
        final PdfPCell[] cell2 = {new PdfPCell(new Phrase(Element.ALIGN_CENTER, "Time", heading))};
        final PdfPCell[] cell3 = {new PdfPCell(new Phrase(Element.ALIGN_CENTER, "Date", heading))};
        float[] columnWidths = {2f, 1f, 1f};
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.setHeaderRows(1);
        try {
            table.setWidths(columnWidths);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        table.addCell(cell1[0]);
        table.addCell(cell2[0]);
        table.addCell(cell3[0]);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Scan scans = dataSnapshot.getValue(Scan.class);
                    detailsf = scans.getScanResults();
                    timef = scans.getTime();
                    datef = scans.getDate();
                    scanList.add(scans);

                    //putting database extracted data to PDF cells and table
                    cell1[0] = new PdfPCell(new Phrase(Element.ALIGN_CENTER, detailsf));
                    table.addCell(cell1[0]);
                    cell2[0] = new PdfPCell(new Phrase(Element.ALIGN_CENTER, timef));
                    table.addCell(cell2[0]);
                    cell3[0] = new PdfPCell(new Phrase(Element.ALIGN_CENTER, datef));
                    table.addCell(cell3[0]);

                    //sorting date


                    scanResults.add(detailsf);
                    time.add(timef);
                    date.add(datef);

                    for (String daes : date){
                        if (dates.contains(daes)){
                            dates.add("");
                        }else {
                            dates.add(daes);
                        }
                    }


                    recylerViewCurrent = new RecylerViewCurrent(scanResults, time, dates);
                    recyclerView.setAdapter(recylerViewCurrent);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    createPdf();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });



        return view;
    }
/*
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.current_toolbar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                Collections.sort(scanList, Scan.sortedDate);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

 */


    public void createPdf() throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory() + "/SmartAttendance");
        if (!docsFolder.exists()) {
            docsFolder.mkdir();
        }

        String pdfName = "Student_scans.pdf";
        pdfFile = new File(docsFolder.getAbsolutePath(), pdfName);
        final OutputStream output = new FileOutputStream(pdfFile);
        final Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, output);
            document.open();
            Font f = new Font(Font.FontFamily.TIMES_ROMAN, 30.0f, Font.UNDERLINE, BaseColor.BLUE);
            Font g = new Font(Font.FontFamily.TIMES_ROMAN, 20.0f, Font.NORMAL, BaseColor.BLUE);
            Paragraph prefaceX = new Paragraph();
            prefaceX.setAlignment(Element.ALIGN_CENTER);
            document.add(prefaceX);
            Toast.makeText(getContext(), "Attendance saved at " + Environment.getExternalStorageDirectory() + "/SmartAttendance", Toast.LENGTH_LONG).show();
            document.add(new Paragraph("SmartAttendance \n\n", f));
            document.add(new Paragraph("Scanned student details \n", g));
            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

}

