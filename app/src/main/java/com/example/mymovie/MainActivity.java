package com.example.mymovie;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Modal.MoviesData;
import com.example.mymovie.Utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private static final String RESULTS = "results";
    private static final String TITLE = "original_title";
    private static final String RELEASE_DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String IMAGE_URL = "poster_path";
    private static final String VOTE_COUNT = "vote_count";
    private static final String POPULARITY = "popularity";
    private static final String ID = "id";
    private static final String BASEURL = "https://api.themoviedb.org/3/movie/popular?";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
    RecyclerView rvMoviesInGrid;
    MoviesAdapter moviesAdapter;
    ArrayList<MoviesData> moviesData;
    ProgressDialog progressDialog;
    ImageView ivError;
    private String TAG = MainActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rvMoviesInGrid = findViewById(R.id.rvMoviesInGrid);
        ivError = findViewById(R.id.notFoundImage);
        moviesData = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        rvMoviesInGrid.setLayoutManager(layoutManager);
        if (isConnectedToInterner()) {
            MovieAsyncTask movieAsyncTask = new MovieAsyncTask();
            moviesAdapter = new MoviesAdapter(this, moviesData);
            rvMoviesInGrid.setAdapter(moviesAdapter);
            movieAsyncTask.execute(BASEURL);
        } else {
            rvMoviesInGrid.setVisibility(View.GONE);
            TextView tvError = findViewById(R.id.tvError);
            tvError.setVisibility(View.VISIBLE);
        }
    }

    private boolean isConnectedToInterner() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sort_by_most_popular) {
            if (moviesData != null) {
                Collections.sort(moviesData);
                moviesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getApplicationContext(), "Data is Loading", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    class MovieAsyncTask extends AsyncTask<String, Void, ArrayList<MoviesData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.Loading));
            progressDialog.show();
        }

        @Override
        protected ArrayList<MoviesData> doInBackground(String... urls) {
            URL url = null;
            if (urls[0] != null) {
                try {
                    url = NetworkUtils.creteUrl(urls[0]);
                } catch (MalformedURLException e) {
                    Log.i(TAG, e.getMessage());
                }
                String responseFromServer = NetworkUtils.getDataFromServer(url);
                try {
                    convertJsonToMovieData(responseFromServer);
                } catch (Exception e) {
                    Log.i(TAG, e.getMessage());
                }

            }
            return moviesData;
        }

        @Override
        protected void onPostExecute(ArrayList<MoviesData> moviesData) {
            super.onPostExecute(moviesData);
            if (moviesData.size() > 0) {
                rvMoviesInGrid.setVisibility(View.VISIBLE);
                moviesAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                rvMoviesInGrid.setVisibility(View.GONE);
                ivError.setVisibility(View.VISIBLE);
            }
        }

        private void convertJsonToMovieData(String responseFromServer) {

            try {
                JSONObject mainObject = new JSONObject(responseFromServer);
                JSONArray mainArray = mainObject.getJSONArray(RESULTS);

                for (int i = 0; i < mainArray.length(); i++) {
                    StringBuilder builder = new StringBuilder(BASE_IMAGE_URL);
                    JSONObject object = mainArray.getJSONObject(i);
                    String imageUrl = builder.append(object.optString(IMAGE_URL)).toString();
                    moviesData.add(new MoviesData(object.optString(TITLE),
                            imageUrl,
                            object.optString(RELEASE_DATE),
                            object.optString(OVERVIEW), object.optInt(ID),
                            object.optDouble(POPULARITY), object.optInt(VOTE_COUNT),
                            object.optDouble(VOTE_AVERAGE)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
