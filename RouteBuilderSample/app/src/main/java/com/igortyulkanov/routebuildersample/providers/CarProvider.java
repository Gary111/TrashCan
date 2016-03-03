package com.igortyulkanov.routebuildersample.providers;

import com.igortyulkanov.routebuildersample.models.Car;
import com.igortyulkanov.routebuildersample.models.LocationBounds;

import java.util.Collection;

public interface CarProvider {

    Collection<Car> getAvailableCars(LocationBounds locationBounds);

}
