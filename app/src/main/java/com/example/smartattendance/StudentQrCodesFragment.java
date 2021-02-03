package com.example.smartattendance;

import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartattendance.Authentication.StudentDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class StudentQrCodesFragment extends Fragment {
    private RecyclerView mRecylerView;
    private RecylerViewQrUpload mAdapter;
    ProgressBar mProgressCircle;

    private DatabaseReference mDatabaseRef;
    private List<Upload> mUploads;

    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_studentqrcodes, container, false);

        mRecylerView = view.findViewById(R.id.qrcode_recyclerview);
        mRecylerView.setHasFixedSize(true);
        mRecylerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        mProgressCircle = view.findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()){
                    Upload upload = postSnapshot.getValue(Upload.class);
                    mUploads.add(upload);
                }
                mAdapter = new RecylerViewQrUpload(getContext(), mUploads);

                mRecylerView.setAdapter(mAdapter);
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


/*
        recyclerView = view.findViewById(R.id.qrcode_recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        bitmapDetails = new ArrayList<>();
        adapter = new RecyclerViewQrAdapter(getContext(), bitmapDetails);
        recyclerView.setAdapter(adapter);

        database = new BitmapDatabase(getContext(), "BitmapDB.sqlite", null, 1);

        Cursor cursor = database.getData("SELECT * FROM BITMAP");
        bitmapDetails.clear();

        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String fullname = cursor.getString(1);
            String regnumber = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            String email = cursor.getString(4);

            bitmapDetails.add(new BitmapDetails(id, fullname, regnumber,  image, email));
        }
 */

        return view;
    }
}

