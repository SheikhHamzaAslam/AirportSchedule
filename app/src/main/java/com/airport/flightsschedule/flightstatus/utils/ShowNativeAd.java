package com.airport.flightsschedule.flightstatus.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airport.flightsschedule.flightstatus.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAdView;

import org.jetbrains.annotations.NotNull;

public class ShowNativeAd {

    private final Context context;
    private AdMobNativeAdLoadListener nativeAdLoadListener;

    public ShowNativeAd(Context context) {
        this.context = context;
    }

    public void showAdMobNativeAd(NativeAdView adView, LinearLayout adUnifiedLayout, String nativeAdId) {
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        AdLoader.Builder builder = new AdLoader.Builder(context, nativeAdId);
        AdLoader adLoader = builder.forNativeAd(nativeAd -> {
            adUnifiedLayout.setVisibility(View.VISIBLE);
            populateNativeAdView(nativeAd, adView);
            Log.v("NativeAd", "AdMob Native Advanced Successfully Load!");
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.v("NativeAd", "AdMob Native Advanced Failed to Load!");
            }
        }).build();
        adLoader.loadAds(new AdRequest.Builder().build(), 3);
    }

    public void showAdMobNativeAd(LinearLayout adUnifiedLayout) {
        // Add the Ad view into the ad container.
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        View view = inflater.inflate(R.layout.unified_ad, adUnifiedLayout, false);
        adUnifiedLayout.addView(view);

        //Fetch NativeAdView
        NativeAdView adView = view.findViewById(R.id.ad_view);
        adView.setMediaView(adView.findViewById(R.id.ad_media));
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        AdLoader.Builder builder = new AdLoader.Builder(context, context.getString(R.string.main_native_ad));
        AdLoader adLoader = builder.forNativeAd(nativeAd -> {
            adUnifiedLayout.setVisibility(View.VISIBLE);
            populateNativeAdView(nativeAd, adView);
            if(nativeAdLoadListener != null)
                nativeAdLoadListener.onNativeAdLoaded();
            Log.v("NativeAd", "AdMob Native Advanced Successfully Load!");
        }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.v("NativeAd", "AdMob Native Advanced Failed to Load!");
            }
        }).build();
        adLoader.loadAds(new AdRequest.Builder().build(), 3);
    }

    private void populateNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        try {
            if (adView.getHeadlineView() != null)
                ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
            if (adView.getBodyView() != null)
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            if (adView.getCallToActionView() != null)
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        com.google.android.gms.ads.nativead.NativeAd.Image icon = nativeAd.getIcon();

        if (icon == null) {
            if (adView.getIconView() != null)
                adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            if (adView.getIconView() != null) {
                ((ImageView) adView.getIconView()).setImageDrawable(icon.getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }
        }

        if (nativeAd.getPrice() == null) {
            if (adView.getPriceView() != null)
                adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            if (adView.getPriceView() != null) {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }
        }

        if (nativeAd.getStore() == null) {
            if (adView.getStoreView() != null)
                adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            if (adView.getStoreView() != null) {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }
        }

        if (nativeAd.getStarRating() == null) {
            if (adView.getStarRatingView() != null)
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            if (adView.getStarRatingView() != null) {
                ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }
        }

        if (nativeAd.getAdvertiser() == null) {
            if (adView.getAdvertiserView() != null)
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            if (adView.getAdvertiserView() != null) {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd);
    }

    public interface AdMobNativeAdLoadListener {
        void onNativeAdLoaded();
    }

    public void setNativeAdLoadListener(AdMobNativeAdLoadListener nativeAdLoadListener) {
        this.nativeAdLoadListener = nativeAdLoadListener;
    }

    public int adaptiveBannerAdWidth() {
        AppCompatActivity activity = (AppCompatActivity) context;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        return (int) (widthPixels / density);
    }
}