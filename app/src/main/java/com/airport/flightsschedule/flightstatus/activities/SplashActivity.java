package com.airport.flightsschedule.flightstatus.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    public static String APIKey;
    private AlertDialog alertDialog;
    private InterstitialAd mAdMobInterstitial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseApp.initializeApp(SplashActivity.this);
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(900)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(SplashActivity.this, task -> {
            if (task.isSuccessful())
                APIKey = FirebaseRemoteConfig.getInstance().getString("aviation_edge_key");
            else
                Log.d("Exception", "Task Exception: " + task.getException());
        });

        ShowNativeAd showNativeAd = new ShowNativeAd(this);
        AdRequest adRequest = new AdRequest.Builder().build();

        FrameLayout adaptiveAdContainer = findViewById(R.id.adaptive_banner_container);
        AdView rectangleAd = findViewById(R.id.adView);
        AdView adaptiveAdView = new AdView(this);
        AdSize adSize = new AdSize(showNativeAd.adaptiveBannerAdWidth(), 80);
        adaptiveAdView.setAdUnitId(getString(R.string.splash_adaptive_banner));
        adaptiveAdView.setAdSize(adSize);
        adaptiveAdContainer.addView(adaptiveAdView);

        alertDialog = new AlertDialog.Builder(this).create();
        View view = getLayoutInflater().inflate(R.layout.privacy_policy_layout, null, false);
        TextView privacy = view.findViewById(R.id.privacyPolicy);
        LinearLayout unifiedLayout = view.findViewById(R.id.ad_unified_layout);
        NativeAdView nativeAdView = view.findViewById(R.id.ad_view);
        RadioButton privacyLink = view.findViewById(R.id.privacyLink);
        Button accept = view.findViewById(R.id.accept);

        privacy.setText(getString(R.string.user_agreement));
        accept.setText(getString(R.string.accept_continue));

        showNativeAd.showAdMobNativeAd(nativeAdView, unifiedLayout, getString(R.string.main_native_ad));

        privacyLink.setOnClickListener(v -> {
            String privacy_link_url = "http://pub.leapfitnessgroup.com/";
            Intent privacy_link = new Intent(Intent.ACTION_VIEW);
            privacy_link.setData(Uri.parse(privacy_link_url));
            try {
                startActivity(privacy_link);
            } catch (RuntimeException e) {
                Toast.makeText(SplashActivity.this, getString(R.string.no_browser), Toast.LENGTH_SHORT).show();
            }
        });

        accept.setOnClickListener(v -> {
            alertDialog.dismiss();
            if (mAdMobInterstitial != null) {
                mAdMobInterstitial.show(SplashActivity.this);
                mAdMobInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        new Handler().postDelayed(() -> {
                            finish();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }, 1000);
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        new Handler().postDelayed(() -> {
                            finish();
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        }, 1000);
                    }
                });
            } else {
                new Handler().postDelayed(() -> {
                    finish();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }, 1000);
            }
        });

        alertDialog.setView(view);
        alertDialog.setOnCancelListener(dialog -> finishAffinity());

        if (alertDialog.getWindow() != null)
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if (!verifyInstallerId()) {
            showPiracyCheckerDialog();
        } else {
            rectangleAd.loadAd(adRequest);
            adaptiveAdView.loadAd(adRequest);
            requestInterstitialAd();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!isFinishing())
                    alertDialog.show();
            }, 6000);
        }
    }

    private void requestInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                SplashActivity.this, getString(R.string.splash_interstitial_ad), adRequest, new InterstitialAdLoadCallback() {
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

    private boolean verifyInstallerId() {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = getPackageManager().getInstallerPackageName(getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }

    private void showPiracyCheckerDialog() {
        Dialog alert = new Dialog(SplashActivity.this);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.appcheckerrdialog);
        alert.setCancelable(false);

        Button accept = alert.findViewById(R.id.accept);
        ImageView close = alert.findViewById(R.id.close);

        close.setOnClickListener(v -> finishAffinity());

        accept.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.airport.flightsschedule.flightstatus")));
            } catch (RuntimeException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.airport.flightsschedule.flightstatus")));
            }
        });

        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (alertDialog.isShowing() && !isFinishing())
            alertDialog.cancel();
    }
}