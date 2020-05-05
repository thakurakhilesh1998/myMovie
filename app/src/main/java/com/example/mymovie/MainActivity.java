package com.example.mymovie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovie.Database.AppDatabase;
import com.example.mymovie.Modal.MoviesData;
import com.example.mymovie.Utils.ConnectivityUtil;
import com.example.mymovie.Utils.NetworkUtils;
import com.example.mymovie.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String RESULTS = "results";
    private static final String TITLE = "original_title";
    private static final String RELEASE_DATE = "release_date";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String IMAGE_URL = "poster_path";
    private static final String VOTE_COUNT = "vote_count";
    private static final String POPULARITY = "popularity";
    private static final String ID = "id";
    private static final String ISFAVOURITE = "false";
    private static final String BASEURL = "https://api.themoviedb.org/3/movie/popular?";
    private static final String TOPRATED_BASEURL = "http://api.themoviedb.org/3/movie/top_rated?";
    private static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/w185/";
    public String sort_value;
    ActivityMainBinding homeBinding;
    MoviesAdapter moviesAdapter;
    ArrayList<MoviesData> moviesData;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    GridLayoutManager layoutManager;
    MovieAsyncTask movieAsyncTask;
    MovieAsyncTask movieAsyncTask2;
    AppDatabase appDatabase;
    private String TAG = MainActivity.this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        appDatabase = AppDatabase.getInstance(this);
        setUpSharedPreferense();
        movieAsyncTask = new MovieAsyncTask();
        movieAsyncTask2 = new MovieAsyncTask();
        setUpLayoutManager();
        progressDialog = new ProgressDialog(this);
        if (ConnectivityUtil.isConnectedToInterner(this)) {
            moviesAdapter = new MoviesAdapter(this, moviesData);
            homeBinding.rvMoviesInGrid.setAdapter(moviesAdapter);
            setMovieAdapter();

        } else {
            homeBinding.rvMoviesInGrid.setVisibility(View.GONE);
            TextView tvError = findViewById(R.id.tvError);
            tvError.setVisibility(View.VISIBLE);
        }
    }

    private void setUpLayoutManager() {
        moviesData = new ArrayList<>();
        layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        homeBinding.rvMoviesInGrid.setLayoutManager(layoutManager);
    }

    private void setMovieAdapter() {

        if (sort_value.equals(getString(R.string.fav_key))) {
            final LiveData<List<MoviesData>> moviesData1 = appDatabase.taskDao().loadAllMovies();
            moviesData1.observe(this, new Observer<List<MoviesData>>() {
                @Override
                public void onChanged(List<MoviesData> moviesData1) {

                    moviesData = (ArrayList<MoviesData>) moviesData1;
                    if (moviesData1.size() == 0) {
                        homeBinding.notFoundImage.setVisibility(View.VISIBLE);
                    } else {
                        homeBinding.rvMoviesInGrid.setVisibility(View.VISIBLE);
                        moviesAdapter.setTasks(moviesData);
                    }

                }
            });

        } else if (sort_value.equals(getResources().getString(R.string.pop_key))) {

            movieAsyncTask2.execute(BASEURL);

        } else if (sort_value.equals(getString(R.string.rec_key))) {

            movieAsyncTask.execute(TOPRATED_BASEURL);

        }

    }

    private void setUpSharedPreferense() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        sort_value = sharedPreferences.getString(getResources().getString(R.string.pref_key), "");
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

                startActivity(new Intent(getApplicationContext(), Setting_Activity.class));
            } else {
                Toast.makeText(getApplicationContext(), "Data is Loading", Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        sort_value = sharedPreferences.getString(getResources().getString(R.string.pref_key), "");
        setMovieAdapter();
        Toast.makeText(this, sort_value, Toast.LENGTH_LONG).show();

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
                Toast.makeText(getApplicationContext(), String.valueOf(moviesData.get(0).getId()), Toast.LENGTH_LONG).show();
                homeBinding.rvMoviesInGrid.setVisibility(View.VISIBLE);
                moviesAdapter.notifyDataSetChanged();
                progressDialog.dismiss();
            } else {
                progressDialog.dismiss();
                homeBinding.rvMoviesInGrid.setVisibility(View.GONE);
                homeBinding.notFoundImage.setVisibility(View.VISIBLE);
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
                            object.optDouble(VOTE_AVERAGE), ISFAVOURITE));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
