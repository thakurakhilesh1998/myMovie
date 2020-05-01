package com.example.mymovie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Modal.MoviesData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesHolder> {
    Context context;
    ArrayList<MoviesData> moviesData;

    public MoviesAdapter(Context context, ArrayList<MoviesData> moviesData) {
        this.context = context;
        this.moviesData = moviesData;
    }

    @NonNull
    @Override
    public MoviesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.movies_item, parent, false);
        return new MoviesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesHolder holder, int position) {
        Picasso.get()
                .load(moviesData.get(position).getPosterUrl())
                .error(R.drawable.error_404_page_not_found_icon)
                .into(holder.ivPosterImage);
        holder.tvMainTitle.setText(moviesData.get(position).getTitle());
        holder.tvPopularity.setText(context.getResources().getString(R.string.Popularity)+String.valueOf(moviesData.get(position).getPopularity()));

    }

    @Override
    public int getItemCount() {
        return moviesData.size();
    }

    public class MoviesHolder extends RecyclerView.ViewHolder {
        ImageView ivPosterImage;
        TextView tvMainTitle;
        TextView tvPopularity;
        public MoviesHolder(@NonNull View itemView) {
            super(itemView);
            ivPosterImage=itemView.findViewById(R.id.ivPosterImage);
            tvMainTitle=itemView.findViewById(R.id.tvMovie);
            tvPopularity=itemView.findViewById(R.id.tvPopularity);
        }
    }
}
