package com.airport.flightsschedule.flightstatus.flights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Flight {

    @SerializedName("iataNumber")
    @Expose
    private String iataNumber;
    @SerializedName("icaoNumber")
    @Expose
    private String icaoNumber;
    @SerializedName("number")
    @Expose
    private String number;

    public String getIataNumber() {
        return iataNumber;
    }

    public void setIataNumber(String iataNumber) {
        this.iataNumber = iataNumber;
    }

    public String getIcaoNumber() {
        return icaoNumber;
    }

    public void setIcaoNumber(String icaoNumber) {
        this.icaoNumber = icaoNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
