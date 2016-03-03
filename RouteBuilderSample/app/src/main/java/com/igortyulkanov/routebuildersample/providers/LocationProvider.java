package com.igortyulkanov.routebuildersample.providers;

import android.content.Context;

import com.igortyulkanov.routebuildersample.models.Location;

public interface LocationProvider {

    void initialize(Context context);

    Location getLastKnownLocation();

    void release();

}
