package com.airport.flightsschedule.flightstatus.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.airport.flightsschedule.flightstatus.database.SearchHistory;
import com.airport.flightsschedule.flightstatus.databinding.NearbyAirportItemBinding;

public class SearchHistoryAdapter extends ListAdapter<SearchHistory, SearchHistoryAdapter.HistoryViewHolder> {

    public SearchHistoryAdapter(@NonNull DiffUtil.ItemCallback<SearchHistory> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        NearbyAirportItemBinding binding = NearbyAirportItemBinding.inflate(layoutInflater, parent, false);
        return HistoryViewHolder.create(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SearchHistory currentItem = getItem(position);
        holder.bind(currentItem.getAirportName(), currentItem.getCityName());
        Log.d("history", "size: " + getCurrentList().size());
    }

    protected static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private final NearbyAirportItemBinding binding;

        public HistoryViewHolder(@NonNull NearbyAirportItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public static HistoryViewHolder create(NearbyAirportItemBinding binding) {
            return new HistoryViewHolder(binding);
        }

        public void bind(String airportName, String cityName) {
            binding.airportName.setText(airportName);
            binding.airportLocation.setText(cityName);
            Log.d("history", "airport: " + airportName);
            Log.d("history", "city: " + cityName);
        }
    }

    public static class SearchHistoryDiff extends DiffUtil.ItemCallback<SearchHistory> {
        @Override
        public boolean areItemsTheSame(@NonNull SearchHistory oldItem, @NonNull SearchHistory newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchHistory oldItem, @NonNull SearchHistory newItem) {
            return oldItem.getAirportName().equals(newItem.getAirportName());
        }
    }
}