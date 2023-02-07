package com.airport.flightsschedule.flightstatus.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airport.flightsschedule.flightstatus.adapters.SearchHistoryAdapter;
import com.airport.flightsschedule.flightstatus.databinding.ActivitySearchBinding;
import com.airport.flightsschedule.flightstatus.viewmodel.HistoryViewModel;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySearchBinding binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HistoryViewModel historyViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        SearchHistoryAdapter adapter = new SearchHistoryAdapter(new SearchHistoryAdapter.SearchHistoryDiff());

        historyViewModel.getSearchHistory().observe(this, adapter::submitList);

        binding.recentSearchesRV.setLayoutManager(new LinearLayoutManager(this));
        binding.recentSearchesRV.setAdapter(adapter);

        binding.deleteAll.setOnClickListener(v -> historyViewModel.deleteAll());
    }
}