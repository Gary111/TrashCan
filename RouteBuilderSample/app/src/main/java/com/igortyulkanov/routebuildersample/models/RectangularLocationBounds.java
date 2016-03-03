package com.igortyulkanov.routebuildersample.models;

import com.google.android.gms.maps.model.LatLng;
import com.igortyulkanov.routebuildersample.exceptions.NotImplementedYetException;

import java.util.Random;

public class RectangularLocationBounds implements LocationBounds {

    private final Location southwest;
    private final Location northeast;

    private RectangularLocationBounds(Location southwest, Location northeast) {
        this.southwest = southwest;
        this.northeast = northeast;
    }

    public static RectangularLocationBounds create(Location southwest, Location northeast) {
        return new RectangularLocationBounds(southwest, northeast);
    }

    public static RectangularLocationBounds create(LatLng southwest, LatLng northeast) {
        return create(Location.create(southwest), Location.create(northeast));
    }

    @Override
    public boolean isInBounds(Location location) {
        throw new NotImplementedYetException(RectangularLocationBounds.class.toString() + "#isInBounds(Location)");
    }

    @Override
    public Location generateNewLocation() {
        final Random random = new Random();

        final double latDifference = northeast.lat - southwest.lat;
        final double newLat = latDifference * random.nextDouble() + southwest.lat;

        final double lngDifference = northeast.lng - southwest.lng;
        final double newLng = lngDifference * random.nextDouble() + southwest.lng;

        return Location.create(newLat, newLng);
    }

    @Override
    public Location getSouthwest() {
        return southwest;
    }

    @Override
    public Location getNortheast() {
        return northeast;
    }

    @Override
    public Location getCenter() {
        return Location.create(
                (northeast.lat - southwest.lat) * 0.5 + southwest.lat,
                (northeast.lng - southwest.lng) * 0.5 + southwest.lng
        );
    }
}
