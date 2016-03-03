package com.igortyulkanov.routebuildersample.providers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.igortyulkanov.routebuildersample.models.Location;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class MockLocationProvider implements LocationProvider {

    private final String providerName;
    private Location lastKnownLocation;

    private Reference<Context> contextReference;

    public MockLocationProvider(String providerName, Location location) {
        this.providerName = providerName;
        this.lastKnownLocation = location;
    }

    @NonNull
    private android.location.Location createLocation(double lat, double lng) {
        final android.location.Location mockLocation = new android.location.Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lng);
        mockLocation.setAltitude(0);
        mockLocation.setTime(System.currentTimeMillis());
        mockLocation.setAccuracy(5.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        }
        return mockLocation;
    }

    @Override
    public void release() {
        if (contextReference != null) {
            final Context context = contextReference.get();
            if (context != null) {
                final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                lm.setTestProviderEnabled(providerName, false);
                lm.removeTestProvider(providerName);
            }

            contextReference.clear();
        }
    }

    @Override
    public void initialize(Context context) {
        if (context != null) {
            this.contextReference = new WeakReference<>(context);

            final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            locationManager.addTestProvider(providerName, false, false, false, false, false, true, true, 0, 5);
            locationManager.setTestProviderEnabled(providerName, true);
            locationManager.setTestProviderLocation(providerName, createLocation(lastKnownLocation.lat, lastKnownLocation.lng));
        }
    }

    @Override
    public Location getLastKnownLocation() {
        if (contextReference != null) {
            final Context context = contextReference.get();
            if (context != null) {
                final LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    android.location.Location location = lm.getLastKnownLocation(providerName);
                    lastKnownLocation = Location.create(location.getLatitude(), location.getLongitude());
                }
            }
        }
        return lastKnownLocation;
    }
}