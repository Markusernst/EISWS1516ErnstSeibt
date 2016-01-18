package com.example.user.myapplication;

import android.content.Intent;
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
    String url;
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

        //Getting Ip from Start Activity
        String ip = getIntent().getExtras().getString("IP", "defaultKey");
        //192.168.43.158
        url = "http://"+ip+":8888/parking/";
        setupParkButton();

        try {
            // HTTP Get on Parking Ressource
            Call get = okHttp.doGetRequest(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    resp = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONArray jsonArray = jsonObject.getJSONArray("parking");
                        //iterate through all Parkings
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
                        //run Set Markers in Main thread
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

    //Start setting Markers in main thread
    private void runStartThread() {
        runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < markerSart.size(); i++) {
                    mMap.addMarker(new MarkerOptions().position(markerSart.get(i)).title("Addresse: " + addressStart.get(i)));
                }}
        }));
    }

    //Smartpark Button
    private void setupParkButton() {

        Button parkButton = (Button) findViewById(R.id.Smartpark);
        parkButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //clear the map from buttons
                mMap.clear();

                try {
                    Call get = okHttp.doGetRequest(url, new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            resp = response.body().string();
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                JSONArray jsonArray = jsonObject.getJSONArray("parking");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    Double tmpBelegung = obj.getDouble("belegung");
                                    Double tmpKapazitaet = obj.getDouble("kapazitaet");

                                    //calculate open space on parkplaces
                                    Double auslastung = tmpBelegung / tmpKapazitaet * 100;
                                    Double auslastungsGrenze = 80.0;
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
                                //run thread to set markers in main
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

    //set markers in main thread
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

    //set markers with search for address
    public void onSearch(View view) throws JSONException {
        mMap.clear();

        //getting text from input
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        searchAddress = location_tf.getText().toString();
        try {

            //HTTP get for parkings
            Call get = okHttp.doGetRequest(url, new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    resp = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        JSONArray jsonArray = jsonObject.getJSONArray("parking");
                        //iterate through all parking places
                        for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String tmpString =  obj.getString("addresse");

                            //compare search-input with addresses
                            if(tmpString.equals(searchAddress)){
                                addresse = tmpString;
                                JSONObject geoData = obj.getJSONObject("geometry");
                                lat = geoData.getDouble("latitude");
                                lng = geoData.getDouble("longitude");
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
