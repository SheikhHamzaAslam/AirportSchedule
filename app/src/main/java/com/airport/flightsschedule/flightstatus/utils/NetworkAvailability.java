package com.airport.flightsschedule.flightstatus.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AppCompatActivity;

public class NetworkAvailability {

    public static boolean isNetworkAvailable(Context context){
        AppCompatActivity activity = (AppCompatActivity) context;
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        if (activeNetworkInfo != null) {
            // connected to the internet
            // connected to mobile data
            return activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        // not connected to the internet
        return false;
    }
}
