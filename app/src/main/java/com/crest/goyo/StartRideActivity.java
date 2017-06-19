package com.crest.goyo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crest.goyo.FCM.MyFirebaseMessagingService;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.FileUtils;
import com.crest.goyo.Utils.GPSTracker;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.crest.goyo.logger.DataParser;
import com.crest.goyo.logger.Log;
import com.crest.goyo.other.CircleTransform;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import java.util.Locale;

import okhttp3.HttpUrl;

/**
 * Created by brittany on 5/1/17.
 */

public class StartRideActivity extends AppCompatActivity implements View.OnClickListener, LocationSource.OnLocationChangedListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private TextView actionbar_title, tv_dr_name, tv_type, tv_pin;
    private ImageView iv_share, img_profile;
    private String mRideid, comeFrom;
    private Intent intent;
    private GPSTracker gps;
    private Marker currentMarker;
    private GoogleMap mMap;
    private CameraPosition cameraPosition;
    private BitmapDrawable bitmapdraw;
    private Bitmap bitmap, resizeMarker;
    private int width = 50;
    private int height = 50;
    private ImageButton bt_sos, bt_track;
    private String driverId;
    private AlertDialog.Builder builder;
    private Double latitude, longitude, driverLat, driverLong;
    private LatLng pickupLatLng, dropLatLng;
    private Location mLastLocation;
    private double pickup_latitude, pickup_longitude, destination_latitude, destination_longitude;
    private String TAG = "StartRideActivity";
    private BroadcastReceiver mReceiveMessageFromNotification;
    private LinearLayout mShare;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private Geocoder geocoder;
    private String cityCurrent;
    private Location location;
    private LocationManager locManager;
    private double currentLat, currentLong;
    private LatLng currentLatLong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_start_ride);
        if (getIntent().getExtras() != null) {
            mRideid = getIntent().getExtras().getString("i_ride_id", "");
            comeFrom = getIntent().getExtras().getString("comeFrom", "");
        } else {
        }

        initUI();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        builder = new AlertDialog.Builder(StartRideActivity.this, R.style.MyAlertDialogStyle);
        getMessageFromNotification();
        iv_share.setOnClickListener(this);
        bt_sos.setOnClickListener(this);

        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareLocation();
            }
        });

        bt_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTrackableLink();
            }
        });

    }

    private void sendTrackableLink() {
        final Dialog dialog = new Dialog(StartRideActivity.this);
        dialog.setContentView(R.layout.dialog_send_trackable_link);
        dialog.setCancelable(false);
        // set the custom dialog components - text, image and button
        final EditText mPhoneNo = (EditText) dialog.findViewById(R.id.edttxt_phone_no);
        Button bt_accept = (Button) dialog.findViewById(R.id.btn_apply);
        Button bt_denied = (Button) dialog.findViewById(R.id.btn_cancel);
        final ProgressBar progressBar11 = (ProgressBar) dialog.findViewById(R.id.progress_bar);
        bt_denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        // if button is clicked, close the custom dialog
        bt_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = mPhoneNo.getText().toString().trim();
                if (phone.isEmpty()) {
                    mPhoneNo.setError("Please enter phone number");
                } else if (phone.length() != 10) {
                    mPhoneNo.setError("Please enter correct phone number");
                } else {
                    send_sharable_linl_api(mRideid, phone, progressBar11, dialog);
                }
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

    private void send_sharable_linl_api(final String mRideId, final String mPhoneNo, final ProgressBar progressBar, final Dialog dialog) {
        FileUtils.showProgressBar(StartRideActivity.this, progressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.START_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", mRideId);
        urlBuilder.addQueryParameter("v_phone", mPhoneNo);

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
                        FileUtils.hideProgressBar(StartRideActivity.this, progressBar);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        FileUtils.hideProgressBar(StartRideActivity.this, progressBar);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void ShareLocation() {

        String uri = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String ShareSub = "Here is my location";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, ShareSub);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));

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
        mShare = (LinearLayout) findViewById(R.id.ll_share);
        bt_track = (ImageButton) findViewById(R.id.bt_track);
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
//                        currentMarker = mMap.addMarker(new MarkerOptions()
//                                .position(loc)
//                                .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getApplicationContext(), R.drawable.marker_direction))));
                        currentMarker = mMap.addMarker(new MarkerOptions()
                                .position(loc)
                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
                        cameraPosition = new CameraPosition.Builder()
                                .target(loc)
                                .bearing(20)
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

    private Handler h1 = new Handler();

    private void updateDriverLocation() {

        h1.postDelayed(new Runnable() {
            public void run() {
                getDriverLocationAPIThread();
                h1.postDelayed(this, 3000); //now is every 2 minutes
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
                        currentMarker.setPosition(loc);
//                        cameraPosition = new CameraPosition.Builder()
//                                .target(loc)             // Sets the center of the map to current location
//                                .zoom(20)                   // Sets the zoom
//                                .bearing(20) // Sets the orientation of the camera to east
//                                .tilt(0)                   // Sets the tilt of the camera to 0 degrees
//                                .build();
//                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        Location targetLocation = new Location("");//provider name is unnecessary
                        targetLocation.setLatitude(driverLat);//your coords of course
                        targetLocation.setLongitude(driverLong);
                        Log.e("TAG", "targetLocation " + targetLocation);
//                        float distanceInMeters =  targetLocation.distanceTo(location);
                        if (targetLocation.hasBearing()) {
                            Log.e("TAG", "hasBearing = true");
                            cameraPosition = new CameraPosition.Builder()
                                    .target(loc)             // Sets the center of the map to current location
                                    .zoom(15)                   // Sets the zoom
                                    .bearing(targetLocation.getBearing()) // Sets the orientation of the camera to east
                                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        } else {
                            Log.e("TAG", "hasBearing = false");
                            cameraPosition = new CameraPosition.Builder()
                                    .target(loc)             // Sets the center of the map to current location
                                    .zoom(15)                   // Sets the zoom
                                    .bearing(targetLocation.getBearing()) // Sets the orientation of the camera to east
                                    .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
//                        currentMarker = mMap.addMarker(new MarkerOptions()
//                                .position(loc)
//                                .flat(true)
//                                .icon(BitmapDescriptorFactory.fromBitmap(resizeMarker)));
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
//                        cameraPosition = new CameraPosition.Builder()
//                                .target(loc)
//                                .zoom(20).build();
//                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

                        /*hector*/
                        JSONObject vehicle_type_data = jsonObject.getJSONObject("vehicle_type_data");
                        Preferences.setValue(getApplicationContext(), Preferences.VEHICLES_IMG, vehicle_type_data.getString("plotting_icon"));

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
                String uri = "geo:" + latitude + ","
                        + longitude + "?q=" + latitude
                        + "," + longitude;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, Uri.parse(uri));
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
        urlBuilder.addQueryParameter("city", cityCurrent);
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
                        JSONObject data = response.getJSONObject("data");
                        String phNo = data.getString("phone_sos");
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                                "tel", phNo, null));
                        startActivity(phoneIntent);
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
//            Location currentLocation = new Location("");//provider name is unnecessary
//            currentLocation.setLatitude(latitude);//your coords of course
//            currentLocation.setLongitude(longitude);
//
//            float distanceInMeters =  targetLocation.distanceTo(location);
        } else {
            gps.showSettingsAlert();
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        cityCurrent = addresses.get(0).getLocality();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                getRideAPI(mMap);
            }
        } else {
            List<Address> addresses;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0) {
                    cityCurrent = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getRideAPI(mMap);
        }


    }

    @Override
    public void onBackPressed() {
        if (h1 != null) {
            Log.e("#####", "updateVehicleListLongTime :handler ");
            h1.removeCallbacksAndMessages(null);
        }
        if (comeFrom.equals("startRideDetail")) {
            finish();
        } else {
            permissionDialog();
        }

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
    public void onConnected(Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000); //5 seconds
        mLocationRequest.setFastestInterval(1000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(getActivity(), "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), " ConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        currentLatLong = latLng;
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                Log.d("#######", "city : " + cityCurrent);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    private void getMessageFromNotification() {
        mReceiveMessageFromNotification = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                android.util.Log.d(TAG, "data: " + "app open notif START RIDE main activity 1");
                if (intent.getAction().equals(MyFirebaseMessagingService.COMPLETE_RIDE)) {
                    android.util.Log.d(TAG, "data: " + "app open notif main activity");
                    if (intent.getExtras() != null) {
                        android.util.Log.d(TAG, "data: " + "app open notif main activity");
                        String mRideid = intent.getStringExtra("i_ride_id");
                        Intent in = new Intent(StartRideActivity.this, CompleteRide.class);
                        in.putExtra("i_ride_id", mRideid);
                        startActivity(in);
                        finishAffinity();
                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
//        App.activityPaused();
        Log.d("##########", "PAUSE");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mReceiveMessageFromNotification);
    }

    @Override
    public void onResume() {
        super.onResume();
//        App.activityResumed();

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiveMessageFromNotification,
                new IntentFilter(MyFirebaseMessagingService.COMPLETE_RIDE));
        Log.e("#########", "Receiver : ");
    }
}
