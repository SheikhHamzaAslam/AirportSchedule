package com.airport.flightsschedule.flightstatus.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.airport.flightsschedule.flightstatus.dao.SearchHistoryDAO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SearchHistory.class}, version = 1, exportSchema = false)
public abstract class SearchHistoryDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static volatile SearchHistoryDatabase INSTANCE;

    public static SearchHistoryDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SearchHistoryDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SearchHistoryDatabase.class, "search_history")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract SearchHistoryDAO searchHistoryDAO();
}