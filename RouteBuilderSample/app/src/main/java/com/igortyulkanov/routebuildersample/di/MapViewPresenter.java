package com.igortyulkanov.routebuildersample.di;

import android.content.Context;
import android.location.LocationManager;
import android.os.Handler;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.igortyulkanov.routebuildersample.models.Car;
import com.igortyulkanov.routebuildersample.models.Location;
import com.igortyulkanov.routebuildersample.models.LocationBounds;
import com.igortyulkanov.routebuildersample.mvp.MvpBasePresenter;
import com.igortyulkanov.routebuildersample.providers.CarProvider;
import com.igortyulkanov.routebuildersample.providers.LocationProvider;
import com.igortyulkanov.routebuildersample.providers.MockCarProvider;
import com.igortyulkanov.routebuildersample.providers.MockLocationProvider;
import com.igortyulkanov.routebuildersample.utils.RandomUtils;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class MapViewPresenter extends MvpBasePresenter<MapView>{

    private final Handler handler;

    private final CarProvider carProvider;
    private final LocationProvider locationProvider;

    private boolean isFirstTimeCameraChanged;

    private final LocationBounds locationBounds;

    private final GeoApiContext geoApiContext;

    public MapViewPresenter(LocationBounds locationBounds, String geoApiKey) {
        this.handler = new Handler();

        this.carProvider = new MockCarProvider();
        this.locationProvider = new MockLocationProvider(LocationManager.GPS_PROVIDER, locationBounds.getCenter());

        this.isFirstTimeCameraChanged = false;

        this.locationBounds = locationBounds;

        this.geoApiContext = new GeoApiContext().setApiKey(geoApiKey);
    }

    public void moveToActualPosition(boolean force, boolean animate) {
        final MapView mapView = getView();
        if (isViewAttached(mapView)) {
            if (!isFirstTimeCameraChanged) {
                isFirstTimeCameraChanged = true;
                mapView.moveToActualPosition(locationBounds, animate);
            } else if (force) {
                mapView.moveToActualPosition(locationBounds, animate);
            }
        }
    }

    public void loadCars() {
        final MapView mapView = getView();
        if (isViewAttached(mapView)) {
            loadCarsWithDefaultDelay(locationBounds, mapView);
        }
    }

    private void loadCarsWithDefaultDelay(final LocationBounds locationBounds, final MapView mapView) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Collection<Car> cars = carProvider.getAvailableCars(locationBounds);
                mapView.showCars(cars);
            }
        }, TimeUnit.SECONDS.toMillis(RandomUtils.newRandomAndNextInt(2, 5)));
    }

    public void initializeLocationProvider(Context context) {
        locationProvider.initialize(context);
    }

    public void requestRouteFromMeToLocation(Location location) {
        requestWalkingRouteFromMeToLocation(location);
    }

    private void requestWalkingRouteFromMeToLocation(final Location location) {
        final Location myLocation = locationProvider.getLastKnownLocation();

        DirectionsApi.newRequest(geoApiContext)
                .origin(new LatLng(myLocation.lat, myLocation.lng))
                .destination(new LatLng(location.lat, location.lng))
                .mode(TravelMode.WALKING)
                .units(Unit.METRIC)
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(final DirectionsResult result) {
                        if (result != null && result.routes != null && result.routes.length > 0) {
                            final long durationInSec = calculateDurationInSec(result);
                            if (durationInSec > TimeUnit.MINUTES.toSeconds(20)) {
                                requestTransitRouteFromMeToLocation(location, result.routes[0], durationInSec);
                            } else {
                                drawRoute(result.routes[0], true, durationInSec);
                            }
                        } else {
                            requestTransitRouteFromMeToLocation(location, null, 0);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        requestTransitRouteFromMeToLocation(location, null, 0);
                    }
                });
    }

    private void requestTransitRouteFromMeToLocation(Location location, final DirectionsRoute directionsRoute, final long durationInSec) {
        final Location myLocation = locationProvider.getLastKnownLocation();

        DirectionsApi.newRequest(geoApiContext)
                .origin(new LatLng(myLocation.lat, myLocation.lng))
                .destination(new LatLng(location.lat, location.lng))
                .mode(TravelMode.TRANSIT)
                .units(Unit.METRIC)
                .setCallback(new PendingResult.Callback<DirectionsResult>() {
                    @Override
                    public void onResult(final DirectionsResult result) {
                        if (result != null && result.routes != null && result.routes.length > 0) {
                            drawRoute(result.routes[0], false, calculateDurationInSec(result));
                        } else {
                            showWalkingOrError(directionsRoute, durationInSec);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        showWalkingOrError(directionsRoute, durationInSec);
                    }

                    private void showWalkingOrError(DirectionsRoute directionsRoute, final long durationInSec) {
                        if (directionsRoute != null) {
                            drawRoute(directionsRoute, true, durationInSec);
                        } else {
                            showPathCalculatingError();
                        }
                    }
                });
    }

    private void drawRoute(final DirectionsRoute directionsRoute, final boolean isWalking, final long timeInSec) {
        final MapView mapView = getView();
        if (isViewAttached(mapView)) {
            mapView.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mapView.drawRoute(directionsRoute, isWalking, TimeUnit.SECONDS.toMinutes(timeInSec));
                }
            });
        }
    }

    private void showPathCalculatingError() {
        final MapView mapView = getView();
        if (isViewAttached(mapView)) {
            mapView.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    mapView.showRouteCalculatingError();
                }
            });
        }
    }

    private static long calculateDurationInSec(DirectionsResult directionsResult) {
        long duration = 0;
        for (DirectionsLeg leg : directionsResult.routes[0].legs) {
            duration += leg.duration.inSeconds;
        }
        return duration;
    }

    @Override
    public void detachView(boolean retainInstance) {
        locationProvider.release();
        super.detachView(retainInstance);
    }
}
