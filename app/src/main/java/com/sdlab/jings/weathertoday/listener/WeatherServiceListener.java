package com.sdlab.jings.weathertoday.listener;

import com.sdlab.jings.weathertoday.data.Channel;

public interface WeatherServiceListener {

    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}
