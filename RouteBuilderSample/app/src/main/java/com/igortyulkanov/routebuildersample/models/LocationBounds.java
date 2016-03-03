package com.igortyulkanov.routebuildersample.models;

public interface LocationBounds {

    LocationBounds MOSCOW = RectangularLocationBounds.create(
            Location.create(55.698365, 37.535992),
            Location.create(55.797687, 37.715893)
    );

    boolean isInBounds(Location location);

    Location generateNewLocation();

    Location getSouthwest();

    Location getNortheast();

    Location getCenter();

}
