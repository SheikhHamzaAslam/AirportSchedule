package com.airport.flightsschedule.flightstatus.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.airport.flightsschedule.flightstatus.database.SearchHistory;
import com.airport.flightsschedule.flightstatus.repository.HistoryRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final HistoryRepository historyRepository;
    private final LiveData<List<SearchHistory>> searchHistory;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        historyRepository = new HistoryRepository(application);
        searchHistory = historyRepository.getSearchHistory();
    }

    public LiveData<List<SearchHistory>> getSearchHistory() {
        return searchHistory;
    }

    public void insert(SearchHistory searchHistory) {
        historyRepository.insert(searchHistory);
    }

    public void delete(SearchHistory searchHistory) {
        historyRepository.delete(searchHistory);
    }

    public void deleteAll() {
        historyRepository.deleteAll();
    }
}
