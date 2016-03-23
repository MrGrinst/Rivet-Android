package com.jumpintorivet.rivet.repositories.dtos;

import android.location.Location;

import com.google.gson.annotations.Expose;

public class LocationDTO {
    @Expose
    Double latitude;
    @Expose
    Double longitude;

    public LocationDTO(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
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
