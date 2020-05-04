package com.example.mymovie.Utils;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class MoviesDetailsUtils {
    private static final String SCHEME = "https";
    private static final String AUTHORITY = "api.themoviedb.org";
    private static final String PATH_1 = "3";
    private static final String PATH_2 = "movie";
    private static final String APPEND_TO_PATH_KEY = "append_to_response";
    private static final String APPEND_TO_PATH_KEY_VALUE = "videos,reviews";
    private static final String API_KEY = "api_key";

    //https://api.themoviedb.org/3/movie/157336?api_key=247e995e482deb44deb4808aea5db18e&append_to_response=videos,reviews
    public static URL getUrl(String id) {
        URL url1 = null;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH_1)
                .appendPath(PATH_2)
                .appendPath(id)
                .appendQueryParameter(API_KEY, Constants.KEY)
                .appendQueryParameter(APPEND_TO_PATH_KEY, APPEND_TO_PATH_KEY_VALUE);
        String myUrl = builder.build().toString();
        try {
            url1 = new URL(myUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url1;
    }

}
