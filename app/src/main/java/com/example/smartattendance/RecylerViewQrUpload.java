package com.example.smartattendance;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RecylerViewQrUpload extends RecyclerView.Adapter<RecylerViewQrUpload.ImageViewHolder> {
    private Context mContext;
    private List<Upload> mUploads;
    private Bitmap bitmap;

    public RecylerViewQrUpload(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.bitmap_details, parent, false);
        return new ImageViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCurrent = mUploads.get(position);
        holder.fullname.setText(uploadCurrent.getFullname());
        holder.regnumber.setText(uploadCurrent.getRegnumber());
        Picasso.get().load(uploadCurrent.getmImageUrl()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView fullname, regnumber;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.bitmap_fullname);
            regnumber = itemView.findViewById(R.id.bitmap_regnumber);
            imageView = itemView.findViewById(R.id.qr_image);
        }
    }
}
