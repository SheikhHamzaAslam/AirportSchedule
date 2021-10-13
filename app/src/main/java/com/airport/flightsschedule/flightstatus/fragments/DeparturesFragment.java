package com.airport.flightsschedule.flightstatus.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.activities.SplashActivity;
import com.airport.flightsschedule.flightstatus.flights.GetFlights;
import com.airport.flightsschedule.flightstatus.flights.GetFlights.OnAPIResponse;

public class DeparturesFragment extends Fragment implements OnAPIResponse {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_IATA_CODE = "iataCode";

    // TODO: Rename and change types of parameters
    private String iataCode;

    private Context context;
    private ProgressBar loading;
    private RecyclerView departuresRV;
    private FrameLayout overlay;
    private GetFlights getFlights;

    public DeparturesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            iataCode = getArguments().getString(ARG_IATA_CODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_departures, container, false);
        departuresRV = view.findViewById(R.id.departuresRV);
        loading = view.findViewById(R.id.loading);
        overlay = view.findViewById(R.id.overlay);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFlights = new GetFlights(context, loading);
        getFlights.setDeparturesRV(departuresRV);
        getFlights.setOnAPIResponse(this);
        new Handler(Looper.getMainLooper()).postDelayed(() -> getFlights.getAirportSchedule(departuresRV, SplashActivity.APIKey, iataCode, "departure"), 1000);
    }

    @Override
    public void onAPIResponse(String type) {
        if(type.equals("departure"))
            new Handler(Looper.getMainLooper()).postDelayed(() -> overlay.setVisibility(View.GONE), 1000);
    }

    @Override
    public void onDataRequest(String type) {
        if(type.equals("departure"))
            new Handler(Looper.getMainLooper()).postDelayed(() -> overlay.setVisibility(View.VISIBLE), 250);
    }
}