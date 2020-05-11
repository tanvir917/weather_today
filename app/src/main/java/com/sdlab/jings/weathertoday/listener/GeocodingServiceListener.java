package com.sdlab.jings.weathertoday.listener;

import com.sdlab.jings.weathertoday.data.LocationResults;

public interface GeocodingServiceListener {
    void geocodeSuccess(LocationResults location);

    void geocodeFailure(Exception exception);
}
