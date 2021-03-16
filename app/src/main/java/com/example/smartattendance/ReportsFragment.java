package com.example.smartattendance;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.smartattendance.Authentication.StudentDetails;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("Member");
    private CardView fromCardView, toCardView;
    private TextView fromTextView, toTextView, count, dateCount, days;
    BarChart barChart;
    Spinner spinner;
    ArrayList<BarEntry> scansArray;
    Calendar myCalendar;
    String fromDate, formatedDate;
    ImageView imageView;
    Button daysButton;

    View view;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reports, container, false);

        fromCardView = view.findViewById(R.id.from_cardview);
        toCardView = view.findViewById(R.id.to_cardview);
        fromTextView = view.findViewById(R.id.fromDate_text);
        toTextView = view.findViewById(R.id.toDate_text);
        barChart = view.findViewById(R.id.bar_chart);
        spinner = view.findViewById(R.id.spinner);
        count = view.findViewById(R.id.count);
        dateCount = view.findViewById(R.id.date_count);
        days = view.findViewById(R.id.days);
        imageView = view.findViewById(R.id.graph);
        daysButton = view.findViewById(R.id.days_button);

        scansArray = new ArrayList<>();

        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        fromCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            }
        });

        toCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                int mMonth = c.get(Calendar.MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("d-MMM-yyyy");
                        formatedDate = df.format(calendar.getTime());
                        toTextView.setText(formatedDate);

                        if (fromTextView == null && toTextView == null) {
                            daysButton.setEnabled(false);
                        } else {
                            daysButton.setEnabled(true);
                        }

                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                final List<String> scanResults = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    StudentDetails studentDetails = dataSnapshot.getValue(StudentDetails.class);
                    String name = studentDetails.getFullname();
                    String regNumber = studentDetails.getRegnumber();
                    String combined_details = name + " " + regNumber;
                    scanResults.add(combined_details);
                }

                if (scanResults != null){
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, scanResults);
                    arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();

                Query query = FirebaseDatabase.getInstance().getReference("scan_StudentResults")
                        .orderByChild("scanResults").equalTo(text);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int size = (int) snapshot.getChildrenCount();
                        count.setText(String.valueOf(size));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d-MMM-yyyy");

        daysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalDate d1 = LocalDate.parse(fromDate, formatter);
                LocalDate d2 = LocalDate.parse(formatedDate, formatter);
                Long daysBetween = ChronoUnit.DAYS.between(d1, d2);
                dateCount.setText(Integer.toString(Math.toIntExact(daysBetween)));

                imageView.setVisibility(View.INVISIBLE);
                barChart.setVisibility(View.VISIBLE);
                Query query = FirebaseDatabase.getInstance().getReference("scan_StudentResults")
                        .orderByChild("dateDay")
                        .startAt(fromDate)
                        .endAt(formatedDate);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Scan scan = dataSnapshot.getValue(Scan.class);
                            final String date = scan.getDateDay();

                            Query query1 = FirebaseDatabase.getInstance().getReference("scan_StudentResults")
                                    .orderByChild("dateDay")
                                    .equalTo(date);
                            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    int datesCount = (int) snapshot.getChildrenCount();

                                    scansArray.add(new BarEntry(Float.parseFloat(date), datesCount));
                                    showBargragh(scansArray);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


        return view;
    }

    private void showBargragh(ArrayList<BarEntry> scansArray) {
        BarDataSet barDataSet = new BarDataSet(scansArray, "Scans");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(16f);
        barDataSet.setValueTextColor(Color.BLACK);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Bar Chart");
        barChart.animateY(500);
    }

    public void updateLabel() {
        String myFormat = "d-MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        fromDate = sdf.format(myCalendar.getTime());
        fromTextView.setText(fromDate);
        days.setClickable(true);
    }

}

