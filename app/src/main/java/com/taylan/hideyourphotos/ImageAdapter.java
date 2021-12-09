package com.taylan.hideyourphotos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import com.taylan.hideyourphotos.databinding.RowRecycyleLayoutBinding;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageShow> {
    private ArrayList<Image> arrayList;
    public ImageAdapter(ArrayList<Image> arrayList) {
        this.arrayList = arrayList;
    }
    @NonNull
    @Override
    public ImageShow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowRecycyleLayoutBinding rowRecycyleLayoutBinding = RowRecycyleLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImageShow(rowRecycyleLayoutBinding);
    }
    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ImageShow holder, int position) {
        Picasso.get().load(arrayList.get(position).urlAddress).into(holder.rowRecycyleLayoutBinding.recyclerViewPhoto);
    }
    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    class ImageShow extends RecyclerView.ViewHolder{
        RowRecycyleLayoutBinding rowRecycyleLayoutBinding;
        public ImageShow(RowRecycyleLayoutBinding rowRecycyleLayoutBinding){
            super(rowRecycyleLayoutBinding.getRoot());
            this.rowRecycyleLayoutBinding = rowRecycyleLayoutBinding;
        }
    }
}
