package com.airport.flightsschedule.flightstatus.flights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Airport {

    @SerializedName("GMT")
    @Expose
    private String gmt;
    @SerializedName("codeIataAirport")
    @Expose
    private String codeIataAirport;
    @SerializedName("codeIataCity")
    @Expose
    private String codeIataCity;
    @SerializedName("codeIcaoAirport")
    @Expose
    private String codeIcaoAirport;
    @SerializedName("codeIso2Country")
    @Expose
    private String codeIso2Country;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("latitudeAirport")
    @Expose
    private Double latitudeAirport;
    @SerializedName("longitudeAirport")
    @Expose
    private Double longitudeAirport;
    @SerializedName("nameAirport")
    @Expose
    private String nameAirport;
    @SerializedName("nameCountry")
    @Expose
    private String nameCountry;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("timezone")
    @Expose
    private String timezone;

    public String getGmt() {
        return gmt;
    }

    public void setGmt(String gmt) {
        this.gmt = gmt;
    }

    public String getCodeIataAirport() {
        return codeIataAirport;
    }

    public void setCodeIataAirport(String codeIataAirport) {
        this.codeIataAirport = codeIataAirport;
    }

    public String getCodeIataCity() {
        return codeIataCity;
    }

    public void setCodeIataCity(String codeIataCity) {
        this.codeIataCity = codeIataCity;
    }

    public String getCodeIcaoAirport() {
        return codeIcaoAirport;
    }

    public void setCodeIcaoAirport(String codeIcaoAirport) {
        this.codeIcaoAirport = codeIcaoAirport;
    }

    public String getCodeIso2Country() {
        return codeIso2Country;
    }

    public void setCodeIso2Country(String codeIso2Country) {
        this.codeIso2Country = codeIso2Country;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getLatitudeAirport() {
        return latitudeAirport;
    }

    public void setLatitudeAirport(Double latitudeAirport) {
        this.latitudeAirport = latitudeAirport;
    }

    public Double getLongitudeAirport() {
        return longitudeAirport;
    }

    public void setLongitudeAirport(Double longitudeAirport) {
        this.longitudeAirport = longitudeAirport;
    }

    public String getNameAirport() {
        return nameAirport;
    }

    public void setNameAirport(String nameAirport) {
        this.nameAirport = nameAirport;
    }

    public String getNameCountry() {
        return nameCountry;
    }

    public void setNameCountry(String nameCountry) {
        this.nameCountry = nameCountry;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
}