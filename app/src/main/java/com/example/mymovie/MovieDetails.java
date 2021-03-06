package com.example.mymovie;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Database.AppDatabase;
import com.example.mymovie.Database.AppExecutor;
import com.example.mymovie.Modal.MoviesData;
import com.example.mymovie.Modal.Reviews;
import com.example.mymovie.Modal.Trailers;
import com.example.mymovie.Utils.Constants;
import com.example.mymovie.Utils.MoviesDetailsUtils;
import com.example.mymovie.Utils.NetworkUtils;
import com.example.mymovie.databinding.ActivityMovieDetailsBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MovieDetails extends AppCompatActivity implements View.OnClickListener {
    private static final int DEFAULT_VALUE = 0;
    private static final String TRUE = "true";
    private static final String FALSE = "false";
    private static final String SEND_DATA_ERROR = "SEND SOME DATA";
    private final String TAG = "Movie Details";
    MoviesData moviesData;
    int position;
    ActivityMovieDetailsBinding mainBinding;
    ArrayList<Trailers> trailers;
    ArrayList<Reviews> reviews;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;
    AppDatabase appDatabase;
    MoviesData moviesData2;
    private boolean isFavourite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        mainBinding.idFavourite.setOnClickListener(this);
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mainBinding.rvReviews.setLayoutManager(linearLayoutManager);
        mainBinding.rvTrailers.setLayoutManager(linearLayoutManager1);
        Intent intent = getIntent();
        if (intent != null) {
            position = intent.getIntExtra(Constants.POSITION, DEFAULT_VALUE);
            Bundle b = intent.getExtras();
            if (b != null) {
                moviesData = b.getParcelable(Constants.LIST_PARCEL);
            }
        } else {
            Toast.makeText(getApplicationContext(), SEND_DATA_ERROR, Toast.LENGTH_LONG).show();
            finish();
        }
        isCurrentIsFavourite();
        setFavouriteButton();
        getSupportActionBar().setTitle(moviesData.getTitle());
        reviewAdapter = new ReviewAdapter(this, reviews);
        trailerAdapter = new TrailerAdapter(this, trailers, moviesData.getPosterUrl());
        mainBinding.rvReviews.addItemDecoration(new DividerItemDecoration(mainBinding.rvReviews.getContext(), DividerItemDecoration.VERTICAL));
        mainBinding.rvTrailers.addItemDecoration(new DividerItemDecoration(mainBinding.rvTrailers.getContext(), DividerItemDecoration.VERTICAL));
        mainBinding.rvTrailers.setAdapter(trailerAdapter);
        mainBinding.rvReviews.setAdapter(reviewAdapter);
        updateUI();
        TrailerAndReviewsAsynLoader trailerAndReviewsAsynLoader = new TrailerAndReviewsAsynLoader();
        trailerAndReviewsAsynLoader.execute(String.valueOf(moviesData.getId()));
    }

    private void isCurrentIsFavourite() {
        AppExecutor.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final MoviesData mData = appDatabase.taskDao().selectSpecialId(moviesData.getId());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mData != null) {
                            setFavouriteIcon(getResources().getDrawable(R.drawable.filled_24dp));
                        }
                    }
                });
            }
        });
    }

    private void setFavouriteButton() {
        Resources res = getResources();
        if (moviesData.getIsFavourite().equals(TRUE)) {
            setFavouriteIcon(res.getDrawable(R.drawable.filled_24dp));
        } else if (moviesData.getIsFavourite().equals(FALSE)) {
            setFavouriteIcon(res.getDrawable(R.drawable.empty_24dp));
        }
    }

    private void setFavouriteIcon(Drawable drawable) {

        mainBinding.idFavourite.setBackground(drawable);
    }

    private void updateUI() {

        Picasso.get().load(moviesData.getPosterUrl())
                .error(R.drawable.error_404_page_not_found_icon)
                .into(mainBinding.ivMainImage);
        mainBinding.tvTitle.setText(moviesData.getTitle());
        if (moviesData.getVoteCount() < 0) {
            mainBinding.tvVoteAvg.setText(getResources().getString(R.string.notAvailabel));
        } else {
            mainBinding.tvVoteAvg.setText(getResources().getString(R.string.votecount) + ": " + moviesData.getVoteAverage());
        }

        mainBinding.tvDes.setText(moviesData.getOverview());
        mainBinding.tvReleaseDate.setText(getResources().getString(R.string.Release) + ": " + moviesData.getReleaseDate());
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.idFavourite) {
            saveFavourite();
        }
    }

    private void saveFavourite() {
        if (isFavourite) {
            isFavourite = false;
            setFavouriteIcon(getResources().getDrawable(R.drawable.filled_24dp));
            moviesData.setIsFavourite(TRUE);
            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.taskDao().insertTask(moviesData);
                }
            });
        } else {
            isFavourite = true;
            setFavouriteIcon(getResources().getDrawable(R.drawable.empty_24dp));
            AppExecutor.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    appDatabase.taskDao().deleteTask(moviesData);
                }
            });
        }
    }

    class TrailerAndReviewsAsynLoader extends AsyncTask<String, Void, Boolean> {
        private static final String TRAILER_OBJECTS = "videos";
        private static final String TRAILER_OBJECTS_ARRAY = "results";
        private static final String TRAILER_ID = "key";
        private static final String TRAILER_NAME = "name";
        private static final String TRAILER_SITE = "site";
        private static final String REVIEWS_OBJECTS = "reviews";
        private static final String REVIEW_OBJECTS_ARRAY = "results";
        private static final String REVIEW_OBJECTS_AUTHOR = "author";
        private static final String REVIEW_OBJECTS_LINK = "url";
        private static final String REVIEW_OBJECTS_CONTENT = "content";


        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean isDataGot = false;
            String jsonResponse = "";
            URL url = MoviesDetailsUtils.getUrl(strings[0]);
            jsonResponse = NetworkUtils.getDataFromServer(url);
            getDataFromJsonResponse(jsonResponse);
            if (reviews != null && trailers != null) {
                isDataGot = true;
            }
            return isDataGot;
        }

        private void getDataFromJsonResponse(String jsonResponse) {

            try {
                JSONObject mainObject = new JSONObject(jsonResponse);
                JSONObject trailer_object = mainObject.getJSONObject(TRAILER_OBJECTS);
                JSONArray resultsArray = trailer_object.getJSONArray(TRAILER_OBJECTS_ARRAY);
                for (int i = 0; i < resultsArray.length(); i++) {
                    JSONObject resultaEle = resultsArray.getJSONObject(i);
                    trailers.add(new Trailers(resultaEle.optString(TRAILER_ID),
                            resultaEle.optString(TRAILER_SITE),
                            resultaEle.optString(TRAILER_NAME)));
                }

                JSONObject review_object = mainObject.getJSONObject(REVIEWS_OBJECTS);
                JSONArray review_result_array = review_object.getJSONArray(REVIEW_OBJECTS_ARRAY);
                for (int j = 0; j < review_result_array.length(); j++) {
                    JSONObject reviewResult = review_result_array.getJSONObject(j);
                    reviews.add(new Reviews(reviewResult.optString(REVIEW_OBJECTS_AUTHOR),
                            reviewResult.optString(REVIEW_OBJECTS_CONTENT),
                            reviewResult.optString(REVIEW_OBJECTS_LINK)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Boolean isData) {
            super.onPostExecute(isData);
            if (isData) {
                reviewAdapter.notifyDataSetChanged();
                trailerAdapter.notifyDataSetChanged();
            } else {
            }
        }
    }
}
