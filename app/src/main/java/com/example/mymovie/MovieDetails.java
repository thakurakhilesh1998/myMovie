package com.example.mymovie;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MovieDetails extends AppCompatActivity {
    private static final int DEFAULT_VALUE = 0;
    MoviesData moviesData;
    int position;
    ActivityMovieDetailsBinding mainBinding;
    ArrayList<Trailers> trailers;
    ArrayList<Reviews> reviews;
    ReviewAdapter reviewAdapter;
    TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_movie_details);
        trailers = new ArrayList<>();
        reviews = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mainBinding.rvReviews.setLayoutManager(linearLayoutManager);
        mainBinding.rvTrailers.setLayoutManager(linearLayoutManager1);
        reviewAdapter = new ReviewAdapter(this, reviews);
        trailerAdapter = new TrailerAdapter(this, trailers);

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
        TrailerAndReviewsAsynLoader trailerAndReviewsAsynLoader = new TrailerAndReviewsAsynLoader();
        trailerAndReviewsAsynLoader.execute(String.valueOf(moviesData.getId()));


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

    class TrailerAndReviewsAsynLoader extends AsyncTask<String, Void, Boolean> {
        private static final String TRAILER_OBJECTS = "videos";
        private static final String TRAILER_OBJECTS_ARRAY = "results";
        private static final String TRAILER_ID = "id";
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
                Toast.makeText(getApplicationContext(), "Failed to fetch data", Toast.LENGTH_LONG).show();
            }
        }
    }
}
