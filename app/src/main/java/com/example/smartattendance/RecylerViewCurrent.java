package com.example.smartattendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecylerViewCurrent extends RecyclerView.Adapter<RecylerViewCurrent.MyViewHolder>{
    ArrayList<String> scanResults = new ArrayList<String>();
    ArrayList<String> time = new ArrayList<String>();
    ArrayList<String> date = new ArrayList<String>();


    public RecylerViewCurrent(ArrayList<String> scanResults,ArrayList<String> time, ArrayList<String> date){
        this.scanResults = scanResults;
        this.time = time;
        this.date = date;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.scan_list, parent, false);
        return  new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (date.get(position).isEmpty()){
            holder.date.setVisibility(View.GONE);
        }else {
            holder.date.setText(date.get(position));
        }


        holder.scanResults.setText(scanResults.get(position));
        holder.time.setText(time.get(position));
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView scanResults, time, date, dateDay;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            scanResults = itemView.findViewById(R.id.scannresults_text);
            time = itemView.findViewById(R.id.time);
            date = itemView.findViewById(R.id.date_display);
            dateDay = itemView.findViewById(R.id.dateDay);
        }
    }

}
