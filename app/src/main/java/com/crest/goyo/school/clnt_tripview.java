package com.crest.goyo.school;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.ModelClasses.MyKidsModel;
import com.crest.goyo.ModelClasses.model_tripdata;
import com.crest.goyo.R;
import com.crest.goyo.SocketClient.SC_IOApplication;
import com.crest.goyo.Utils.Global;
import com.crest.goyo.googlemap.LatLngInterpolator;
import com.crest.goyo.googlemap.MarkerAnimation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class clnt_tripview extends AppCompatActivity implements OnMapReadyCallback {


    //UI
    TextView tvSpeed, tvLastloc, txtBatch;


    //socket
    private Socket mSocket;
    private boolean isSocConnected = false;

    //googel map related variables
    private GoogleMap mMap;
    private boolean isRecenter = true;
    private List<HashMap<String, Marker>> driverOnMap = new ArrayList<HashMap<String, Marker>>();
    private String tripid = "0";
    private List<model_tripdata> lstMytripdata;
    private float tilt = 0;
    private float zoom = 17f;
    private boolean upward = true;
    private String status = "0";
    //font
    Typeface tf;

    //views
    View kid1,kid2,kid3,kid4,kid5;
    TextView txtk1,txtk2,txtk3,txtk4,txtk5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clnt_tripview);
        addCustomFont();
        setTitle("Trip View");
        initUI();
        getBundle();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //Initialize
    private void initUI() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        kid1 = findViewById(R.id.kid1);
        kid2 = findViewById(R.id.kid2);
        kid3 = findViewById(R.id.kid3);
        kid4 = findViewById(R.id.kid4);
        kid5 = findViewById(R.id.kid5);

        txtk1 = (TextView) kid1.findViewById(R.id.txtName);
        txtk2 = (TextView) kid2.findViewById(R.id.txtName);
        txtk3 = (TextView) kid3.findViewById(R.id.txtName);
        txtk4 = (TextView) kid4.findViewById(R.id.txtName);
        txtk5 = (TextView) kid5.findViewById(R.id.txtName);


//        tvSpeed = (TextView) findViewById(R.id.tvSpeed);
//        tvSpeed.setTypeface(tf);
//        tvLastloc = (TextView) findViewById(R.id.tvLastloc);
//        txtBatch = (TextView) findViewById(R.id.txtBatch);
        //showTimeSpeed("0", "------");


