package com.example.user.myapplication;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    OkHttp okHttp = new OkHttp();
    public String resp;
    public Double lat;
    public Double lng;
    public String addresse;
    public String searchAddress;
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

        // Add a marker in Deutschland and move the camera
        LatLng deutschland = new LatLng(50.93645868636, 6.9616086266323);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(deutschland));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void onSearch(View view) throws JSONException {

        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        searchAddress = location_tf.getText().toString();

        String url = "http://192.168.43.158:8888/parking/";
        try {
            Call get = okHttp.doGetRequest(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    resp = response.body().string();
                    //System.out.println(resp);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONArray jsonArray = jsonObject.getJSONArray("parking");

                        for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        System.out.println(obj);
                        String tmpString =  obj.getString("addresse");
                        //System.out.println(searchAddress);
                        //System.out.println(tmpString);
                            if(tmpString.equals(searchAddress)){
                                addresse = tmpString;
                                JSONObject geoData = obj.getJSONObject("geometry");
                                lat = geoData.getDouble("latitude");
                                lng = geoData.getDouble("longitude");
                                //System.out.println("latitude: " + lat + "longitude: " + lng);
                            }

                        }

                        runThread();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runThread(){
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                LatLng latLng = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Addresse: " + addresse));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }));
    }



    private void setUpMapIfNeeded() {
        if (mMap == null) {

            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMyLocationEnabled(true);
        LatLng deutschland = new LatLng(50.93645868636, 6.9616086266323);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(deutschland));

        /*
        Location location = new Location();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
         */
    }

}
