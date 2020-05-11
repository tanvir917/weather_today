package com.sdlab.jings.weathertoday;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sdlab.jings.weathertoday.data.Astronomy;
import com.sdlab.jings.weathertoday.data.Atmosphere;
import com.sdlab.jings.weathertoday.data.Channel;
import com.sdlab.jings.weathertoday.data.Condition;
import com.sdlab.jings.weathertoday.data.LocationResults;
import com.sdlab.jings.weathertoday.data.Units;
import com.sdlab.jings.weathertoday.data.Wind;
import com.sdlab.jings.weathertoday.fragments.WeatherConditionFragments;
import com.sdlab.jings.weathertoday.listener.GeocodingServiceListener;
import com.sdlab.jings.weathertoday.listener.WeatherServiceListener;
import com.sdlab.jings.weathertoday.services.GoogleMapsGeoCodingService;
import com.sdlab.jings.weathertoday.services.WeatherService;

import offline_1.sdlab.com.weathertoday.R;

public class WeatherActivity extends AppCompatActivity implements WeatherServiceListener, GeocodingServiceListener, LocationListener {
    public static int GET_WEATHER_FROM_CURRENT_LOCATION = 0x00001;
    private SharedPreferences preferences = null;
    private GoogleMapsGeoCodingService geocodingService;

    //private AdView mAdView;
    //private InterstitialAd minterstitial;
    private ImageView weatherIconImageView;
    private TextView temperatureTextView;
    private TextView conditionTextView;
    private TextView locationTextView;
    private TextView dateTextView;

    private TextView sunriseTextView;
    private TextView sunsetTextView;
    private TextView humidityTextView;
    private TextView pressureTextView;
    private TextView windChillTextView;
    private TextView winddirectionTextView;
    private TextView windSpeed;

    private LinearLayout tw;
    private WeatherService weatherService;
    private ScrollView scrollView;
    private LinearLayout tw2;

