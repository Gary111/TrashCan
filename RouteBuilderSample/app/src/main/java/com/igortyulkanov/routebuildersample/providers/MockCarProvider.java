package com.igortyulkanov.routebuildersample.providers;

import com.igortyulkanov.routebuildersample.models.Car;
import com.igortyulkanov.routebuildersample.models.LocationBounds;
import com.igortyulkanov.routebuildersample.utils.RandomUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

public class MockCarProvider implements CarProvider {

    private static final int MIN_CAR_COUNT = 20;
    private static final int MAX_CAR_COUNT = 40;

    @Override
    public Collection<Car> getAvailableCars(LocationBounds locationBounds) {
        return generateCarLocations(locationBounds);
    }

    private Collection<Car> generateCarLocations(LocationBounds locationBounds) {
        return generateCarLocations(MIN_CAR_COUNT, MAX_CAR_COUNT, locationBounds);
    }

    private Collection<Car> generateCarLocations(int minCount, int maxCount, LocationBounds locationBounds) {
        if (maxCount <= minCount) {
            throw new IllegalStateException("Car count should be positive");
        }

        final int totalCount = RandomUtils.newRandom().nextInt(maxCount - minCount) + minCount;

        final Collection<Car> cars = new HashSet<>(totalCount);
        for (int i = 0; i < totalCount; i++) {
            cars.add(new Car(locationBounds.generateNewLocation()));
        }

        return cars;
    }
}
