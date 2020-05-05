package com.example.mymovie.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

public class ConnectivityUtil {

    public static boolean isConnectedToInterner(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}
