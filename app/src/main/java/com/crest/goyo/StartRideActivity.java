package com.crest.goyo;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.GPSTracker;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.crest.goyo.logger.DataParser;
import com.crest.goyo.logger.Log;
import com.crest.goyo.other.CircleTransform;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.HttpUrl;

/**
 * Created by brittany on 5/1/17.
 */

public class StartRideActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, LocationSource.OnLocationChangedListener {
    private TextView actionbar_title, tv_dr_name, tv_type, tv_pin;
    private ImageView iv_share, img_profile;
    private String mRideid;
    private Intent intent;
    private GPSTracker gps;
    private Marker currentMarker;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private BitmapDrawable bitmapdraw;
    private Bitmap bitmap, resizeMarker;
    private int width = 50;
    private int height = 50;
    private ImageButton bt_sos;
    private String driverId;
    private AlertDialog.Builder builder;
    private Double latitude, longitude, driverLat, driverLong;
    private LatLng pickupLatLng, dropLatLng;
    private Location mLastLocation;
    private double pickup_latitude, pickup_longitude, destination_latitude, destination_longitude;
    private String  TAG="StartRideActivity";
    private BroadcastReceiver mReceiveMessageFromNotification;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_ride);
        if (getIntent().getExtras() != null) {
            mRideid = getIntent().getExtras().getString("i_ride_id","ANNIE");
        }else {
        }

        initUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        builder = new AlertDialog.Builder(StartRideActivity.this, R.style.MyAlertDialogStyle);

        iv_share.setOnClickListener(this);
        bt_sos.setOnClickListener(this);
    }

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        iv_share = (ImageView) findViewById(R.id.iv_share);
        img_profile = (ImageView) findViewById(R.id.img_profile);
        tv_dr_name = (TextView) findViewById(R.id.tv_dr_name);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_pin = (TextView) findViewById(R.id.tv_pin);
        bt_sos = (ImageButton) findViewById(R.id.bt_sos);
        actionbar_title.setText("START RIDING");
    }

    private void getDriverLocationAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", String.valueOf(driverId));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(getApplicationContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        LatLng loc = new LatLng(driverLat, driverLong);
                        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_direction);
                        bitmap = bitmapdraw.getBitmap();
                        resizeMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                        currentMarker = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                        cameraPosition = new CameraPosition.Builder()
                                .target(loc)
                                .zoom(20).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        updateDriverLocation();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });
    }

    private void updateDriverLocation() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                getDriverLocationAPIThread();
                handler.postDelayed(this, 3000); //now is every 2 minutes
            }
        }, 3000);
    }

    private void getDriverLocationAPIThread() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", driverId);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        Log.d("######", "strat ride Thread Activity : ");
        VolleyRequestClassNew.allRequest(getApplicationContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        LatLng loc = new LatLng(driverLat, driverLong);
                        bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_direction);
                        bitmap = bitmapdraw.getBitmap();
                        resizeMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
                        if (currentMarker != null) {
                            currentMarker.remove();
                        }
                        currentMarker = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .flat(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                        cameraPosition = new CameraPosition.Builder()
                                .target(loc)
                                .zoom(20).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getRideAPI(final GoogleMap googleMap) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", mRideid);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(StartRideActivity.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        JSONObject l_data = jsonObject.getJSONObject("l_data");
                        JSONObject driver_data = jsonObject.getJSONObject("driver_data");
                        pickup_latitude = Double.parseDouble(l_data.getString("pickup_latitude"));
                        pickup_longitude = Double.parseDouble(l_data.getString("pickup_longitude"));
                        destination_latitude = Double.parseDouble(l_data.getString("destination_latitude"));
                        destination_longitude = Double.parseDouble(l_data.getString("destination_longitude"));
                        driverId = jsonObject.getString("i_driver_id");
                        tv_pin.setText("Your trip confirmation PIN : " + jsonObject.getString("v_pin"));
                        tv_type.setText(l_data.getString("vehicle_type"));
                        tv_dr_name.setText(driver_data.getString("driver_name"));
                        if (driver_data.getString("driver_image").equals("")) {
                            img_profile.setImageResource(R.drawable.no_user);
                        } else {
                            Glide.with(getApplicationContext()).load(driver_data.getString("driver_image"))
                                    .crossFade()
                                    .thumbnail(0.5f)
                                    .bitmapTransform(new CircleTransform(getApplicationContext()))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(img_profile);
                        }
                        pickupLatLng = new LatLng(pickup_latitude, pickup_longitude);
                        dropLatLng = new LatLng(destination_latitude, destination_longitude);
                        drawRoot(googleMap, pickupLatLng, dropLatLng);
                        getDriverLocationAPI();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }
    private void drawRoot(GoogleMap googleMap, LatLng picup, LatLng drop) {
        LatLng origin = picup;
        LatLng dest = drop;
        String url = getUrl(origin, dest);
        Log.d("onMapClick", url.toString());
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(origin, 19));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(19), 2000, null);
    }
    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }
    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            Log.e("TAG", "DATA = " + data);
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_share:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "location : ");
                intent.setType("text/plain");
                startActivity(intent);
                break;

            case R.id.bt_sos:
                if (Constant.isOnline(getApplicationContext())) {
                    sendRideSosAPI();
                }
                break;
        }
    }
    private void sendRideSosAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_RIDE_SOS).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", mRideid);
        urlBuilder.addQueryParameter("l_latitude", String.valueOf(latitude));
        urlBuilder.addQueryParameter("l_longitude", String.valueOf(longitude));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(StartRideActivity.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gps = new GPSTracker(StartRideActivity.this, StartRideActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        } else {
            gps.showSettingsAlert();
        }
        getRideAPI(mMap);

    }
    @Override
    public void onBackPressed() {
        permissionDialog();
    }
    private void permissionDialog() {
        builder.setTitle("Close Ride?");
        builder.setMessage("Are you sure you want to close the ride?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_cancel);
        builder.show();
    }
    @Override
    public void onLocationChanged(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
    }
}
