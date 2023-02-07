package com.airport.flightsschedule.flightstatus.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_history")
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    @ColumnInfo(name = "airportName")
    public String airportName;
    @NonNull
    @ColumnInfo(name = "cityName")
    public String cityName;
    @NonNull
    @ColumnInfo(name = "airportIATA")
    public String airportIATA;
    @NonNull
    @ColumnInfo(name = "airportICAO")
    public String airportICAO;
    @NonNull
    @ColumnInfo(name = "latitudeAirport")
    public Double latitudeAirport;
    @NonNull
    @ColumnInfo(name = "longitudeAirport")
    public Double longitudeAirport;
    @NonNull
    @ColumnInfo(name = "distance")
    public Double distance;

    public SearchHistory(@NonNull String airportName,
                         @NonNull String cityName,
                         @NonNull String airportIATA,
                         @NonNull String airportICAO,
                         @NonNull Double latitudeAirport,
                         @NonNull Double longitudeAirport,
                         @NonNull Double distance) {
        this.airportName = airportName;
        this.cityName = cityName;
        this.airportIATA = airportIATA;
        this.airportICAO = airportICAO;
        this.latitudeAirport = latitudeAirport;
        this.longitudeAirport = longitudeAirport;
        this.distance = distance;
    }

    @NonNull
    public String getAirportName() {
        return airportName;
    }

    @NonNull
    public String getCityName() {
        return cityName;
    }

    @NonNull
    public String getAirportIATA() {
        return airportIATA;
    }

    @NonNull
    public String getAirportICAO() {
        return airportICAO;
    }

    @NonNull
    public Double getLatitudeAirport() {
        return latitudeAirport;
    }

    @NonNull
    public Double getLongitudeAirport() {
        return longitudeAirport;
    }

    @NonNull
    public Double getDistance() {
        return distance;
    }
}
