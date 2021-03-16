package com.example.smartattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.DataHolder> {
    Context context;
    ArrayList<Scan> scans;

    public DataAdapter(Context con, ArrayList<Scan> scanList){
        context = con;
        scans = scanList;
    }
    @NonNull
    @Override
    public DataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recycler, parent, false);
        DataHolder dataHolder = new DataHolder(view);
        return dataHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataHolder holder, int position) {
        Scan currentScan  = scans.get(position);
        String wordMeaning = currentScan.getScanResults();
        String wordExample = currentScan.getTime();
        holder.tvMeaning.setText(wordMeaning);
        holder.tvExample.setText(wordExample);
    }

    @Override
    public int getItemCount() {
        return scans.size();
    }

    public class DataHolder extends RecyclerView.ViewHolder{
        TextView tvMeaning, tvExample;

        public DataHolder(@NonNull View itemView) {
            super(itemView);

            tvMeaning = itemView.findViewById(R.id.text_row_desc1);
            tvExample = itemView.findViewById(R.id.text_row_desc2);
        }
    }
}
