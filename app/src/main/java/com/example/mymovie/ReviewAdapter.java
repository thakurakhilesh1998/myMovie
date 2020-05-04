package com.example.mymovie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Modal.Reviews;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewHolder> {
    Context context;
    ArrayList<Reviews> reviews;

    public ReviewAdapter(Context context, ArrayList<Reviews> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review, parent, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewHolder holder, final int position) {

        if (position == reviews.size() - 1) {
            holder.dividerr.setVisibility(View.INVISIBLE);
        }

        holder.tvAuthor.setText(reviews.get(position).getReviewAuthor_name());
        if (reviews.get(position).getReviewDescription().length() > 100) {
            holder.tvContent.setText(reviews.get(position).getReviewDescription().substring(0, 30) + "...");
        } else {
            holder.tvContent.setText(reviews.get(position).getReviewDescription());
        }

        holder.tvLink.setText(reviews.get(position).getReviewId());

        holder.tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(reviews.get(position).getReviewId()));
                context.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public class ReviewHolder extends RecyclerView.ViewHolder {
        TextView tvAuthor;
        TextView tvContent;
        TextView tvLink;
        View dividerr;

        public ReviewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvContent = itemView.findViewById(R.id.tvReview);
            tvLink = itemView.findViewById(R.id.tvLink);
            dividerr = itemView.findViewById(R.id.dividerr);
        }
    }
}
