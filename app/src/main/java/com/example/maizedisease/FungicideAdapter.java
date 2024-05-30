package com.example.maizedisease;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FungicideAdapter extends RecyclerView.Adapter<FungicideAdapter.FungicideViewHolder> {
    private ArrayList<FungicideModel> fungicideList;

    public FungicideAdapter(ArrayList<FungicideModel> fungicideList) {
        this.fungicideList = fungicideList;
    }

    @NonNull
    @Override
    public FungicideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fungicide, parent, false);
        return new FungicideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FungicideViewHolder holder, int position) {
        FungicideModel fungicide = fungicideList.get(position);
        holder.fungicideNameTextView.setText(fungicide.getName());
        holder.fungicideImageView.setImageResource(fungicide.getImageResource());
        holder.fungicideDescriptionTextView.setText(fungicide.getDescription());
    }

    @Override
    public int getItemCount() {
        return fungicideList.size();
    }

    static class FungicideViewHolder extends RecyclerView.ViewHolder {
        ImageView fungicideImageView;
        TextView fungicideNameTextView;
        TextView fungicideDescriptionTextView;

        FungicideViewHolder(@NonNull View itemView) {
            super(itemView);
            fungicideImageView = itemView.findViewById(R.id.fungicideImageView);
            fungicideNameTextView = itemView.findViewById(R.id.fungicideNameTextView);
            fungicideDescriptionTextView = itemView.findViewById(R.id.fungicideDescriptionTextView);
        }
    }
}