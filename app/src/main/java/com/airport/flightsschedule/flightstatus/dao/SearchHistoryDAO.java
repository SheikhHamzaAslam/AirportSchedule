package com.airport.flightsschedule.flightstatus.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.airport.flightsschedule.flightstatus.database.SearchHistory;

import java.util.List;

@Dao
public interface SearchHistoryDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SearchHistory searchHistory);

    @Delete
    void delete(SearchHistory searchHistory);

    @Query("DELETE FROM search_history")
    void deleteAll();

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    LiveData<List<SearchHistory>> getSearchedAirports();
}