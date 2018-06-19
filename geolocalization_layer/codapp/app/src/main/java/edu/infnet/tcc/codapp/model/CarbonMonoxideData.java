package edu.infnet.tcc.codapp.model;

import android.location.Location;

public class CarbonMonoxideData {
    private Float ppm;
    private Location location;

    public CarbonMonoxideData(String gasConcentration, Location currentLocation) {
        this.ppm = Float.valueOf(gasConcentration);
        this.location = currentLocation;
    }


    @Override
    public String toString() {
        return "CarbonMonoxideData{" +
                "ppm=" + ppm +
                ", altitude=" + (location != null ? location.getAltitude() : "0") +
                ", longitude=" + (location != null ? location.getLongitude() : "0") +
                '}';
    }
}
