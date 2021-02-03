package com.example.smartattendance;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class HomeAdminFragment extends Fragment {
    CardView scanCardview, currentCardview, reportsCardview, viewqrCardview;
    

    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_ahome, container, false);

        scanCardview = view.findViewById(R.id.scan_cardview);
        currentCardview = view.findViewById(R.id.current_cardview);
        reportsCardview = view.findViewById(R.id.reports_cardview);
        viewqrCardview = view.findViewById(R.id.viewqr_cardview);

        scanCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getFragmentManager()
                       .beginTransaction()
                       .replace(R.id.fragment_container, new ScanFragment()).commit();
            }
        });
        currentCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new CurrentFragment()).commit();
            }
        });
        reportsCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new ReportsFragment()).commit();
            }
        });
        viewqrCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new StudentQrCodesFragment()).commit();
            }
        });
        return view;
    }
}

