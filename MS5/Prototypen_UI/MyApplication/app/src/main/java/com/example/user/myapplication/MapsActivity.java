package com.example.user.myapplication;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    OkHttp okHttp = new OkHttp();
    String url = "http://192.168.43.158:8888/parking/";
    LatLng latLng;
    public String resp;
    public Double lat;
    public Double lng;
    public String addresse;
    public String searchAddress;
    List<LatLng> markerSmartpark = new ArrayList<LatLng>();
    List<String> addressSmartpark = new ArrayList<String>();
    List<LatLng> markerSart = new ArrayList<LatLng>();
    List<String> addressStart = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        setupParkButton();

        try {
            Call get = okHttp.doGetRequest(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    resp = response.body().string();
                    System.out.println(resp);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONArray jsonArray = jsonObject.getJSONArray("parking");
                        System.out.println(jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            addresse = obj.getString("addresse");
                            JSONObject geoData = obj.getJSONObject("geometry");
                            lat = geoData.getDouble("latitude");
                            lng = geoData.getDouble("longitude");
                            System.out.println("latitude: " + lat + "longitude: " + lng);
                            markerSart.add(new LatLng(lat, lng));
                            addressStart.add(addresse);
                        }
                        runStartThread();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void runStartThread() {
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < markerSart.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(markerSart.get(i)).title("Addresse: " + addressStart.get(i)));
                }}
        }));
    }

    private void setupParkButton() {

        Button parkButton = (Button) findViewById(R.id.Smartpark);
        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mMap.clear();

                try {
                    Call get = okHttp.doGetRequest(url, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            resp = response.body().string();
                            System.out.println(resp);
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                JSONArray jsonArray = jsonObject.getJSONArray("parking");
                                System.out.println(jsonArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    Double tmpBelegung = obj.getDouble("belegung");
                                    Double tmpKapazitaet = obj.getDouble("kapazitaet");
                                    System.out.println(tmpBelegung);
                                    System.out.println(tmpKapazitaet);
                                    Double auslastung = tmpBelegung / tmpKapazitaet * 100;
                                    System.out.println(auslastung);
                                    Double auslastungsGrenze = 80.0;
                                    System.out.println(i);
                                    if (auslastung <= auslastungsGrenze) {
                                        addresse = obj.getString("addresse");
                                        System.out.println(addresse);
                                        JSONObject geoData = obj.getJSONObject("geometry");
                                        lat = geoData.getDouble("latitude");
                                        lng = geoData.getDouble("longitude");
                                        System.out.println("latitude: " + lat + "longitude: " + lng);
                                        markerSmartpark.add(new LatLng(lat, lng));
                                        addressSmartpark.add(addresse);
                                    }

                                }

                                runParkThread();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

   private void runParkThread() {
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < markerSmartpark.size(); i++) {
                mMap.addMarker(new MarkerOptions().position(markerSmartpark.get(i)).title("Addresse: " + addressSmartpark.get(i)));
            }}
        }));
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
        mMap.clear();
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        searchAddress = location_tf.getText().toString();
        try {
            Call get = okHttp.doGetRequest(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    resp = response.body().string();
                    System.out.println(resp);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONArray jsonArray = jsonObject.getJSONArray("parking");
                        System.out.println(jsonArray);
                        for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        System.out.println(obj);
                        String tmpString =  obj.getString("addresse");
                        System.out.println(searchAddress);
                        System.out.println(tmpString);
                            if(tmpString.equals(searchAddress)){
                                addresse = tmpString;
                                JSONObject geoData = obj.getJSONObject("geometry");
                                lat = geoData.getDouble("latitude");
                                lng = geoData.getDouble("longitude");
                                System.out.println("latitude: " + lat + "longitude: " + lng);
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
                latLng = new LatLng(lat,lng);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Addresse: " + addresse));
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

    public void onClick(View view) {
    }
}
