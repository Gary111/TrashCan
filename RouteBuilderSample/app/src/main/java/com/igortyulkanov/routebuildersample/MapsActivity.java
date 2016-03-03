package com.igortyulkanov.routebuildersample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.model.DirectionsRoute;
import com.igortyulkanov.routebuildersample.di.MapView;
import com.igortyulkanov.routebuildersample.di.MapViewPresenter;
import com.igortyulkanov.routebuildersample.models.Car;
import com.igortyulkanov.routebuildersample.models.Location;
import com.igortyulkanov.routebuildersample.models.LocationBounds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MapsActivity extends FragmentActivity implements MapView,
        OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener {

    private final static int PERMISSIONS_REQUEST_CODE_FOR_MY_LOCATION = 1;

    private GoogleMap googleMap;

    private MapViewPresenter mapViewPresenter;

    private Bitmap carMarkerBitmap = null;

    private Polyline lastRoute = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mapViewPresenter = createPresenter();
        mapViewPresenter.initializeLocationProvider(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    private MapViewPresenter createPresenter() {
        final MapViewPresenter mapViewPresenter = new MapViewPresenter(LocationBounds.MOSCOW, getResources().getString(R.string.geo_api_key));
        mapViewPresenter.attachView(this);
        return mapViewPresenter;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE_FOR_MY_LOCATION:
                if (isAllPermissionsGranted(permissions, grantResults)) {
                    enableMyLocation();
                }
                break;
        }
    }

    private boolean isAllPermissionsGranted(@NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.setOnCameraChangeListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);

        enableMyLocationOrRequestPermissions();

        mapViewPresenter.loadCars();
    }

    private void enableMyLocationOrRequestPermissions() {
        final List<String> permissionsToRequest = createPermissionsListToRequestMyLocation();

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[permissionsToRequest.size()]), PERMISSIONS_REQUEST_CODE_FOR_MY_LOCATION);
        } else {
            enableMyLocation();
        }
    }

    private void enableMyLocation() {
        if (googleMap != null) {
            //noinspection ResourceType
            this.googleMap.setMyLocationEnabled(true);
        }
    }

    @NonNull
    private List<String> createPermissionsListToRequestMyLocation() {
        final String[] permissionsToCheck = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };

        final List<String> permissionsToRequest = new ArrayList<>(permissionsToCheck.length);

        for (String permissionToCheck : permissionsToCheck) {
            if (ActivityCompat.checkSelfPermission(this, permissionToCheck) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permissionToCheck);
            }
        }
        return permissionsToRequest;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        mapViewPresenter.moveToActualPosition(false, false);
    }

    @Override
    protected void onDestroy() {
        mapViewPresenter.detachView(false);
        super.onDestroy();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        mapViewPresenter.requestRouteFromMeToLocation(Location.create(marker.getPosition()));
        return true;
    }

    @Override
    public void showCars(Collection<Car> cars) {
        if (googleMap != null) {
            for (Car car : cars) {
                this.googleMap.addMarker(createMarkerOptionsForCar(car));
            }
        }
    }

    @Override
    public void moveToActualPosition(LocationBounds locationBounds, boolean animate) {
        if (googleMap != null) {
            final CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds(
                            new LatLng(locationBounds.getSouthwest().lat, locationBounds.getSouthwest().lng),
                            new LatLng(locationBounds.getNortheast().lat, locationBounds.getNortheast().lng)
                    ), 0);

            if (animate) {
                googleMap.animateCamera(cameraUpdate);
            } else {
                googleMap.moveCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void showRouteCalculatingError() {
        if (lastRoute != null) {
            lastRoute.remove();
            lastRoute = null;
        }
        Toast.makeText(this, R.string.error_cant_find_route, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void drawRoute(DirectionsRoute directionsRoute, boolean isWalking, long timeInMin) {
        if (googleMap != null) {
            if (lastRoute != null) {
                lastRoute.remove();
                lastRoute = null;
            }
            lastRoute = googleMap.addPolyline(createPolylineOptionsForRoute(directionsRoute, isWalking));
            if (isWalking) {
                Toast.makeText(this, getResources().getString(R.string.route_time_walking_hint, timeInMin), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getResources().getString(R.string.route_time_transit_hint, timeInMin), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    private PolylineOptions createPolylineOptionsForRoute(DirectionsRoute directionsRoute, boolean isWalking) {
        final PolylineOptions lastRoute = new PolylineOptions();
        lastRoute.width(10f);
        if (isWalking) {
            lastRoute.color(getResources().getColor(R.color.route_walking));
        } else {
            lastRoute.color(getResources().getColor(R.color.route_transit));
        }
        for (com.google.maps.model.LatLng latLng : directionsRoute.overviewPolyline.decodePath()) {
            lastRoute.add(new LatLng(latLng.lat, latLng.lng));
        }
        return lastRoute;
    }

    private MarkerOptions createMarkerOptionsForCar(Car car) {
        return new MarkerOptions()
                .position(new LatLng(car.location.lat, car.location.lng))
                .icon(createMarkerIconForCar())
                .flat(true)
                .anchor(0.5f, 0.5f);
    }

    @SuppressWarnings({"deprecation", "ConstantConditions"})
    private BitmapDescriptor createMarkerIconForCar() {
        if (carMarkerBitmap == null) {
            final int size = getResources().getDimensionPixelSize(R.dimen.marker_car_available_size);
            carMarkerBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            final Canvas canvas = new Canvas(carMarkerBitmap);
            final Drawable shape = getResources().getDrawable(R.drawable.marker_car_available);
            shape.setBounds(0, 0, carMarkerBitmap.getWidth(), carMarkerBitmap.getHeight());
            shape.draw(canvas);
        }

        return BitmapDescriptorFactory.fromBitmap(carMarkerBitmap);
    }

    @Override
    public void runOnUIThread(Runnable runnable) {
        runOnUiThread(runnable);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapViewPresenter.moveToActualPosition(true, true);
        if (lastRoute != null) {
            lastRoute.remove();
            lastRoute = null;
        }
    }
}
