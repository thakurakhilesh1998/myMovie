package com.example.mymovie.Utils;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkUtils {

    private static final String API = "api_key";
    private static final String API_KEY = "247e995e482deb44deb4808aea5db18e";
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int READ_TIMEOUT = 10000;
    private static final String CONN_METHOD = "GET";

    public static URL creteUrl(String address) throws MalformedURLException {
        URL url = null;
        Uri uri = Uri.parse(address);
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter(API, API_KEY);
        url = new URL(builder.toString());
        return url;
    }

    public static String getDataFromServer(URL url) {
        String jsonRsponse = "";
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        if (url != null) {

            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod(CONN_METHOD);
                httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                httpURLConnection.setReadTimeout(READ_TIMEOUT);
                inputStream = httpURLConnection.getInputStream();
                jsonRsponse = inputStreamToString(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonRsponse;
    }

    private static String inputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }
}
