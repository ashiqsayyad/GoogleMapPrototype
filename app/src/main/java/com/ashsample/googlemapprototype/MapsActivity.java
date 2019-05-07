package com.ashsample.googlemapprototype;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ashsample.googlemapprototype.apirepo.ApiResponse;
import com.ashsample.googlemapprototype.datastorage.DataStorage;
import com.ashsample.googlemapprototype.util.PathJSONParser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.ashsample.googlemapprototype.apirepo.GoogleMapsApiService.BROOKLYN_BRIDGE;
import static com.ashsample.googlemapprototype.apirepo.GoogleMapsApiService.GOOGLE;
import static com.ashsample.googlemapprototype.apirepo.GoogleMapsApiService.LOWER_MANHATTAN;
import static com.ashsample.googlemapprototype.apirepo.GoogleMapsApiService.MANIKONDA;
import static com.ashsample.googlemapprototype.apirepo.GoogleMapsApiService.WALL_STREET;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ApiResponse {

    public static final String TAG ="AshiqMapsActivity";
    private GoogleMap mMap;
    Handler mainHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

       /* // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/

        MarkerOptions options = new MarkerOptions();
        options.position(MANIKONDA);
        options.title("Manikonda");
        options.snippet("This is source location");
        googleMap.addMarker(options);

        options.position(GOOGLE);
        options.title("Google");
        options.snippet("This is destination location");
        googleMap.addMarker(options);

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MANIKONDA,
                13));

    }
     public void getDirections(View v){
        Toast.makeText(this,"Button is cliked",Toast.LENGTH_SHORT).show();
        new com.ashsample.googlemapprototype.apirepo.GoogleMapsApiService().getDirections(this);




     }

    @Override
    public void onSuccess(final String response) {
        Log.i(TAG," onSuccess Server response ::::"+response);
        DataStorage.saveDirections(this,response);
        new Thread(new Runnable() {
            @Override
            public void run() {
                parseData(response);
            }
        });

    }

    @Override
    public void onFailure() {
        Log.i(TAG," onFailure Server response");
        final String response = DataStorage.getStoredDirections(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG," inside runnable data"+response);
                parseData(response);
            }
        }).start();


    }

    public void parseData(String data){
        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;
        try {
            jObject = new JSONObject(data);
            PathJSONParser parser = new PathJSONParser();
            routes = parser.parse(jObject);
            Log.i(TAG,"Parsing routes === ::"+routes.toString());
            addRouteOnMap(routes);
        } catch (Exception e) {
            Log.i(TAG,"Parsing exception ::"+e.toString());
        }

    }

    public void addRouteOnMap(List<List<HashMap<String, String>>> routes){
        ArrayList<LatLng> points = null;
       final PolylineOptions polyLineOptions = new PolylineOptions();;

        // traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();

            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(10);
            polyLineOptions.color(Color.BLUE);
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                mMap.addPolyline(polyLineOptions);
            }
        });

    }

}
