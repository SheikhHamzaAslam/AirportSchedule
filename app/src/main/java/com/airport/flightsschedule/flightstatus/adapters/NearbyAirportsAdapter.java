package com.airport.flightsschedule.flightstatus.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airport.flightsschedule.flightstatus.R;
import com.airport.flightsschedule.flightstatus.activities.ArrivalsDeparturesActivity;
import com.airport.flightsschedule.flightstatus.database.SearchHistory;
import com.airport.flightsschedule.flightstatus.flights.Airport;
import com.airport.flightsschedule.flightstatus.viewmodel.HistoryViewModel;

import java.util.ArrayList;

public class NearbyAirportsAdapter extends RecyclerView.Adapter<NearbyAirportsAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Airport> airports;
    private final HistoryViewModel historyViewModel;

    public NearbyAirportsAdapter(Context context, ArrayList<Airport> airports, HistoryViewModel historyViewModel) {
        this.context = context;
        this.airports = airports;
        this.historyViewModel = historyViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nearby_airport_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.airportName.setText(airports.get(position).getNameAirport());
        holder.airportLocation.setText(airports.get(position).getCodeIataCity());
        holder.itemView.setOnClickListener(v -> {
            Intent airportActivity = new Intent(context, ArrivalsDeparturesActivity.class);
            String airportCode = airports.get(position).getCodeIataAirport();
            String airportName = airports.get(position).getNameAirport();
            String cityName = airports.get(position).getCodeIataCity();
            SearchHistory searchHistory = new SearchHistory(airports.get(position).getNameAirport(),
                    airports.get(position).getCodeIataCity(),
                    airports.get(position).getCodeIataAirport(),
                    airports.get(position).getCodeIcaoAirport(),
                    airports.get(position).getLatitudeAirport(),
                    airports.get(position).getLongitudeAirport(),
                    airports.get(position).getDistance());
            historyViewModel.insert(searchHistory);
            airportActivity.putExtra("airportCode", airportCode);
            airportActivity.putExtra("airportName", airportName);
            Log.d("airport", "airport code: " + airportCode);
            Log.d("airport", "airport name: " + airportName);
            Log.d("airport", "city name: " + cityName);
            //context.startActivity(airportActivity);
        });
    }

    @Override
    public int getItemCount() {
        return airports.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {

        TextView airportName, airportLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            airportName = itemView.findViewById(R.id.airportName);
            airportLocation = itemView.findViewById(R.id.airportLocation);
        }
    }
}
