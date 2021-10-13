package com.airport.flightsschedule.flightstatus.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.utils.AutoCompleteTextAdapter;
import com.airport.flightsschedule.flightstatus.utils.BottomDrawerDialog;
import com.airport.flightsschedule.flightstatus.utils.CSVFileReader;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    AutoCompleteTextView airportAutoComplete;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    InterstitialAd mAdMobInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        AdView rectangleAd = findViewById(R.id.adView);
        rectangleAd.loadAd(new AdRequest.Builder().build());

        requestInterstitialAd();

        airportAutoComplete = findViewById(R.id.searchAirport);

        CSVFileReader csvReader = new CSVFileReader(this, "airports.csv");
        List<String[]> airportsList = new ArrayList<>();
        ArrayList<String> airports = new ArrayList<>();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        try {
            airportsList = csvReader.readCSV();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("airports", e.toString());
        }

        for (int i = 0; i < airportsList.size(); i++) {
            String airport = airportsList.get(i)[3].concat(", ").concat(airportsList.get(i)[0].concat(", ").concat(airportsList.get(i)[1]).concat(", ").concat(airportsList.get(i)[4]));
            airports.add(airport);
        }

        AutoCompleteTextAdapter autoCompleteAdapter = new AutoCompleteTextAdapter(this, R.layout.drop_down_list_item, R.id.list_item, airports);
        airportAutoComplete.setThreshold(2);
        airportAutoComplete.setAdapter(autoCompleteAdapter);
        airportAutoComplete.setDropDownBackgroundResource(R.drawable.search_background);

        airportAutoComplete.setOnItemClickListener((parent, view1, position, id) -> {
            Intent airportActivity = new Intent(MainActivity.this, ArrivalsDeparturesActivity.class);
            String[] airport = parent.getItemAtPosition(position).toString().split(",");
            String airportCode = airport[0];
            String airportName = airport[3];
            airportActivity.putExtra("airportCode", airportCode);
            airportActivity.putExtra("airportName", airportName);
            airportAutoComplete.setText("");
            startActivity(airportActivity);
            hideKeyboard(MainActivity.this);
        });

        navigationView = findViewById(R.id.nav);
        drawerLayout = findViewById(R.id.drawer_layout);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerToggle.setDrawerIndicatorEnabled(false);

        navigationView.setItemIconTintList(null);

        ImageView more = findViewById(R.id.more);
        ImageView close = navigationView.getHeaderView(0).findViewById(R.id.close);

        more.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        if (close != null)
            close.setOnClickListener(v -> drawerLayout.closeDrawers());

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.rateUs) {
                rateApp();
            } else if (id == R.id.shareApp) {
                shareApp();
            } else if (id == R.id.privacyPolicy) {
                if (mAdMobInterstitial != null) {
                    mAdMobInterstitial.show(MainActivity.this);
                    mAdMobInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                            super.onAdFailedToShowFullScreenContent(adError);
                            requestInterstitialAd();
                            privacyPolicy();
                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            super.onAdDismissedFullScreenContent();
                            requestInterstitialAd();
                            privacyPolicy();
                        }
                    });
                } else {
                    requestInterstitialAd();
                    privacyPolicy();
                }
            }
            return true;
        });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        airportAutoComplete.setText("");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else {
            Intent intent = new Intent(MainActivity.this, ExitActivity.class);
            startActivity(intent);
        }
    }

    private void rateApp() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.rate_us_layout, null, false);
        ImageView nope = view.findViewById(R.id.nope);
        ImageView yup = view.findViewById(R.id.yup);
        TextView no = view.findViewById(R.id.no);
        TextView yes = view.findViewById(R.id.yes);
        no.setOnClickListener(v -> alertDialog.dismiss());
        nope.setOnClickListener(v -> alertDialog.dismiss());
        yes.setOnClickListener(v -> {
            rateUs();
            alertDialog.dismiss();
        });
        yup.setOnClickListener(v -> {
            rateUs();
            alertDialog.dismiss();
        });
        alertDialog.setView(view);
        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
        drawerLayout.closeDrawers();
    }

    private void rateUs() {
        String rate_url = "https://play.google.com/store/apps/details?id=com.airport.flightsschedule.flightstatus";
        Intent rate_us = new Intent(Intent.ACTION_VIEW);
        rate_us.setData(Uri.parse(rate_url));
        startActivity(rate_us);
        drawerLayout.closeDrawers();
    }

    private void shareApp() {
        Intent share = new Intent(Intent.ACTION_SEND);
        String url = "https://play.google.com/store/apps/details?id=com.airport.flightsschedule.flightstatus";
        String shareText = getString(R.string.try_flight_tracker);
        share.setType("text/plain");
        share.setAction(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        share.putExtra(Intent.EXTRA_TEXT, shareText.concat("\n\n").concat(Uri.parse(url).toString()));
        startActivity(Intent.createChooser(share, getString(R.string.app_name)));
        drawerLayout.closeDrawers();
    }

    private void privacyPolicy() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).create();
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.privacy_policy_layout, null, false);
        RadioButton privacyLink = view.findViewById(R.id.privacyLink);
        LinearLayout unifiedLayout = view.findViewById(R.id.ad_unified_layout);
        NativeAdView nativeAdView = view.findViewById(R.id.ad_view);
        Button accept = view.findViewById(R.id.accept);
        accept.setText(getString(R.string.accept));
        ShowNativeAd showNativeAd = new ShowNativeAd(MainActivity.this);
        showNativeAd.showAdMobNativeAd(nativeAdView, unifiedLayout, getString(R.string.main_native_ad));
        privacyLink.setOnClickListener(v -> {
            String privacy_link_url = "http://pub.leapfitnessgroup.com/";
            Intent privacy_link = new Intent(Intent.ACTION_VIEW);
            privacy_link.setData(Uri.parse(privacy_link_url));
            try {
                startActivity(privacy_link);
            } catch (RuntimeException e) {
                Toast.makeText(MainActivity.this, getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
            }
        });
        accept.setOnClickListener(v -> dialog.dismiss());
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setView(view);
        dialog.show();
        drawerLayout.closeDrawers();
    }

    private void requestInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                MainActivity.this, getString(R.string.main_interstitial_ad), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        mAdMobInterstitial = interstitialAd;
                        Log.v("Ad", "AdMob Interstitial Ad Successfully Loaded!");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        mAdMobInterstitial = null;
                        Log.v("Ad", "AdMob Interstitial Ad Failed to Load!");
                    }
                }
        );
    }

    private void showBottomDrawerDialog() {
        BottomDrawerDialog drawerDialog = new BottomDrawerDialog();
        if (drawerDialog.getDialog() != null) {
            if (drawerDialog.getDialog().getWindow() != null)
                drawerDialog.getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        drawerDialog.show(getSupportFragmentManager(), "BottomDrawerDialog");
    }
}