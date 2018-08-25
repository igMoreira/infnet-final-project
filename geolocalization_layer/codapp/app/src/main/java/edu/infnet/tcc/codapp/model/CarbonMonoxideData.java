package edu.infnet.tcc.codapp.model;

import android.location.Location;
import android.os.Build;
import android.support.annotation.RequiresApi;


import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.time.Instant;


public class CarbonMonoxideData {

    @SerializedName("node")
    private String node;

    @SerializedName("co")
    private Float ppm;

    @SerializedName("long")
    private Double longitude;

    @SerializedName("lat")
    private Double latitude;

    @SerializedName("mat")
    private Long measuredAt;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public CarbonMonoxideData(String node, String gasConcentration, Location currentLocation) {
        this.node = node;
        this.ppm = Float.valueOf(gasConcentration);
        this.longitude = (currentLocation != null) ? currentLocation.getLongitude() : .0;
        this.latitude = (currentLocation != null) ? currentLocation.getLatitude() : .0;
        this.measuredAt = Instant.now().toEpochMilli();
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public Float getPpm() {
        return ppm;
    }

    public void setPpm(Float ppm) {
        this.ppm = ppm;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(Long measuredAt) {
        this.measuredAt = measuredAt;
    }
}
