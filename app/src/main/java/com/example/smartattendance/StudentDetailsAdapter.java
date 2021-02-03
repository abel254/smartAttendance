package com.example.smartattendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartattendance.Authentication.StudentDetails;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class StudentDetailsAdapter extends FirebaseRecyclerAdapter<StudentDetails, StudentDetailsAdapter.MyViewHolder> {

    public StudentDetailsAdapter(@NonNull FirebaseRecyclerOptions<StudentDetails> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull StudentDetails model) {
        holder.fullname.setText(model.getFullname());
        holder.reg_number.setText(model.getRegnumber());
        holder.id.setText(model.getIdNuM());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_details, parent, false);

        return new StudentDetailsAdapter.MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView id, fullname, reg_number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.full_name);
            reg_number = itemView.findViewById(R.id.regnumber);
            id = itemView.findViewById(R.id.id);
        }
    }
}
