package com.airport.flightsschedule.flightstatus.flights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LiveFlight {

    @SerializedName("aircraft")
    @Expose
    private Aircraft aircraft;
    @SerializedName("airline")
    @Expose
    private Airline airline;
    @SerializedName("arrival")
    @Expose
    private Arrival arrival;
    @SerializedName("departure")
    @Expose
    private Departure departure;
    @SerializedName("flight")
    @Expose
    private Flight flight;
    @SerializedName("geography")
    @Expose
    private Geography geography;
    @SerializedName("speed")
    @Expose
    private Speed speed;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("system")
    @Expose
    private System system;

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public Airline getAirline() {
        return airline;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Arrival getArrival() {
        return arrival;
    }

    public void setArrival(Arrival arrival) {
        this.arrival = arrival;
    }

    public Departure getDeparture() {
        return departure;
    }

    public void setDeparture(Departure departure) {
        this.departure = departure;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Geography getGeography() {
        return geography;
    }

    public void setGeography(Geography geography) {
        this.geography = geography;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public System getSystem() {
        return system;
    }

    public void setSystem(System system) {
        this.system = system;
    }

}
