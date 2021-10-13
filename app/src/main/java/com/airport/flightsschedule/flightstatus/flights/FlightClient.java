package com.airport.flightsschedule.flightstatus.flights;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FlightClient {
    @GET("flights")
    Call<List<LiveFlight>> getLiveFlightsByFlightNumber(@Query("key")  String key, @Query("flightIata") String flightIata);

    @GET("timetable")
    Call<List<ArrivalsDepartures>> getAirportSchedule(@Query("key")  String key, @Query("iataCode") String iataCode, @Query("type") String type);
}
