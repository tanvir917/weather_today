package com.sdlab.jings.weathertoday.data;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

public class Channel implements JSONPopulator {
    private Units units;
    private Item item;
    private String location;
    private Astronomy astronomy;
    private Atmosphere atmosphere;
    private Wind wind;

    public Wind getWind() {
        return wind;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Astronomy getAstronomy() {
        return astronomy;
    }

    public Units getUnits() {
        return units;
    }

    public Item getItem() {
        return item;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public void populate(JSONObject data) {
        units = new Units();
        units.populate(data.optJSONObject("units"));
        item = new Item();
        item.populate(data.optJSONObject("item"));

        astronomy = new Astronomy();
        astronomy.populate(data.optJSONObject("astronomy"));
        atmosphere = new Atmosphere();
        atmosphere.populate(data.optJSONObject("atmosphere"));
        wind = new Wind();
        wind.populate(data.optJSONObject("wind"));

        JSONObject locationData = data.optJSONObject("location");

        String region = locationData.optString("region");
        String country = locationData.optString("country");

        location = String.format("%s, %s", locationData.optString("city"), (region.length() != 0 ? region : country));

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("units", units.toJSON());
            data.put("item", item.toJSON());
            data.put("astronomy", astronomy.toJSON());
            data.put("atmosphere", atmosphere.toJSON());
            data.put("requestLocation", location);
            data.put("wind", wind);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}
