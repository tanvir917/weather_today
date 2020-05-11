package com.sdlab.jings.weathertoday.data;

import org.json.JSONException;
import org.json.JSONObject;

public class Condition implements JSONPopulator {
    private int code;
    private int temperature;
    private int highTemperature;
    private int lowTemperature;
    private String description;
    private String day;

    private String date;


    public String getDate() {
        return date;
    }

    public int getCode() {
        return code;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getHighTemperature() {
        return highTemperature;
    }

    public int getLowTemperature() {
        return lowTemperature;
    }

    public String getDescription() {
        return description;
    }

    public String getDay() {
        return day;
    }

    @Override
    public void populate(JSONObject data) {
        code = data.optInt("code");
        temperature = data.optInt("temp");
        highTemperature = data.optInt("high");
        lowTemperature = data.optInt("low");
        description = data.optString("text");
        day = data.optString("day");

        date = data.optString("date");
        //sunset = data.optString("sunset");

    }

    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("code", code);
            data.put("temp", temperature);
            data.put("high", highTemperature);
            data.put("low", lowTemperature);
            data.put("text", description);
            data.put("day", day);

            data.put("date", date);
            //data.put("sunset",sunset);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
