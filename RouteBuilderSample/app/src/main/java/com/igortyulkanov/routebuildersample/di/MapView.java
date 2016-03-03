package com.igortyulkanov.routebuildersample.di;

import com.google.maps.model.DirectionsRoute;
import com.igortyulkanov.routebuildersample.models.Car;
import com.igortyulkanov.routebuildersample.models.LocationBounds;
import com.igortyulkanov.routebuildersample.mvp.MvpView;

import java.util.Collection;

public interface MapView extends MvpView {

    void showCars(Collection<Car> cars);

    void moveToActualPosition(LocationBounds locationBounds, boolean animate);

    void showRouteCalculatingError();

    void drawRoute(DirectionsRoute directionsRoute, boolean isWalking, long timeInMin);
}
