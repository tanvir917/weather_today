package com.sdlab.jings.weathertoday.data;

import org.json.JSONObject;

public class Units implements JSONPopulator {
    private String temperature;
    private String pressure;

    public String getPressure() {
        return pressure;
    }

    public String getTemperature() {
        return temperature;
    }

    @Override
    public void populate(JSONObject data) {
        temperature = data.optString("temperature");
        pressure = data.optString("pressure");

    }

    @Override
    public JSONObject toJSON() {
        return null;
    }
}
