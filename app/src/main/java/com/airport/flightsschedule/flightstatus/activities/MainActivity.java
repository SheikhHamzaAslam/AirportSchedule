package com.airport.flightsschedule.flightstatus.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.flights.GetFlights;
import com.airport.flightsschedule.flightstatus.utils.AutoCompleteTextAdapter;
import com.airport.flightsschedule.flightstatus.utils.BottomDrawerDialog;
import com.airport.flightsschedule.flightstatus.utils.CSVFileReader;
import com.airport.flightsschedule.flightstatus.utils.CheckLocationEnabled;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.airport.flightsschedule.flightstatus.viewmodel.HistoryViewModel;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    AutoCompleteTextView airportAutoComplete;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    private HistoryViewModel historyViewModel;
    private RecyclerView nearbyAirportsRV;
    private LocationManager mLocationManager;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private InterstitialAd mAdMobInterstitial;
    private int i = 0;

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

        nearbyAirportsRV = findViewById(R.id.nearbyAirportsRV);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 98);
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                showLocationPermissionDialog();
            } else {
                if (CheckLocationEnabled.init(this).isLocationEnabled()) {
                    requestLocationUpdate();
                }
            }
        } else {
            if (CheckLocationEnabled.init(this).isLocationEnabled()) {
                requestLocationUpdate();
            }
        }

        historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

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
        TextView touchView = findViewById(R.id.touchView);
        ImageView close = navigationView.getHeaderView(0).findViewById(R.id.close);

        more.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawers();
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        if (close != null)
            close.setOnClickListener(v -> drawerLayout.closeDrawers());

        touchView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));

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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 98) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showLocationPermissionDialog();
                Log.d("permission", "permission not granted");
            } else {
                if (CheckLocationEnabled.init(this).isLocationEnabled()) {
                    requestLocationUpdate();
                }
                Log.d("permission", "permission granted");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 98) {
            if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        requestLocationUpdate();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 98);
                    }
                } else {
                    requestLocationUpdate();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && i == 0) {
            Log.d("airports", "Location isn't null");
            GetFlights getFlights = new GetFlights(this, null);
            getFlights.getNearbyAirports(nearbyAirportsRV, historyViewModel, SplashActivity.APIKey, location.getLatitude(), location.getLongitude());
            i++;
        } else {
            Log.d("airports", "Location is null");
        }
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

    private void showLocationPermissionDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Permission Required");
        alertDialog.setMessage("Location Permission is required for accessing the nearby airports.");
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog, which) -> dialog.dismiss());
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Allow", (dialog, which) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 98);
                }
            }
        });
        alertDialog.show();
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

    private void requestLocationUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            } else {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
                LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null)
                            onLocationChanged(location);
                        else Log.d("airports", "Last location is null");
                    }
                });
                Log.d("airports", "Location requested");
            }
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            LocationServices.getFusedLocationProviderClient(this).getLastLocation().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if (location != null)
                        onLocationChanged(location);
                }
            });
        }
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