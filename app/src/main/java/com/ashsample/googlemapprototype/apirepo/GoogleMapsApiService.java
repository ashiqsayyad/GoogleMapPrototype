package com.ashsample.googlemapprototype.apirepo;

import android.text.TextUtils;
import android.util.Log;

import com.ashsample.googlemapprototype.datastorage.DataStorage;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleMapsApiService {

    public static final String TAG = "Ashiq";
    public static final String API_KEY = "AIzaSyB7ZJvJqkE0BER5srEJJIitHk5ArgAM00o";
    ApiResponse callback;

    public  static final LatLng LOWER_MANHATTAN = new LatLng(40.722543,
            -73.998585);
    public static final LatLng BROOKLYN_BRIDGE = new LatLng(40.7057, -73.9964);
    public static final LatLng WALL_STREET = new LatLng(40.7064, -74.0094);

    public  static final LatLng MANIKONDA = new LatLng(17.405989,
            78.382096);

    public  static final LatLng GOOGLE = new LatLng(17.458629,
            78.372397);

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + LOWER_MANHATTAN.latitude + "," + LOWER_MANHATTAN.longitude
                + "|" + "|" + BROOKLYN_BRIDGE.latitude + ","
                + BROOKLYN_BRIDGE.longitude + "|" + WALL_STREET.latitude + ","
                + WALL_STREET.longitude;

        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
       // url = "https://maps.googleapis.com/maps/api/directions/json?origin=Disneyland&destination=Universal+Studios+Hollywood&key="+API_KEY;
        url = "https://maps.googleapis.com/maps/api/directions/json?origin=17.405989,78.382096&destination=17.458629,78.372397&key="+API_KEY;
        return url;
    }

   public void getDirections(ApiResponse callback){
        this.callback = callback;
      new Thread(new Runnable() {
          @Override
          public void run() {
              getServerResponse(getMapsApiDirectionsUrl());

          }
      }).start();
   }

    public void getServerResponse(String request) {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(request);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            if(TextUtils.isEmpty(data) || new JSONObject(data).has("error_message")){
            callback.onFailure();}else{
                callback.onSuccess(data);
            }

            br.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception ::::"+ e.toString());
            callback.onFailure();
        } finally {
            try {
                iStream.close();
                urlConnection.disconnect();
            }catch (Exception e){
                Log.d(TAG, "Exception in closing stream::::"+ e.toString());
            }
        }

    }
}
