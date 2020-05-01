package com.example.mymovie;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymovie.Modal.MoviesData;
import com.example.mymovie.Utils.Constants;
import com.squareup.picasso.Picasso;

public class MovieDetails extends AppCompatActivity {
    private static final int DEFAULT_VALUE = 0;
    ImageView ivMainImage;
    TextView tvTitle;
    TextView tvVoteAvg;
    TextView tvDes;
    TextView tvReleaseDate;
    MoviesData moviesData;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ivMainImage = findViewById(R.id.ivMainImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvVoteAvg = findViewById(R.id.tvVoteAvg);
        tvDes = findViewById(R.id.tvDes);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(Constants.POSITION, DEFAULT_VALUE);
            Bundle b = intent.getExtras();
            if (b != null) {
                moviesData = b.getParcelable(Constants.LIST_PARCEL);
            }

        }
        getSupportActionBar().setTitle(moviesData.getTitle());
        updateUI();

    }

    private void updateUI() {

        Picasso.get().load(moviesData.getPosterUrl()).error(R.drawable.error_404_page_not_found_icon)
                .into(ivMainImage);
        tvTitle.setText(moviesData.getTitle());
        if (moviesData.getVoteCount() < 0) {
            tvVoteAvg.setText(getResources().getString(R.string.notAvailabel));
        } else {
            tvVoteAvg.setText(getResources().getString(R.string.votecount) + ": " + moviesData.getVoteAverage());
        }

        tvDes.setText(moviesData.getOverview());
        tvReleaseDate.setText(getResources().getString(R.string.Release) + ": " + moviesData.getReleaseDate());
    }
}
