package com.crest.goyo.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crest.goyo.AdapterClasses.RecyclerBookRidesAdapter;
import com.crest.goyo.AdapterClasses.RecyclerItemClickListener;
import com.crest.goyo.AdapterClasses.RideCancelAdapter;
import com.crest.goyo.MainActivity;
import com.crest.goyo.ModelClasses.ChargesModel;
import com.crest.goyo.ModelClasses.RecyclerBookRideModel;
import com.crest.goyo.ModelClasses.RideCancelModel;
import com.crest.goyo.R;
import com.crest.goyo.ScheduleRideDetail;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.GPSTracker;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.ServiceHandler;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.crest.goyo.logger.Log;
import com.crest.goyo.logger.LogWrapper;
import com.crest.goyo.other.CircleTransform;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;
import okhttp3.Request;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static android.view.View.VISIBLE;
import static com.android.volley.VolleyLog.TAG;


public class BookYourRideFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match

    private TextView tv_surcharge, tv_pin, tv_driver_name, tv_vehicle_type_driver, tv_ph_no, tv_saved_drop_location, tv_saved_pickup_from, tv_total, tv_vehicle_type, tv_enter_promocode, tv_pickup_date, tv_pickup_time, tv_pickup_from, tv_drop_location;
    private LinearLayout lay_total, lay_map_saved_location, lay_map_selection_location, lay_drop_location, lay_pickup_from, lay_ride_now, lay_book_your_ride_detail, lay_book_your_ride, lay_confirm_booking, lay_cancel_booking, lay_ride_later, lay_schedule_your_ride, lay_schedule_cancel, lay_booking_back, lay_cancel_book_ride, lay_schedule_now;
    private Button bt_call;
    private ImageView ic_calender, ic_timer, img_driver_profile;
    private EditText et_reason, et_add_promocode;
    private ProgressDialog pd;
    private GoogleMap mMap;
    private View view;
    private Location location;
    private LocationManager locManager;
    private Geocoder geocoder;
    private LatLng dest, origin;
    private ArrayList<LatLng> MarkerPoints;
    private float total = 0;
    private double temp;
    private GPSTracker gps;
    private double gpsLat, gpsLong;
    private CameraPosition cameraPosition;
    private CameraUpdate cameraUpdate;
    private Calendar cal;
    private String scheduleTime;
    int posVehicleTypes = 0;
    private double chargesLat, chargesLong, driverLat, driverLong;
    private Marker greenMarker, redMarker, vehicleMarker, driverMarker, customerMarker;
    private AlertDialog.Builder builder;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RecyclerView rv_book_ride, rv_schedule_ride;
    private JSONObject data,charges;
    private RideCancelAdapter adapter;
    private LatLng greenLatLng, redLatLng;
    private String showEstimationCharge;
    private String rideDistance;
    private String rideTime;
    private String cityCurrent;
    private String dialogMessage;
    private String AM_PM = " AM", mm_precede = "";
    private String vehicleStatus;
    private String CHARGE_SERVICE_TAX, CHARGE_MIN_CHARGE, CHARGE_BASE_FARE, CHARGE_UPTO_KM, CHARGE_UPTO_KM_CHARGE, CHARGE_AFTER_KM, CHARGE_RIDE_TIME_PICKUP_CHARGE, CHARGE_RIDE_TIME_WAIT_CHARGE;
    private Dialog dialog;
    private Handler myhandler;
    private RecyclerBookRidesAdapter recyclerBookRidesAdapter;
    private ArrayList<RecyclerBookRideModel> vehicleTypes;
    private ArrayList<ChargesModel> vhicleCharges;
    private List<RideCancelModel> list = new ArrayList<>();
    private static final int REQUEST_CODE_PICKUP = 1;
    private static final int REQUEST_CODE_DROP = 2;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book_my_ride, container, false);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        initializeMap();
        initUI(view);
        loadCalanderView();
        recyclerviewItemClick();

        return view;

    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_ride_now:
                rideRequestValidations();
                break;

            case R.id.lay_schedule_now:
                dateConvert();
                rideRequestValidations();
                break;

            case R.id.lay_confirm_booking:
                confirmRideAPIAsync();
                break;

            case R.id.lay_cancel_booking:
                bookingCancelDialog();
                break;

            case R.id.lay_ride_later:
                lay_book_your_ride.setVisibility(View.GONE);
                lay_schedule_your_ride.setVisibility(VISIBLE);
                getAvalableVehiclesAPI();
                break;

            case R.id.lay_schedule_cancel:
                lay_schedule_your_ride.setVisibility(View.GONE);
                lay_book_your_ride.setVisibility(VISIBLE);
                break;

            case R.id.lay_booking_back:
                lay_book_your_ride_detail.setVisibility(View.GONE);
                lay_map_saved_location.setVisibility(View.GONE);
                lay_map_selection_location.setVisibility(View.VISIBLE);
                lay_book_your_ride.setVisibility(VISIBLE);
                break;

            case R.id.ic_calender:
                calanderPickerDialog();
                break;

            case R.id.ic_timer:
                timePickerDialog();
                break;

            case R.id.lay_pickup_from:
                selectPickupLocation();
                break;

            case R.id.lay_drop_location:
                selectDropLocation();
                break;

            case R.id.tv_enter_promocode:
                enterPromoCodeDialog();
                break;

            case R.id.rv_book_ride:
                break;

            case R.id.tv_pickup_date:
                calanderPickerDialog();
                break;

            case R.id.tv_pickup_time:
                timePickerDialog();
                break;

            case R.id.bt_call:
                String phone = tv_ph_no.getText().toString();
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts(
                        "tel", phone, null));
                startActivity(phoneIntent);
                break;
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getAvalableVehiclesAPI();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                onMapReadyGettingLocation();
            }
        } else {
            onMapReadyGettingLocation();
        }

    }

    private void onMapReadyGettingLocation() {
        gps = new GPSTracker(getActivity(), getActivity());
        if (gps.canGetLocation()) {
            gpsLat = gps.getLatitude();
            gpsLong = gps.getLongitude();
            chargesLat = gpsLat;
            chargesLong = gpsLong;
            try {
                mMap.setMyLocationEnabled(true);
                origin = new LatLng(gpsLat, gpsLong);
                geocoder = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses;
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                addresses = geocoder.getFromLocation(gpsLat, gpsLong, 1);
                String address = addresses.get(0).getAddressLine(0);
                cityCurrent = addresses.get(0).getLocality();
                tv_pickup_from.setText("" + address + ", " + cityCurrent);
                greenMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(gpsLat, gpsLong))
                        .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));
                cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(gpsLat, gpsLong))
                        .zoom(18)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } catch (IOException e) {
                e.printStackTrace();

            }
        } else {
            gps.showSettingsAlert();
        }
    }

    private void initializeMap() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        locManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
            View mapView = mapFrag.getView();
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlp.setMargins(0, 180, 180, 20);
        }
    }

    public void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);
        Log.i("GoYo", "Ready");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Place place = PlaceAutocomplete.getPlace(getContext(), data);
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        if (requestCode == REQUEST_CODE_PICKUP) {
            if (resultCode == RESULT_OK) {
                origin = place.getLatLng();
                tv_pickup_from.setText(place.getAddress());
                greenLatLng = place.getLatLng();
                chargesLat = origin.latitude;
                chargesLong = origin.longitude;
                gpsLat = origin.latitude;
                gpsLong = origin.longitude;
                mMap.clear();
                getVehiclesListAPI(vehicleTypes.get(posVehicleTypes).getType());
                greenMarker = mMap.addMarker(new MarkerOptions()
                        .position(greenLatLng)
                        .title("" + place.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));
                cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(14).build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if (redMarker != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if (vehicleMarker != null) {
                        builder.include(vehicleMarker.getPosition());
                        builder.include(redMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen
                        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cameraUpdate);
                    } else if (greenMarker != null) {
                        builder.include(greenMarker.getPosition());
                        builder.include(redMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen
                        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cameraUpdate);
                    }
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == REQUEST_CODE_DROP) {
            if (resultCode == RESULT_OK) {
                dest = place.getLatLng();
                redLatLng = place.getLatLng();
                chargesLat = dest.latitude;
                chargesLong = dest.longitude;
                tv_drop_location.setText(place.getAddress());
                if (redMarker != null) {
                    redMarker.remove();
                }
                redMarker = mMap.addMarker(new MarkerOptions()
                        .position(redLatLng)
                        .title("" + place.getName())
                        .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_drop))));
                cameraPosition = new CameraPosition.Builder()
                        .target(place.getLatLng())
                        .zoom(14).build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if (redMarker != null) {
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    if (vehicleMarker != null) {
                        builder.include(vehicleMarker.getPosition());
                        builder.include(redMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen
                        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cameraUpdate);
                    } else if (greenMarker != null) {
                        builder.include(greenMarker.getPosition());
                        builder.include(redMarker.getPosition());
                        LatLngBounds bounds = builder.build();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen
                        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cameraUpdate);
                    }
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (myhandler != null) {
            myhandler.removeCallbacksAndMessages(null);
        }
        initializeLogging();
    }

    private void selectDropLocation() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("IN")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_DROP);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPickupLocation() {
        try {
            AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                    .setCountry("IN")
                    .build();
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .setFilter(typeFilter)
                            .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_PICKUP);
        } catch (GooglePlayServicesRepairableException e) {
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);
            Log.e(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void rideRequestValidations() {
        if (tv_pickup_from.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "Please enter pickup location", Toast.LENGTH_SHORT).show();
        } else {
            if (tv_drop_location.getText().toString().equals("")) {
                Toast.makeText(getActivity(), "Please enter drop location", Toast.LENGTH_SHORT).show();
            } else {
                if (vehicleStatus.equals("0")) {
                    Toast.makeText(getActivity(), "No vehicles available.", Toast.LENGTH_SHORT).show();
                } else {
                    if (Constant.isOnline(getActivity())) {
                        downloadUrl();
                    }
                }
            }
        }
    }

    private void getVehicleTypeCharge(String type) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_VEHICLE_CHARGE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("city", cityCurrent);
        urlBuilder.addQueryParameter("vehicle_type", type);
        urlBuilder.addQueryParameter("latitude", String.valueOf(chargesLat));
        urlBuilder.addQueryParameter("longitude", String.valueOf(chargesLong));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                String value = String.valueOf(success);
                android.util.Log.e("value", "    " + value);
                if (value.equals("0")) {
                } else {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject l_data = data.getJSONObject("l_data");
                        charges = l_data.getJSONObject("charges");

                        vhicleCharges.add(new ChargesModel(charges.getString("min_charge"), charges.getString("base_fare"), charges.getString("upto_km"), charges.getString("upto_km_charge"), charges.getString("after_km_charge"), charges.getString("ride_time_pick_charge"), charges.getString("ride_time_charge"), charges.getString("service_tax")));

                        showEstimationCharge = charges.getString("i_show_estimate_charge");
                        CHARGE_MIN_CHARGE = charges.getString("min_charge");
                        CHARGE_BASE_FARE = charges.getString("base_fare");
                        CHARGE_UPTO_KM = charges.getString("upto_km");
                        CHARGE_UPTO_KM_CHARGE = charges.getString("upto_km_charge");
                        CHARGE_AFTER_KM = charges.getString("after_km_charge");
                        CHARGE_RIDE_TIME_PICKUP_CHARGE = charges.getString("ride_time_pick_charge");
                        CHARGE_RIDE_TIME_WAIT_CHARGE = charges.getString("ride_time_charge");
                        CHARGE_SERVICE_TAX = charges.getString("service_tax");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    private void confirmRideAPIAsync() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_CONFIRM_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getActivity(), Preferences.RIDE_ID));
        urlBuilder.addQueryParameter("payment_mode", "cash");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        Request request = new Request.Builder()
                .url(newurl)
                .build();
        new GetConfirmRide().execute(newurl);
    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + tv_pickup_from.getText().toString().trim();
        String str_dest = "destination=" + tv_drop_location.getText().toString().trim();
        String sensor = "sensor=true";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters.trim();

        return url;
    }

    private void saveRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_SAVE_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("vehicle_type", vehicleTypes.get(posVehicleTypes).getType());
        urlBuilder.addQueryParameter("pickup_address", tv_pickup_from.getText().toString());
        urlBuilder.addQueryParameter("pickup_latitude", String.valueOf(gpsLat));
        urlBuilder.addQueryParameter("pickup_longitude", String.valueOf(gpsLong));
        urlBuilder.addQueryParameter("destination_address", tv_drop_location.getText().toString());
        urlBuilder.addQueryParameter("destination_latitude", String.valueOf(dest.latitude));
        urlBuilder.addQueryParameter("destination_longitude", String.valueOf(dest.longitude));
        urlBuilder.addQueryParameter("estimate_amount", String.valueOf(total));
        urlBuilder.addQueryParameter("estimate_km", String.valueOf(rideDistance));
        urlBuilder.addQueryParameter("estimate_time", String.valueOf(rideTime));
        urlBuilder.addQueryParameter("city", String.valueOf(cityCurrent));
        urlBuilder.addQueryParameter("charges", String.valueOf(charges));
        if (lay_book_your_ride.getVisibility() == VISIBLE) {
            urlBuilder.addQueryParameter("ride_type", "ride_now");
        } else if (lay_schedule_your_ride.getVisibility() == VISIBLE) {
            urlBuilder.addQueryParameter("ride_type", "ride_later");
            urlBuilder.addQueryParameter("ride_time", scheduleTime);
        }
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        final okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject data = response.getJSONObject("data");

                        if (lay_schedule_your_ride.getVisibility() == VISIBLE) {
                            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), ScheduleRideDetail.class);
                            intent.putExtra("i_ride_id", "" + data.getString("i_ride_id"));
                            startActivity(intent);
                        } else if (lay_book_your_ride.getVisibility() == VISIBLE) {
                            Preferences.setValue(getActivity(), Preferences.RIDE_ID, data.getString("i_ride_id"));
                            lay_book_your_ride.setVisibility(View.GONE);
                            lay_map_selection_location.setVisibility(View.GONE);
                            lay_map_saved_location.setVisibility(View.VISIBLE);
                            lay_book_your_ride_detail.setVisibility(VISIBLE);

                            getRideAPI();
                        }
                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void getRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getActivity(), Preferences.RIDE_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        JSONObject l_data = jsonObject.getJSONObject("l_data");
                        String vehicle_type = l_data.getString("vehicle_type");
                        String estimate_amount = l_data.getString("estimate_amount");
                        String pickup_address = l_data.getString("pickup_address");
                        String destination_addres = l_data.getString("destination_address");
                        if (lay_book_your_ride_detail.getVisibility() == VISIBLE) {
                            if (estimate_amount.equals("0.0")) {
                                lay_total.setVisibility(View.GONE);
                            } else {
                                tv_total.setText("\u20B9" + " " + estimate_amount);
                            }
                            tv_surcharge.setText(l_data.getJSONObject("charges").getString("surcharge") + "x");
                            tv_vehicle_type.setText(vehicle_type);
                            tv_saved_pickup_from.setText(pickup_address);
                            tv_saved_drop_location.setText(destination_addres);
                        } else if (lay_cancel_book_ride.getVisibility() == VISIBLE) {
                            mMap.clear();
                            mMap.setMyLocationEnabled(true);
                            ((MainActivity) getActivity()).getSupportActionBar().setTitle("PICKUP ARRIVING");
                            Preferences.setValue(getActivity(), Preferences.DRIVER_ID, jsonObject.getString("i_driver_id").toString());
                            JSONObject driver_data = jsonObject.getJSONObject("driver_data");
                            tv_pin.setText("Your trip confirmation PIN : " + jsonObject.getString("v_pin"));
                            tv_driver_name.setText(driver_data.getString("driver_name"));
                            tv_ph_no.setText(driver_data.getString("driver_phone"));
                            tv_vehicle_type_driver.setText(l_data.getString("vehicle_type"));
                            if (driver_data.getString("driver_image").equals("")) {
                                img_driver_profile.setImageResource(R.drawable.no_user);
                            } else {
                                Glide.with(getActivity()).load(driver_data.getString("driver_image"))
                                        .crossFade()
                                        .thumbnail(0.5f)
                                        .bitmapTransform(new CircleTransform(getContext()))
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(img_driver_profile);
                            }
                            getDriverLocationAPI();
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void getDriverLocationAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", Preferences.getValue_String(getActivity(), Preferences.DRIVER_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        final LatLng driver = new LatLng(driverLat, driverLong);
                        final LatLng customer = new LatLng(gpsLat, gpsLong);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(vehicleTypes.get(posVehicleTypes).getPlotting_icon());
                                    Log.d("#####", "icon : " + vehicleTypes.get(posVehicleTypes).getPlotting_icon());
                                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            driverMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(driver)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                                            customerMarker = mMap.addMarker(new MarkerOptions().position(customer).icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_driver))));
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(driverMarker.getPosition());
                                            builder.include(customerMarker.getPosition());
                                            LatLngBounds bounds = builder.build();
                                            int width = getResources().getDisplayMetrics().widthPixels;
                                            int height = getResources().getDisplayMetrics().heightPixels;
                                            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                                            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                            mMap.animateCamera(cameraUpdate);

                                            updateDriverLocation();
                                        }
                                    });

                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        });

    }

    private void getDriverLocationAPIThread() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", Preferences.getValue_String(getActivity(), Preferences.DRIVER_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClassNew.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        final LatLng driver = new LatLng(driverLat, driverLong);
                        final LatLng customer = new LatLng(gpsLat, gpsLong);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (vehicleTypes.size() == 0) {
                                        Log.e(TAG, "TAG : vehicle types == 0 :" + vehicleTypes.size());
                                    } else {
                                        Log.e(TAG, "TAG : vehicle types != 0 :" + vehicleTypes.size());
                                        URL url = new URL(vehicleTypes.get(posVehicleTypes).getPlotting_icon());
                                        final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                driverMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(driver)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                                                customerMarker = mMap.addMarker(new MarkerOptions().position(customer).icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_driver))));
                                                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                builder.include(driverMarker.getPosition());
                                                builder.include(customerMarker.getPosition());
                                                LatLngBounds bounds = builder.build();
                                                int width = getResources().getDisplayMetrics().widthPixels;
                                                int height = getResources().getDisplayMetrics().heightPixels;
                                                int padding = (int) (width * 0.40);
                                                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                                mMap.animateCamera(cameraUpdate);
                                            }
                                        });
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }


            }
        });

    }

    private void getRideCancelReasonApi() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_RIDE_CANCEL).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_type", "user");
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("lang", "en");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        list.clear();
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            list.add(new RideCancelModel(jsonObject.getString("j_title"), jsonObject.getString("id")));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);

    }

    private void userCancelScheduleRide(final Dialog dialog) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_RIDE_CANCEL).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getActivity(), Preferences.RIDE_ID));
        urlBuilder.addQueryParameter("cancel_reason_id", Preferences.getValue_String(getActivity(), "cancel_id"));
        urlBuilder.addQueryParameter("cancel_reason_text", et_reason.getText().toString());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void promotionCodeExistsAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_APPLY_PROMOCODE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("city", cityCurrent);
        urlBuilder.addQueryParameter("v_code", et_add_promocode.getText().toString());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void updateDriverLocation() {
        myhandler = new Handler();
        myhandler.postDelayed(new Runnable() {
            public void run() {
                getDriverLocationAPIThread();
                myhandler.postDelayed(this, 6000); //now is every 2 minutes
            }
        }, 6000);
    }

    private void enterPromoCodeDialog() {
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_promocode);
        dialog.setCancelable(false);
        Button bt_accept = (Button) dialog.findViewById(R.id.bt_accept);
        Button bt_denied = (Button) dialog.findViewById(R.id.bt_denied);
        et_add_promocode = (EditText) dialog.findViewById(R.id.et_add_promocode);
        bt_denied.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        bt_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_add_promocode.getText().toString().equals("")) {
                    et_add_promocode.setError("Please enter promocode.");
                } else {
                    promotionCodeExistsAPI();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void timePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        String AM_PM = " AM";
                        String mm_precede = "";
                        if (hourOfDay >= 12) {
                            AM_PM = " PM";
                            if (hourOfDay >= 13 && hourOfDay < 24) {
                                hourOfDay -= 12;
                            } else {
                                hourOfDay = 12;
                            }
                        } else if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if (minute < 10) {
                            mm_precede = "0";
                        }
                        tv_pickup_time.setText("" + hourOfDay + ":" + mm_precede + minute + AM_PM);

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void calanderPickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        tv_pickup_date.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void bookingCancelDialog() {
        builder.setTitle("Booking Cancel");
        builder.setMessage("Are you sure you want to cancel the ride?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                showReqCancelDialog();
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

    private void showReqCancelDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_request_cancel_reason);
        RecyclerView rv_cancel_reason = (RecyclerView) dialog.findViewById(R.id.rv_cancel_reason);
        et_reason = (EditText) dialog.findViewById(R.id.et_reason);
        Button bt_cancel = (Button) dialog.findViewById(R.id.bt_cancel);
        Button bt_done = (Button) dialog.findViewById(R.id.bt_done);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_cancel_reason.setLayoutManager(layoutManager);
        adapter = new RideCancelAdapter(list);
        rv_cancel_reason.setAdapter(adapter);
        if (Constant.isOnline(getActivity())) {
            getRideCancelReasonApi();
        }
        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isOnline(getActivity())) {
                    userCancelScheduleRide(dialog);
                }
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void bookingSuccessfullDialog() {
        builder.setTitle("Booking Sucessful");
        builder.setMessage(dialogMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                lay_book_your_ride_detail.setVisibility(View.GONE);
                lay_cancel_book_ride.setVisibility(VISIBLE);
                getRideAPI();
            }
        });
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_correct);
        builder.show();
    }

    private void getAvalableVehiclesAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_VEHICLE_TYPES).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("city", cityCurrent);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                String value = String.valueOf(success);
                android.util.Log.e("value", "    " + value);
                if (value.equals("0")) {
                } else {
                    try {
                        data = response.getJSONObject("data");
                        vehicleTypes.clear();
                        for (int i = 1; i < data.length() + 1; i++) {
                            JSONObject objData = data.getJSONObject(String.valueOf(i));
                            JSONObject l_data = objData.getJSONObject("l_data");
                            String id = objData.getString("id");
                            String name = objData.getString("v_name");
                            String type = objData.getString("v_type");
                            vehicleTypes.add(new RecyclerBookRideModel(id, name, type, l_data.getString("list_icon"), l_data.getString("active_icon"), l_data.getString("plotting_icon")));
                            if (lay_book_your_ride.getVisibility() == View.VISIBLE) {
                                recyclerBookRidesAdapter = new RecyclerBookRidesAdapter(vehicleTypes);
                                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                                rv_book_ride.setLayoutManager(llm);
                                llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rv_book_ride.setItemAnimator(new DefaultItemAnimator());
                                rv_book_ride.setAdapter(recyclerBookRidesAdapter);
                            }
                            if (lay_schedule_your_ride.getVisibility() == View.VISIBLE) {
                                recyclerBookRidesAdapter = new RecyclerBookRidesAdapter(vehicleTypes);
                                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                                rv_schedule_ride.setLayoutManager(llm);
                                llm.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rv_schedule_ride.setItemAnimator(new DefaultItemAnimator());
                                rv_schedule_ride.setAdapter(recyclerBookRidesAdapter);
                            }

                            try {
                                getVehiclesListAPI(vehicleTypes.get(posVehicleTypes).getType());
                            } catch (Exception e) {

                            }

                        }
                        getVehicleTypeCharge(vehicleTypes.get(0).getType());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, false);
    }

    private void countAmount() {
        int min_charge = Integer.parseInt(CHARGE_MIN_CHARGE);
        int base_fare = Integer.parseInt(CHARGE_BASE_FARE);
        int upto_km = Integer.parseInt(CHARGE_UPTO_KM);
        int upto_km_charge = Integer.parseInt(CHARGE_UPTO_KM_CHARGE);
        float service_tax = Float.parseFloat(CHARGE_SERVICE_TAX);

        if (min_charge > 0) {
            total = min_charge;
        }

        if (base_fare > 0) {
            total += base_fare;
        }
        Float i = Float.valueOf(rideDistance.substring(0, rideDistance.indexOf("km")));
        float dis = i.floatValue();
        if (upto_km > dis) {

            total += dis + upto_km_charge;
            total = (total + service_tax);
            android.util.Log.d("######", "total : if   " + total);
        } else {
            temp = (dis - upto_km);
            total += (temp * upto_km_charge);
            total += (upto_km * upto_km_charge);
            total = total + service_tax;
            android.util.Log.d("######", "total :  else  " + total);
        }
        android.util.Log.d("######", "total :  total " + total);

    }


    private void getVehiclesListAPI(String vehicleType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_VEHICLE_LIST).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_type", vehicleType);
        urlBuilder.addQueryParameter("l_latitude", String.valueOf(gpsLat));
        urlBuilder.addQueryParameter("l_longitude", String.valueOf(gpsLong));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                vehicleStatus = String.valueOf(success);
                if (vehicleStatus.equals("0")) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            String id = objData.getString("id");
                            String lati = objData.getString("l_latitude");
                            String longi = objData.getString("l_longitude");
                            String latlng = objData.getString("latlong");
                            final LatLng point = new LatLng(Double.parseDouble(lati), Double.parseDouble(longi));
                            MarkerPoints.clear();
                            MarkerPoints.add(point);
                            MarkerOptions options = new MarkerOptions();
                            options.position(point);
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        URL url = new URL(vehicleTypes.get(posVehicleTypes).getPlotting_icon());
                                        final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                        if (getActivity() == null)
                                            return;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                vehicleMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(point)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                                            }
                                        });

                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            thread.start();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, false);
    }

    private void downloadUrl() {
        String url1 = getUrl(origin, dest);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url1).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    JSONObject duration = steps.getJSONObject("duration");
                    rideDistance = distance.getString("text");
                    rideTime = duration.getString("text");
                    Log.i("#########" + "duration", rideTime);
                    if (Integer.parseInt(showEstimationCharge) == 1) {
                        Log.d("#####", "estimation : " + showEstimationCharge);
                        countAmount();
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            saveRideAPI();
                        }
                    });
                } catch (Exception e) {
                    android.util.Log.d("Exception.", e.toString());
                }
            }
        }, true);
    }

    private class GetConfirmRide extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.show();
            pd.setMessage("Please wait..!!");
            pd.setCancelable(false);

        }

        @Override
        protected Void doInBackground(String... url) {
            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url[0], ServiceHandler.GET);
            if (jsonStr != null) {
                try {
                    JSONObject response = new JSONObject(jsonStr);
                    int responce_status = response.getInt(VolleyTAG.status);
                    dialogMessage = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject data = response.getJSONObject("data");
                        new Thread() {
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        bookingSuccessfullDialog();
                                    }
                                });
                            }
                        }.start();
                    } else {
                        new Thread() {
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        builder.setMessage(dialogMessage);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                        builder.setCancelable(false);
                                        builder.show();
                                    }
                                });
                            }
                        }.start();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                android.util.Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            pd.dismiss();

        }
    }

    private void recyclerviewItemClick() {
        rv_schedule_ride.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        posVehicleTypes = position;
                        mMap.clear();
                        createPickupDropMarkers();
                        if (Constant.isOnline(getActivity())) {
                            getVehiclesListAPI(vehicleTypes.get(position).getType());
                            getVehicleTypeCharge(vehicleTypes.get(position).getType());
                        }
                    }
                })
        );
        rv_book_ride.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        posVehicleTypes = position;
                        mMap.clear();
                        createPickupDropMarkers();
                        if (Constant.isOnline(getActivity())) {
                            getVehiclesListAPI(vehicleTypes.get(position).getType());
                            getVehicleTypeCharge(vehicleTypes.get(position).getType());
                        }
                    }
                })
        );
    }

    private void createPickupDropMarkers() {
        if (greenMarker != null) {
            greenMarker = mMap.addMarker(new MarkerOptions()
                    .position(origin)
                    .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));
        } else {

        }
        if (redMarker != null) {
            redMarker = mMap.addMarker(new MarkerOptions()
                    .position(dest)
                    .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_drop))));
        } else {

        }
    }

    private void dateConvert() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = parseFormat.parse(tv_pickup_time.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(tv_pickup_date.getText().toString() + " " + displayFormat.format(date));
        scheduleTime = tv_pickup_date.getText().toString() + " " + displayFormat.format(date);
    }

    private void loadCalanderView() {
        try {
            cal = Calendar.getInstance();
            mYear = cal.get(Calendar.YEAR);
            mMonth = cal.get(Calendar.MONTH);
            mDay = cal.get(Calendar.DAY_OF_MONTH);
            mHour = cal.get(Calendar.HOUR_OF_DAY);
            mMinute = cal.get(Calendar.MINUTE);
            if (mHour >= 12) {
                AM_PM = " PM";
                if (mHour >= 13 && mHour < 24) {
                    mHour -= 12;
                } else {
                    mHour = 12;
                }
            } else if (mHour == 0) {
                mHour = 12;
            }
            if (mMinute < 10) {
                mm_precede = "0";
            }

            tv_pickup_date.setText(mYear + "-" + (mMonth + 1) + "-" + mDay);
            tv_pickup_time.setText("" + mHour + ":" + mm_precede + mMinute + AM_PM);
        } catch (Exception e) {

        }

        if (myhandler != null) {
            myhandler.removeCallbacksAndMessages(null);
        } else {
        }
    }

    private void initUI(View view) {
        lay_ride_now = (LinearLayout) view.findViewById(R.id.lay_ride_now);
        lay_book_your_ride = (LinearLayout) view.findViewById(R.id.lay_book_your_ride);
        lay_book_your_ride_detail = (LinearLayout) view.findViewById(R.id.lay_book_your_ride_detail);
        lay_confirm_booking = (LinearLayout) view.findViewById(R.id.lay_confirm_booking);
        lay_cancel_booking = (LinearLayout) view.findViewById(R.id.lay_cancel_booking);
        lay_ride_later = (LinearLayout) view.findViewById(R.id.lay_ride_later);
        lay_schedule_your_ride = (LinearLayout) view.findViewById(R.id.lay_schedule_your_ride);
        lay_schedule_cancel = (LinearLayout) view.findViewById(R.id.lay_schedule_cancel);
        lay_booking_back = (LinearLayout) view.findViewById(R.id.lay_booking_back);
        lay_cancel_book_ride = (LinearLayout) view.findViewById(R.id.lay_cancel_book_ride);
        lay_schedule_now = (LinearLayout) view.findViewById(R.id.lay_schedule_now);
        lay_pickup_from = (LinearLayout) view.findViewById(R.id.lay_pickup_from);
        lay_drop_location = (LinearLayout) view.findViewById(R.id.lay_drop_location);
        lay_map_selection_location = (LinearLayout) view.findViewById(R.id.lay_map_selection_location);
        lay_map_saved_location = (LinearLayout) view.findViewById(R.id.lay_map_saved_location);
        lay_total = (LinearLayout) view.findViewById(R.id.lay_total);
        rv_book_ride = (RecyclerView) view.findViewById(R.id.rv_book_ride);
        rv_schedule_ride = (RecyclerView) view.findViewById(R.id.rv_schedule_ride);
        ic_calender = (ImageView) view.findViewById(R.id.ic_calender);
        tv_pickup_date = (TextView) view.findViewById(R.id.tv_pickup_date);
        tv_pickup_time = (TextView) view.findViewById(R.id.tv_pickup_time);
        ic_timer = (ImageView) view.findViewById(R.id.ic_timer);
        tv_pickup_from = (TextView) view.findViewById(R.id.tv_pickup_from);
        tv_drop_location = (TextView) view.findViewById(R.id.tv_drop_location);
        tv_enter_promocode = (TextView) view.findViewById(R.id.tv_enter_promocode);
        tv_vehicle_type = (TextView) view.findViewById(R.id.tv_vehicle_type);
        tv_saved_drop_location = (TextView) view.findViewById(R.id.tv_saved_drop_location);
        tv_saved_pickup_from = (TextView) view.findViewById(R.id.tv_saved_pickup_from);
        tv_total = (TextView) view.findViewById(R.id.tv_total);
        tv_pin = (TextView) view.findViewById(R.id.tv_pin);
        tv_surcharge = (TextView) view.findViewById(R.id.tv_surcharge);
        tv_driver_name = (TextView) view.findViewById(R.id.tv_driver_name);
        tv_vehicle_type_driver = (TextView) view.findViewById(R.id.tv_vehicle_type_driver);
        tv_ph_no = (TextView) view.findViewById(R.id.tv_ph_no);
        img_driver_profile = (ImageView) view.findViewById(R.id.img_driver_profile);
        bt_call = (Button) view.findViewById(R.id.bt_call);


        lay_ride_now.setOnClickListener(this);
        lay_confirm_booking.setOnClickListener(this);
        lay_cancel_booking.setOnClickListener(this);
        lay_ride_later.setOnClickListener(this);
        lay_schedule_your_ride.setOnClickListener(this);
        lay_schedule_cancel.setOnClickListener(this);
        lay_booking_back.setOnClickListener(this);
        ic_calender.setOnClickListener(this);
        ic_timer.setOnClickListener(this);
        lay_schedule_now.setOnClickListener(this);
        lay_pickup_from.setOnClickListener(this);
        lay_drop_location.setOnClickListener(this);
        tv_enter_promocode.setOnClickListener(this);
        bt_call.setOnClickListener(this);


        MarkerPoints = new ArrayList<>();
        vehicleTypes = new ArrayList<RecyclerBookRideModel>();
        vhicleCharges = new ArrayList<ChargesModel>();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myhandler != null) {
            myhandler.removeCallbacksAndMessages(null);
            Log.d("!!!!!!", "Thread successfully stopped. dv");
        }
        Log.d("!!!!!!", "stop fragment : dv");
    }

    @Override
    public void onStop() {
        super.onStop();

    }
}





