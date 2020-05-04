package com.example.mymovie;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Modal.Trailers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {
    ArrayList<Trailers> trailers;
    Context context;
    String posterUrl;

    public TrailerAdapter(Context context, ArrayList<Trailers> trailers, String posterUrl) {
        this.context = context;
        this.trailers = trailers;
        this.posterUrl = posterUrl;
    }

    @NonNull
    @Override
    public TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer, parent, false);
        return new TrailerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerHolder holder, final int position) {
        if (position == trailers.size() - 1) {
            holder.view.setVisibility(View.INVISIBLE);
        }
        Picasso.get().load(posterUrl).error(R.drawable.movies_tiles).into(holder.ivImage);
        holder.tvName.setText(trailers.get(position).getTrialer_title());
        holder.ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailers.get(position).getTrailer_id()));
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + trailers.get(position).getTrailer_id()));
                try {
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(webIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    public class TrailerHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        ImageView ivPlay;
        TextView tvName;
        View view;

        public TrailerHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivPlay = itemView.findViewById(R.id.ivPlay);
            tvName = itemView.findViewById(R.id.tvTrailerTilte);
            view = itemView.findViewById(R.id.dividert);
        }
    }
}
