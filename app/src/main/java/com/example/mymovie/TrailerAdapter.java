package com.example.mymovie;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Modal.Trailers;


import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {
    public TrailerAdapter(Context context, ArrayList<Trailers> trailersAndReviews) {
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class TrailerHolder extends RecyclerView.ViewHolder {
        public TrailerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
