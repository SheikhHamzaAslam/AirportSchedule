
package com.airport.flightsschedule.flightstatus.flights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Speed {

    @SerializedName("horizontal")
    @Expose
    private Double horizontal;
    @SerializedName("isGround")
    @Expose
    private Double isGround;
    @SerializedName("vspeed")
    @Expose
    private Double vspeed;

    public Double getHorizontal() {
        return horizontal;
    }

    public void setHorizontal(Double horizontal) {
        this.horizontal = horizontal;
    }

    public Double getIsGround() {
        return isGround;
    }

    public void setIsGround(Double isGround) {
        this.isGround = isGround;
    }

    public Double getVspeed() {
        return vspeed;
    }

    public void setVspeed(Double vspeed) {
        this.vspeed = vspeed;
    }

}
