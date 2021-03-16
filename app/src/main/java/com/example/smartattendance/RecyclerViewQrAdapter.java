package com.example.smartattendance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class RecyclerViewQrAdapter extends RecyclerView.Adapter<RecyclerViewQrAdapter.MyViewHolder> {
    private Context mContext;
    private List<BitmapDetails> bitmapDetails;

    public RecyclerViewQrAdapter(Context mContext, List<BitmapDetails> bitmapDetails) {
        this.mContext = mContext;
        this.bitmapDetails = bitmapDetails;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.bitmap_details, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.fullname.setText(bitmapDetails.get(position).getFullname());
        holder.regnumber.setText(bitmapDetails.get(position).getRegnumber());

        final byte[] bitmapImage = bitmapDetails.get(position).getImage();
        final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapImage, 0, bitmapImage.length);
        holder.imageView.setImageBitmap(bitmap);

        holder.bitmapCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ViewQRCode.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                intent.putExtra("clear_QRCode", byteArray);
                intent.putExtra("email", bitmapDetails.get(position).getEmail());
                intent.putExtra("fullname", bitmapDetails.get(position).getFullname());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bitmapDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView fullname, regnumber, email;
        ImageView imageView;
        CardView bitmapCardview;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.bitmap_fullname);
            regnumber = itemView.findViewById(R.id.bitmap_regnumber);
            imageView = itemView.findViewById(R.id.qr_image);
            email = itemView.findViewById(R.id.email_of_student);
            bitmapCardview = itemView.findViewById(R.id.bitmap_cardview);
        }
    }
}