//        ListView listView1 = (ListView) findViewById(R.id.lstkids);
//
//        String[] items = { "Milk", "Butter", "Yogurt", "Toothpaste", "Ice Cream" };
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.layout_icon_text,R.id.txtName, items);
//
//        listView1.setAdapter(adapter);

    }



    private void showTimeSpeed(String speed, String Time) {
        tvSpeed.setText(speed + " Km/h");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {

            tvLastloc.setText(toLocalDateString(Time));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static String toLocalDateString(String utcTimeStamp) {
        Date utcDate = new Date(utcTimeStamp);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        df.setTimeZone(TimeZone.getTimeZone("IN"));
        return df.format(utcDate);
    }



    private void googleMapInit() {
        SupportMapFragment mMap1 = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap1.getMapAsync(this);
        //animator = new Animator();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;//get map object after ready
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setTrafficEnabled(true);

        addMapListner();
        getLastKnownLocation();
        if (!status.equals("2"))
            SocketClient();
    }


    private void addMapListner() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                isRecenter = false;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                //TODO: Any custom actions

                return false;
            }
        });
    }

    //google map helptes


    private void addMarkerEntry(String id, Marker marker) {
        HashMap<String, Marker> entry = new HashMap<String, Marker>();
        entry.put(id, marker);
        driverOnMap.add(entry);
    }

    private boolean markerExitsts(String id) {
        for (int i = 0; i <= driverOnMap.size() - 1; i++) {
            HashMap<String, Marker> l = driverOnMap.get(i);
            if (l.get(id).equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addMarkerToMap(LatLng latLng, String title, String snippet, String Id, String bearing, String speed, String sertm) {
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bus1))
                .zIndex(1.0f)
                .flat(true)
        );
        addMarkerEntry(Id, marker);
        moveMarker(marker, latLng.latitude + "", latLng.longitude + "", bearing, speed, sertm);
    }


    private boolean removeCreawFromMap(String id) {
        int searchListLength = driverOnMap.size();
        for (int i = 0; i < searchListLength; i++) {
            if (driverOnMap.get(i).containsKey(id)) {
                Marker mrk = driverOnMap.get(i).get(id);
                mrk.remove();
                driverOnMap.get(i).remove(id);
                return true;
            }
        }
        return false;
    }

    public void navigateToPoint(LatLng latLng, float tilt, float bearing, float zoom, boolean animate) {
        CameraPosition position =
                new CameraPosition.Builder().target(latLng)
                        .zoom(zoom)
                        .bearing(bearing)
                        .tilt(tilt)
                        .build();

        changeCameraPosition(position, animate);


    }

    private void changeCameraPosition(CameraPosition cameraPosition, boolean animate) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        if (animate) {
            mMap.animateCamera(cameraUpdate);
        } else {
            mMap.moveCamera(cameraUpdate);
        }

    }


    //pub sub socket client
    private void SocketClient() {
        SC_IOApplication app = new SC_IOApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("msgd", onNewMessage);
        mSocket.connect();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "Connected", Toast.LENGTH_LONG).show();
                    if (!isSocConnected) {
                        //if(null!=mUsername)

                        // mSocket.emit("register", tripid);
                        isSocConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.i(TAG, "diconnected");
                    isSocConnected = false;
                    /*Toast.makeText(getApplicationContext(),
                            "Disconnect", Toast.LENGTH_LONG).show();*/
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.e(TAG, "Error connecting");
                    /*Toast.makeText(getApplicationContext(),
                            "Unable to connect server!", Toast.LENGTH_LONG).show();*/
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /*JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;*/
                    try {

                        JSONObject data = ((JSONObject) args[0]);
                        if (data.get("evt").equals("regreq")) {
                            mSocket.emit("register", tripid);
                        } else if (data.get("evt").equals("registered")) {
                            JSONObject objTrp = (JSONObject) data.get("tripid");
                            Toast.makeText(getApplicationContext(),
                                    "registered", Toast.LENGTH_LONG).show();
                        } else if (data.get("evt").equals("data")) {
                            JSONObject objTrp = (JSONObject) data.get("data");
                            trackMarker(objTrp);
                            /*Toast.makeText(getApplicationContext(),
                                    d, Toast.LENGTH_LONG).show();*/
                        }else if (data.get("evt").equals("stop")) {
                           // JSONObject objTrp = (JSONObject) data.get("data");
                            Toast.makeText(clnt_tripview.this, "Trip End", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        //Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            });
        }
    };

    private void trackMarker(JSONObject objTrp) throws JSONException {
        String trpid = objTrp.get("tripid").toString();
        String lat = objTrp.get("lat").toString();
        String lon = objTrp.get("lon").toString();
        String speed = objTrp.get("speed").toString();
        String bearing = objTrp.get("bearng").toString();
        String servertm = objTrp.get("sertm").toString();



        for (int i = 0; i <= driverOnMap.size() - 1; i++) {
            HashMap<String, Marker> l = driverOnMap.get(i);
            if (l.containsKey(trpid)) {
                Marker mrk = l.get(trpid);
                moveMarker(mrk, lat, lon, bearing, speed, servertm);
                return;
            }
        }
        addMarkerToMap(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)), trpid.toString(), "", trpid, bearing, speed, servertm);

    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        tripid = bundle.get("tripid").toString();
        status = bundle.get("status").toString();
        googleMapInit();
        getKidsOnTrip();
    }

    private void updateMap() {
        if (lstMytripdata.size() > 0) {
            model_tripdata mtrp = lstMytripdata.get(0);
            for (int i = 0; i <= driverOnMap.size() - 1; i++) {
                HashMap<String, Marker> l = driverOnMap.get(i);
                if (l.containsKey(mtrp.tripid)) {
                    Marker mrk = l.get(mtrp.tripid);
                    moveMarker(mrk, mtrp.loc[0].toString(), mtrp.loc[1].toString(), mtrp.bearing, mtrp.speed, mtrp.sertm);
                    return;
                }
            }

            addMarkerToMap(new LatLng(Double.parseDouble(mtrp.loc[0]), Double.parseDouble(mtrp.loc[1])), mtrp.tripid.toString(), "", mtrp.tripid, mtrp.bearing, mtrp.speed, mtrp.sertm);

        }
    }

    private void moveMarker(Marker mrk, String lat, String lon, String bearing, String speed, String Servertm) {

        LatLng latlon = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
        mrk.setRotation(Float.parseFloat(bearing));
        LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
        MarkerAnimation.animateMarker(mrk, latlon, latLngInterpolator);
        if (isRecenter) {
            navigateToPoint(latlon, this.tilt, Float.parseFloat(bearing), this.zoom, true);
        }
        //showTimeSpeed(speed, Servertm);

    }


    private void getLastKnownLocation() {
        JsonObject json = new JsonObject();
        json.addProperty("tripid", tripid);
        Ion.with(this)
                .load(Global.urls.getlastknownloc.value)

                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<model_tripdata>>() {
                            }.getType();
                            //JsonElement k = result.get("data");
                            lstMytripdata = (List<model_tripdata>) gson.fromJson(result.get("data"), listType);
                            updateMap();
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }

                    }
                });
    }


    List<MyKidsModel> lstmykidsd;
    private void getKidsOnTrip() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", Global.getUserID(this));
        json.addProperty("tripid", tripid);
        json.addProperty("flag", "kidsontrip");
        Ion.with(this)
                .load(Global.urls.getmykids.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<MyKidsModel>>() {
                            }.getType();
                            lstmykidsd = (List<MyKidsModel>) gson.fromJson(result.get("data"), listType);
                            MyKidsModel _d ;
                            if(lstmykidsd.size() > 0){
                                _d =lstmykidsd.get(0);
                                kid1.setVisibility(View.VISIBLE);
                                txtk1.setText(_d.Name);
                            }
                            if(lstmykidsd.size() > 1){
                                _d =lstmykidsd.get(1);
                                kid2.setVisibility(View.VISIBLE);
                                txtk2.setText(_d.Name);
                            }
                            if(lstmykidsd.size() > 2){
                                _d =lstmykidsd.get(2);
                                kid3.setVisibility(View.VISIBLE);
                                txtk3.setText(_d.Name);
                            }
                            if(lstmykidsd.size() > 3){
                                _d =lstmykidsd.get(3);
                                kid4.setVisibility(View.VISIBLE);
                                txtk4.setText(_d.Name);
                            }
                            if(lstmykidsd.size() > 4){
                                _d =lstmykidsd.get(4);
                                kid5.setVisibility(View.VISIBLE);
                                txtk5.setText(_d.Name);
                            }


                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                    }
                });
    }


    //add font
    private void addCustomFont() {
        tf = Typeface.createFromAsset(getAssets(), "fonts/digital.ttf");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!status.equals("2")) {
            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT, onConnect);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.off("tripd", onNewMessage);
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }
}
