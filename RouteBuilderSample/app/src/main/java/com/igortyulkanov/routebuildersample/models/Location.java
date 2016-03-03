package com.igortyulkanov.routebuildersample.models;

import com.google.android.gms.maps.model.LatLng;

public class Location {

    public final double lat;
    public final double lng;

    private Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public static Location create(double lat, double lng) {
        return new Location(lat, lng);
    }

    public static Location create(LatLng latLng) {
        return new Location(latLng.latitude, latLng.longitude);
    }
}
