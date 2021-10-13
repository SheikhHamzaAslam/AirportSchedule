package com.airport.flightsschedule.flightstatus.activities;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.fragments.ArrivalsFragment;
import com.airport.flightsschedule.flightstatus.fragments.DeparturesFragment;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class ArrivalsDeparturesActivity extends AppCompatActivity {

    private CardView arrCardView, depCardView;
    private ImageView arrival, departure;
    private RelativeLayout tabsContainer;
    private ViewPager viewPager;

    public static boolean isLoading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arrivals_departures);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        String IATA_Code = getIntent().getStringExtra("airportCode");
        String AirportName = getIntent().getStringExtra("airportName");

        ImageView back = findViewById(R.id.back);
        TextView airportName = findViewById(R.id.name);
        TextView airportCode = findViewById(R.id.shortName);
        FrameLayout adaptiveAdContainer = findViewById(R.id.adaptive_banner_container);

        ShowNativeAd showNativeAd = new ShowNativeAd(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        AdView adaptiveAdView = new AdView(this);
        AdSize adSize = new AdSize(showNativeAd.adaptiveBannerAdWidth(), 80);
        adaptiveAdView.setAdUnitId(getString(R.string.main_adaptive_banner));
        adaptiveAdView.setAdSize(adSize);
        adaptiveAdContainer.addView(adaptiveAdView);
        adaptiveAdView.loadAd(adRequest);

        tabsContainer = findViewById(R.id.tabsContainer);
        arrCardView = findViewById(R.id.arrivalCardView);
        arrival = findViewById(R.id.arrival);
        depCardView = findViewById(R.id.departureCardVew);
        departure = findViewById(R.id.departure);

        Bundle args = new Bundle();
        args.putString("iataCode", IATA_Code);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(getString(R.string.arrivals), ArrivalsFragment.class, args)
                .add(getString(R.string.departures), DeparturesFragment.class, args)
                .create());

        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        airportName.setText(AirportName);
        airportCode.setText(IATA_Code);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        depCardView.setBackground(new ColorDrawable(Color.TRANSPARENT));

        back.setOnClickListener(v -> onBackPressed());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        arrival.setOnClickListener(v -> {
            switchTab(0);
            viewPager.setCurrentItem(0);
        });

        departure.setOnClickListener(v -> {
            switchTab(1);
            viewPager.setCurrentItem(1);
        });
    }

    private void switchTab(int position) {
        switch (position) {
            case 0:
                arrCardView.setBackground(ContextCompat.getDrawable(ArrivalsDeparturesActivity.this, R.drawable.round_corners_bg));
                arrival.setScaleType(ImageView.ScaleType.CENTER_CROP);
                arrival.setImageResource(R.drawable.active_arr);

                departure.setScaleType(ImageView.ScaleType.FIT_CENTER);
                depCardView.setBackground(new ColorDrawable(Color.TRANSPARENT));
                departure.setImageResource(R.drawable.ic_departure_png);

                tabsContainer.requestLayout();
                arrCardView.forceLayout();
                depCardView.forceLayout();
                arrival.forceLayout();
                departure.forceLayout();
                break;

            case 1:
                arrival.setScaleType(ImageView.ScaleType.FIT_CENTER);
                arrCardView.setBackground(new ColorDrawable(Color.TRANSPARENT));
                arrival.setImageResource(R.drawable.ic_arrival_png);

                departure.setScaleType(ImageView.ScaleType.CENTER_CROP);
                depCardView.setBackground(ContextCompat.getDrawable(ArrivalsDeparturesActivity.this, R.drawable.round_corners_bg));
                departure.setImageResource(R.drawable.active_dep);

                tabsContainer.requestLayout();
                arrCardView.forceLayout();
                depCardView.forceLayout();
                arrival.forceLayout();
                departure.forceLayout();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(isLoading)
            Toast.makeText(this, "Please wait until data loads", Toast.LENGTH_SHORT).show();
        else
            super.onBackPressed();
    }
}