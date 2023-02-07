package com.airport.flightsschedule.flightstatus.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.airport.flightsschedule.flightstatus.dao.SearchHistoryDAO;
import com.airport.flightsschedule.flightstatus.database.SearchHistory;
import com.airport.flightsschedule.flightstatus.database.SearchHistoryDatabase;

import java.util.ArrayList;
import java.util.List;

public class HistoryRepository {

    private final SearchHistoryDAO searchHistoryDAO;
    private final LiveData<List<SearchHistory>> searchHistory;

    public HistoryRepository(Application application) {
        SearchHistoryDatabase searchHistoryDatabase = SearchHistoryDatabase.getDatabase(application);
        searchHistoryDAO = searchHistoryDatabase.searchHistoryDAO();
        searchHistory = searchHistoryDAO.getSearchedAirports();
    }

    public LiveData<List<SearchHistory>> getSearchHistory() {
        return searchHistory;
    }

    public void insert(SearchHistory searchHistory) {
        SearchHistoryDatabase.databaseWriteExecutor.execute(() -> searchHistoryDAO.insert(searchHistory));
    }

    public void delete(SearchHistory searchHistory) {
        SearchHistoryDatabase.databaseWriteExecutor.execute(() -> searchHistoryDAO.delete(searchHistory));
    }

    public void deleteAll() {
        SearchHistoryDatabase.databaseWriteExecutor.execute(searchHistoryDAO::deleteAll);
    }
}