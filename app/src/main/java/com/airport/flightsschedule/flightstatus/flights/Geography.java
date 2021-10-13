
package com.airport.flightsschedule.flightstatus.flights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geography {

    @SerializedName("altitude")
    @Expose
    private Double altitude;
    @SerializedName("direction")
    @Expose
    private Double direction;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Double getDirection() {
        return direction;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

}
