package com.sdlab.jings.weathertoday.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Astronomy implements JSONPopulator {

    private String sunrise;
    private String sunset;

    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    @Override
    public void populate(JSONObject data) {

        sunrise = data.optString("sunrise");
        sunset = data.optString("sunset");

    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("sunrise", sunrise);
            data.put("sunset", sunset);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