    private ProgressDialog loadingDialog;
    private boolean weatherServicesHasFailed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);


        //ad view started
        /*MobileAds.initialize(getApplicationContext(),
                "ca-app-pub-5036155976366649~9648186083");

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        minterstitial = new InterstitialAd(this);
        minterstitial.setAdUnitId("ca-app-pub-5036155976366649/2703503785");
        minterstitial.loadAd(new AdRequest.Builder().build());


        //add view ended*/

        weatherIconImageView = (ImageView) findViewById(R.id.weatherIconImageView);
        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        conditionTextView = (TextView) findViewById(R.id.conditionTextView);
        locationTextView = (TextView) findViewById(R.id.locationTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        sunriseTextView = (TextView) findViewById(R.id.sunriseTextView);
        sunsetTextView = (TextView) findViewById(R.id.sunsetTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);
        pressureTextView = (TextView) findViewById(R.id.pressureTextView);
        windChillTextView = (TextView) findViewById(R.id.windTextView);
        winddirectionTextView = (TextView) findViewById(R.id.winddirectionTextView);
        windSpeed = (TextView) findViewById(R.id.windSpeedTextView);

        tw = (LinearLayout) findViewById(R.id.todayTemp);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        tw2 = (LinearLayout) findViewById(R.id.todaysDetailsLayout);

        tw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.scrollTo((int) scrollView.getScrollX(), (int) scrollView.getScrollY() + 2400);
                //Toast.makeText(getApplicationContext(), "Clicked",Toast.LENGTH_LONG).show();
            }
        });
        tw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.scrollTo((int) scrollView.getScrollX(), (int) scrollView.getScrollY() - 2400);
            }
        });
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        weatherService = new WeatherService(this);
        weatherService.setTemperatureUnit(preferences.getString(getString(R.string.pref_temperature_unit), null));

        //loadingDialog = new ProgressDialog(this);
        //loadingDialog.setMessage("Loading....");
        //loadingDialog.show();

        //Refresh Weather City Name Manual input
        weatherService.refreshWeather("Nome, United States");

        geocodingService = new GoogleMapsGeoCodingService(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("LoadingGeo...");
        //loadingDialog.setCancelable(false);
        loadingDialog.show();

        String location = null;

        if (preferences.getBoolean("geolocation_enabled", true)) {
            String locationcache = preferences.getString("cached_location", null);

            if (locationcache == null) {
                getWeatherFromCurrentLocation();
            } else {
                location = locationcache;
            }
        } else {
            //location = preferences.getString("manual_location",null);
            location = preferences.getString(getString(R.string.pref_manual_location), null);
            //Toast.makeText(getApplicationContext(), "Geo-Location is not enabled",
            //Toast.LENGTH_LONG).show();

        }

        if (location != null) {
            weatherService.refreshWeather(location);
        }
    }

    private void getWeatherFromCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
            }, GET_WEATHER_FROM_CURRENT_LOCATION);

            return;
        }

        // system's LocationManager
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Criteria locationCriteria = new Criteria();

        if (isNetworkEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_COARSE);
        } else if (isGPSEnabled) {
            locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        }

        locationManager.requestSingleUpdate(locationCriteria, this, null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == WeatherActivity.GET_WEATHER_FROM_CURRENT_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getWeatherFromCurrentLocation();
            } else {
                loadingDialog.hide();

                AlertDialog messageDialog = new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.location_permission_needed))
                        .setPositiveButton(getString(R.string.disable_geolocation), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startSettingsActivity();
                            }
                        })
                        .create();

                messageDialog.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnulocation:
                loadingDialog.show();
                getWeatherFromCurrentLocation();
                Toast.makeText(getApplicationContext(), "Geo-Location is Clicked!",
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.settings:
                //Toast.makeText(getApplicationContext(), "Setting is Clicked!",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(WeatherActivity.this, SettingActivity.class);
                startActivity(intent);

                return true;
            case R.id.mnuShare:
                Toast.makeText(getApplicationContext(), "Share is Clicked", Toast.LENGTH_LONG).show();
                return true;
            case R.id.aboutUs:
                Intent intent1 = new Intent(this, AboutUs.class);
                startActivity(intent1);
                //Toast.makeText(getApplicationContext(),"About Us is Clicked",Toast.LENGTH_LONG).show();
            case R.id.refresh:
                Toast.makeText(getApplicationContext(), "Refresh is Clicked", Toast.LENGTH_LONG).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void serviceSuccess(Channel channel) {
        loadingDialog.hide();

        Condition condition = channel.getItem().getCondition();
        Astronomy astronomy = channel.getAstronomy();
        Atmosphere atmosphere = channel.getAtmosphere();
        Wind wind = channel.getWind();
        Units units = channel.getUnits();
        Condition[] forecast = channel.getItem().getForecast();

        int weatherIconImageResource = getResources().getIdentifier("icon_" + condition.getCode(), "drawable", getPackageName());

        weatherIconImageView.setImageResource(weatherIconImageResource);
        temperatureTextView.setText(getString(R.string.temperature_output, condition.getTemperature(), units.getTemperature()));
        conditionTextView.setText(condition.getDescription());
        locationTextView.setText(channel.getLocation());
        dateTextView.setText(condition.getDate());
        sunriseTextView.setText("Sunset: " + astronomy.getSunrise());
        sunsetTextView.setText("Sunset: " + " " + astronomy.getSunset());
        humidityTextView.setText("Humidity: " + atmosphere.getHumidity() + " %");
        pressureTextView.setText("Pressure: " + atmosphere.getPressure() + " in");
        windChillTextView.setText("Wind Chill: " + wind.getChill());
        winddirectionTextView.setText("Wind Direction: " + wind.getDirection());
        windSpeed.setText("Wind Speed: " + wind.getSpeed() + " mp/h");

        for (int day = 0; day < forecast.length; day++) {
            if (day >= 11) {
                break;
            }

            Condition currentCondition = forecast[day];

            int viewId = getResources().getIdentifier("forecast_" + day, "id", getPackageName());
            WeatherConditionFragments fragment = (WeatherConditionFragments) getSupportFragmentManager().findFragmentById(viewId);

            if (fragment != null) {
                fragment.loadForecast(currentCondition, channel.getUnits());
            }
        }


    }

    @Override
    public void serviceFailure(Exception exception) {
        // display error if this is the second failure
        if (weatherServicesHasFailed) {
            loadingDialog.hide();
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            // error doing reverse geocoding, load weather data from cache
            weatherServicesHasFailed = true;
            // OPTIONAL: let the user know an error has occurred then fallback to the cached data
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void geocodeSuccess(LocationResults location) {
        // completed geocoding successfully
        weatherService.refreshWeather(location.getAddress());

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.pref_cached_location), location.getAddress());
        editor.apply();

    }

    @Override
    public void geocodeFailure(Exception exception) {

    }

    @Override
    public void onLocationChanged(Location location) {
        geocodingService.refreshLocation(location);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
