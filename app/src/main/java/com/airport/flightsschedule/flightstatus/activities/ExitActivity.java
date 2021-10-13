package com.airport.flightsschedule.flightstatus.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);

        ShowNativeAd showNativeAd = new ShowNativeAd(this);
        AdRequest adRequest = new AdRequest.Builder().build();

        FrameLayout adaptiveAdContainer = findViewById(R.id.adaptive_banner_container);
        AdView rectangleAd = findViewById(R.id.adView);
        AdView adaptiveAdView = new AdView(this);
        AdSize adSize = new AdSize(showNativeAd.adaptiveBannerAdWidth(), 80);
        adaptiveAdView.setAdUnitId(getString(R.string.main_adaptive_banner));
        adaptiveAdView.setAdSize(adSize);
        adaptiveAdContainer.addView(adaptiveAdView);
        rectangleAd.loadAd(adRequest);
        adaptiveAdView.loadAd(adRequest);

        ImageView back = findViewById(R.id.back);
        Button exit = findViewById(R.id.exit);
        Button rate = findViewById(R.id.rate);

        back.setOnClickListener(v -> onBackPressed());

        exit.setOnClickListener(v -> {
            finish();
            finishAffinity();
        });

        rate.setOnClickListener(v -> {
            String rate_url = "https://play.google.com/store/apps/details?id=com.airport.flightsschedule.flightstatus";
            Intent rate_us = new Intent(Intent.ACTION_VIEW);
            rate_us.setData(Uri.parse(rate_url));
            startActivity(rate_us);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}