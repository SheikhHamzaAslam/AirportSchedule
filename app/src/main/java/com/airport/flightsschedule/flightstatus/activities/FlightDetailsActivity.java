package com.airport.flightsschedule.flightstatus.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.utils.ShowNativeAd;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class FlightDetailsActivity extends AppCompatActivity {

    String flightNumberIata, flightNumberIcao, aircraftRegistration, airlineIcao, airlineName, depAirportIata, depAirportName, depCity, depCountry, departureGate, departureTerminal,
            departureBaggage, departureTime, arrAirportIata, arrAirportName, arrCity, arrCountry, arrivalTime, arrivalGate, arrivalTerminal, arrivalBaggage, status;
    String estimatedDeparture, scheduledDeparture, estimatedArrival, scheduledArrival;
    String not_ava = "N/A";

    TextView callSign, flightNumberIataTV, flightNumberIcaoTV, aircraftRegistrationTV, airlineIcaoTV, airlineNameTV, depAirportIataTV, depAirportNameTV, depCityTV, depCountryTV, departureTimeTV,
            departureGateTV, departureBaggageTV, departureTerminalTV, arrAirportIataTV, arrAirportNameTV, arrCityTV, arrCountryTV, arrivalTimeTV, arrivalGateTV, arrivalTerminalTV,
            arrivalBaggageTV, statusTV;

    TextView estimatedDepartureTV, scheduledDepartureTV, estimatedArrivalTV, scheduledArrivalTV;
    TextView scheduledTxt, estimatedTxt, arrivalScheduledTxt, arrivalEstimatedTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_backspace);
        }

        ShowNativeAd showNativeAd = new ShowNativeAd(this);
        AdRequest adRequest = new AdRequest.Builder().build();

        FrameLayout adaptiveAdContainer = findViewById(R.id.adaptive_banner_container);
        FrameLayout adaptiveAdContainerBottom = findViewById(R.id.adaptive_banner_container_bottom);

        AdView adaptiveAdView = new AdView(this);
        AdView adaptiveAdViewBottom = new AdView(this);
        AdSize adSize = new AdSize(showNativeAd.adaptiveBannerAdWidth(), 60);
        AdSize adSizeBottom = new AdSize(showNativeAd.adaptiveBannerAdWidth(), 80);
        adaptiveAdView.setAdUnitId(getString(R.string.main_adaptive_banner));
        adaptiveAdViewBottom.setAdUnitId(getString(R.string.main_adaptive_banner));
        adaptiveAdView.setAdSize(adSize);
        adaptiveAdViewBottom.setAdSize(adSizeBottom);
        adaptiveAdContainer.addView(adaptiveAdView);
        adaptiveAdContainerBottom.addView(adaptiveAdViewBottom);

        adaptiveAdView.loadAd(adRequest);
        adaptiveAdViewBottom.loadAd(adRequest);

        flightNumberIata = getIntent().getStringExtra("flightNumberIata");
        flightNumberIcao = getIntent().getStringExtra("flightNumberIcao");
        aircraftRegistration = getIntent().getStringExtra("aircraftRegistration");
        airlineIcao = getIntent().getStringExtra("airlineIcao");
        airlineName = getIntent().getStringExtra("airlineName");
        depAirportIata = getIntent().getStringExtra("depAirportIata");
        depAirportName = getIntent().getStringExtra("depAirportName");
        depCity = getIntent().getStringExtra("depCity");
        depCountry = getIntent().getStringExtra("depCountry");
        departureGate = getIntent().getStringExtra("departureGate");
        departureTerminal = getIntent().getStringExtra("departureTerminal");
        departureBaggage = getIntent().getStringExtra("departureBaggage");
        departureTime = getIntent().getStringExtra("departureTime");
        arrAirportIata = getIntent().getStringExtra("arrAirportIata");
        arrAirportName = getIntent().getStringExtra("arrAirportName");
        arrCity = getIntent().getStringExtra("arrCity");
        arrCountry = getIntent().getStringExtra("arrCountry");
        arrivalGate = getIntent().getStringExtra("arrivalGate");
        arrivalTerminal = getIntent().getStringExtra("arrivalTerminal");
        arrivalBaggage = getIntent().getStringExtra("arrivalBaggage");
        arrivalTime = getIntent().getStringExtra("arrivalTime");
        estimatedDeparture = getIntent().getStringExtra("estimatedDeparture");
        scheduledDeparture = getIntent().getStringExtra("scheduledDeparture");
        estimatedArrival = getIntent().getStringExtra("estimatedArrival");
        scheduledArrival = getIntent().getStringExtra("scheduledArrival");
        status = getIntent().getStringExtra("status");

        callSign = findViewById(R.id.callSign);
        flightNumberIataTV = findViewById(R.id.flightNumberIATA);
        flightNumberIcaoTV = findViewById(R.id.flightNumberICAO);
        aircraftRegistrationTV = findViewById(R.id.aircraftReg);
        airlineIcaoTV = findViewById(R.id.airlineICAO);
        airlineNameTV = findViewById(R.id.airlineName);
        depAirportIataTV = findViewById(R.id.depAirportCode);
        depAirportNameTV = findViewById(R.id.depAirportName);
        depCityTV = findViewById(R.id.depCity);
        depCountryTV = findViewById(R.id.depCountry);
        depCountryTV.setText(depCountry);
        departureGateTV = findViewById(R.id.depGate);
        departureTerminalTV = findViewById(R.id.depTerminal);
        departureBaggageTV = findViewById(R.id.depBaggage);
        departureTimeTV = findViewById(R.id.departureTime);
        estimatedDepartureTV = findViewById(R.id.depEstimated);
        scheduledDepartureTV = findViewById(R.id.depScheduled);
        arrAirportIataTV = findViewById(R.id.arrAirportCode);
        arrAirportNameTV = findViewById(R.id.arrAirportName);
        arrCityTV = findViewById(R.id.arrCity);
        arrCountryTV = findViewById(R.id.arrCountry);
        arrivalGateTV = findViewById(R.id.arrGate);
        arrivalTerminalTV = findViewById(R.id.arrTerminal);
        arrivalBaggageTV = findViewById(R.id.arrBaggage);
        arrivalTimeTV = findViewById(R.id.arrivalTime);
        estimatedArrivalTV = findViewById(R.id.arrEstimated);
        scheduledArrivalTV = findViewById(R.id.arrScheduled);
        scheduledTxt = findViewById(R.id.scheduledTxt);
        estimatedTxt = findViewById(R.id.estimatedTxt);
        arrivalScheduledTxt = findViewById(R.id.arrScheduledTxt);
        arrivalEstimatedTxt = findViewById(R.id.arrEstimatedTxt);
        statusTV = findViewById(R.id.flightStatus);
        arrCityTV = findViewById(R.id.arrCity);
        arrCountryTV = findViewById(R.id.arrCountry);
        depCityTV = findViewById(R.id.depCity);
        depCountryTV = findViewById(R.id.depCountry);

        callSign.setText(flightNumberIata);
        depAirportIataTV.setText(depAirportIata);
        depAirportNameTV.setText(depAirportName);
        departureTimeTV.setText(departureTime);
        arrAirportIataTV.setText(arrAirportIata);
        arrAirportNameTV.setText(arrAirportName);
        arrivalTimeTV.setText(arrivalTime);
        aircraftRegistrationTV.setText(aircraftRegistration);
        flightNumberIataTV.setText(flightNumberIata);
        flightNumberIcaoTV.setText(flightNumberIcao);
        statusTV.setText(status);
        airlineIcaoTV.setText(airlineIcao);
        airlineNameTV.setText(airlineName);

        depCityTV.setText(depCity);
        depCountryTV.setText(depCountry);
        departureGateTV.setText(departureGate);
        departureTerminalTV.setText(departureTerminal);
        departureBaggageTV.setText(departureBaggage);
        arrCityTV.setText(arrCity);
        arrCountryTV.setText(arrCountry);
        arrivalGateTV.setText(arrivalGate);
        arrivalTerminalTV.setText(arrivalTerminal);
        arrivalBaggageTV.setText(arrivalBaggage);

        if (flightNumberIata.isEmpty()) {
            flightNumberIataTV.setText(not_ava);
            if (!flightNumberIcao.isEmpty()) {
                callSign.setText(flightNumberIcao);
            } else {
                callSign.setText(not_ava);
            }
        }

        if (airlineName.isEmpty() || airlineName.equals("empty"))
            airlineNameTV.setText(not_ava);

        if (!scheduledDeparture.matches("N/A")) {
            scheduledDepartureTV.setText(scheduledDeparture);
        } else {
            scheduledTxt.setVisibility(View.GONE);
            scheduledDepartureTV.setVisibility(View.GONE);
        }
        if (!estimatedDeparture.matches("N/A")) {
            estimatedDepartureTV.setText(estimatedDeparture);
        } else {
            estimatedTxt.setVisibility(View.GONE);
            estimatedDepartureTV.setVisibility(View.GONE);
        }
        if (!scheduledArrival.matches("N/A")) {
            scheduledArrivalTV.setText(scheduledArrival);
        } else {
            arrivalScheduledTxt.setVisibility(View.GONE);
            scheduledArrivalTV.setVisibility(View.GONE);
        }
        if (!estimatedArrival.matches("N/A")) {
            estimatedArrivalTV.setText(estimatedArrival);
        } else {
            arrivalEstimatedTxt.setVisibility(View.GONE);
            estimatedArrivalTV.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);
    }
}