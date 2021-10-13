package com.airport.flightsschedule.flightstatus;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        MobileAds.initialize(this);
        AppOpenManager appOpenManager = new AppOpenManager(this);
        appOpenManager.onStart();
    }
}
