package com.londonappbrewery.climapm;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

//import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "b2d439fcbcda74143b2927fa7d46ee16";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 1000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;
    //Checks if user response to locationPermission pertains to the desired request
    final int REQUEST_CODE = 123;

    // TODO: Set LOCATION_PROVIDER here:
    final String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);
        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage =  findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel =  findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);
//        ImageButton goBackButton = findViewById((R.id.backButton));


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              setContentView(R.layout.change_city_layout);
                Intent intent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(intent);
            }
        });
    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima", "onResume() callback received !");
        Log.d("Clima", "Getting weather for current location !");
        Intent intentFromChangeCity = getIntent();
        String city = intentFromChangeCity.getStringExtra("City");

        if(city==null){
            getWeatherForCurrentLocation();
        }else{
            getWeatherForNewCity(city);
        }
    }

    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String city) {
        Log.d("Clima","Getting weather for new city !");
        RequestParams params = new RequestParams();
        params.put("q",city);
        params.put("appid",APP_ID);
        letsDoSomeNetworking(params);
    }


    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location Location) {
                Log.d("Clima", "onLocationChanged() callback received !");
                String longitude =String.valueOf(Location.getLongitude());
                String latitude =String.valueOf(Location.getLatitude());
                Log.d("Clima","Long :"+latitude);
                Log.d("Clima","Long :"+longitude);

                RequestParams params = new RequestParams();
                params.put("lat",latitude);
                params.put("lon",longitude);
                params.put("appid",APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Clima"," 0 means OUT_OF_SERVICE, 1 means TEMPORARILY_UNAVAILABLE," +
                        "2 means  AVAILABLE :"+status);

            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Log.d("Clima","Permission granted !");
                getWeatherForCurrentLocation();
            }else{
                Log.d("Clima","Permission denied !");
            }
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(WEATHER_URL,params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject jsonObject) {
                super.onSuccess(statusCode, headers, jsonObject);
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
                WeatherDataModel weatherdata = WeatherDataModel.fromJson(jsonObject);
                updateUI(weatherdata);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("Clima", statusCode + " " + throwable.getMessage());
            }

        });
    }

    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weatherData){
        Log.d("Clima","Inside UpdateUI :"+weatherData.getCityName());
        mTemperatureLabel.setText(weatherData.getTemperature());
        mCityLabel.setText(weatherData.getCityName());
        mWeatherImage.setImageResource(getResources().getIdentifier(weatherData.getIconName(), "drawable", getPackageName()));
//        Log.d("Clima","Updating UI !"+weather.getCityName()+" "+weather.getTemperature());

    }

    // TODO: Add onPause() here:
    protected void onPause(){
        super.onPause();

        // Freeing up memory once the app is on background
        // because this is a mere a weather app, not a running app
        // so , no need to keep location updates while running on background
        if (mLocationManager !=null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}
