package com.crest.goyo.fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.crest.goyo.UpdateLocationService;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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


public class BookYourRideFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    private TextView tv_surcharge, tv_pin, tv_driver_name, tv_vehicle_type_driver, tv_ph_no, tv_saved_drop_location, tv_saved_pickup_from, tv_total, tv_vehicle_type, tv_pickup_date, tv_pickup_time, tv_pickup_from, tv_drop_location;
    private LinearLayout lay_total, lay_map_saved_location, lay_map_selection_location, lay_drop_location, lay_pickup_from, lay_ride_now, lay_book_your_ride_detail, tv_enter_promocode, lay_book_your_ride, lay_confirm_booking, lay_cancel_booking, lay_ride_later, lay_schedule_your_ride, lay_schedule_cancel, lay_booking_back, lay_cancel_book_ride, lay_schedule_now;
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
    private Integer mtime = 0;
    private double temp;
    private GPSTracker gps;
    private double gpsLat, gpsLong, changedLat, changedLong;
    private CameraPosition cameraPosition;
    private CameraUpdate cameraUpdate;
    private Calendar cal;
    private String scheduleTime;
    int posVehicleTypes = 0;
    private double driverLat, driverLong;
    private Marker greenMarker, redMarker, vehicleMarker, driverMarker, customerMarker;
    private AlertDialog.Builder builder;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private RecyclerView rv_book_ride, rv_schedule_ride;
    private JSONObject data, charges;
    private RideCancelAdapter adapter;
    private LatLng greenLatLng, redLatLng;
    private String showEstimationCharge;
    private String rideDistance;
    private String rideTime;
    private String cityCurrent;
    private String dialogMessage;
    private String AM_PM = " AM", mm_precede = "";
    /*hector*/
    private int vehicleStatus;
    private String CHARGE_SERVICE_TAX, CHARGE_MIN_CHARGE, CHARGE_BASE_FARE, CHARGE_UPTO_KM, CHARGE_UPTO_KM_CHARGE, CHARGE_AFTER_KM, CHARGE_RIDE_TIME_PICKUP_CHARGE, CHARGE_RIDE_TIME_WAIT_CHARGE;
    private Dialog dialog;
    private Handler myhandler;
    private RecyclerBookRidesAdapter recyclerBookRidesAdapter;
    private ArrayList<RecyclerBookRideModel> vehicleTypes;
    private ArrayList<ChargesModel> vhicleCharges;
    private List<RideCancelModel> list = new ArrayList<>();
    private static final int REQUEST_CODE_PICKUP = 1;
    private static final int REQUEST_CODE_DROP = 2;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    private boolean isChooseAddress = false;
    private View locationButton;
    private String updatedAddres = "";
    private Context mContext;
    private Integer timeInMinutes;
    private TextView mCanclePromocode;
    private String promocode_code;
    Location mLastLocation;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static BookYourRideFragment newInstance() {
        BookYourRideFragment bookYourRideFragment = new BookYourRideFragment();
        return bookYourRideFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_book_my_ride, container, false);

        mContext = getContext();

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        isChooseAddress = false;
        initializeMap();
        initUI(view);
        //loadCalanderView();
        recyclerviewItemClick();

        mCanclePromocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isOnline(getActivity())) {
                    removePrompcodeAPI();
                }
            }
        });

        getActivity().startService(new Intent(getActivity(),UpdateLocationService.class));

        return view;
    }

    private void removePrompcodeAPI() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_REMOVE_PROMO_CODE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("city", cityCurrent);
        urlBuilder.addQueryParameter("v_code", promocode_code);
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getActivity(), Preferences.RIDE_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        mCanclePromocode.setVisibility(View.GONE);
                        tv_enter_promocode.setVisibility(VISIBLE);
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
        });
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
                if (Constant.isOnline(getContext())) {
                    getAvalableVehiclesAPI();
                }
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

            case R.id.ll_promocode:
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
//        getAvalableVehiclesAPI();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        location = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                if (Preferences.getValue_String(getActivity(), "comefrom").equals("MyRides")) {
                    onMapReadyGettingLocationForConfirmBooking();
                    Preferences.setValue(getActivity(), "comefrom", "");
                } else {
                    onMapReadyGettingLocation();
                }

            }
        } else {
            if (Preferences.getValue_String(getActivity(), "comefrom").equals("MyRides")) {
                onMapReadyGettingLocationForConfirmBooking();
                Preferences.setValue(getActivity(), "comefrom", "");
            } else {
                onMapReadyGettingLocation();
            }
        }

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (lay_map_selection_location.getVisibility() == VISIBLE) {
                    List<Address> addresses;
                    try {
                        addresses = geocoder.getFromLocation(changedLat, changedLong, 1);
                        if (addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0);
                            String locality = addresses.get(0).getSubLocality();
                            String adminArea = addresses.get(0).getAdminArea();
                            cityCurrent = addresses.get(0).getLocality();
                            Preferences.setValue(getActivity(), Preferences.CITY, cityCurrent);
                            tv_pickup_from.setText("" + address + ", " + locality + ", " + cityCurrent + ", " + adminArea);
                            if (greenMarker != null) {
                                greenMarker.remove();
                            }
                            greenMarker = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(changedLat, changedLong))
                                    .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));

                            cameraPosition = new CameraPosition.Builder()
                                    .target(new LatLng(changedLat, changedLong))
                                    .bearing(20)
                                    .zoom(18).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


//                          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(changedLat, changedLong), 15));
                            isChooseAddress = false;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(changedLat, changedLong))
                            .bearing(20)
                            .zoom(18).build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(changedLat, changedLong), 15));
//                 isChooseAddress = false;
                }

                return true;
            }
        });

    }

    private void onMapReadyGettingLocationForConfirmBooking() {


//        buildGoogleApiClient();
//        mGoogleApiClient.connect();
        gps = new GPSTracker(getActivity(), getActivity());

        if (gps.canGetLocation()) {
            gpsLat = gps.getLatitude();
            gpsLong = gps.getLongitude();
            gps.stopGpsTrackerLocationUpdate();
            lay_cancel_book_ride.setVisibility(VISIBLE);
            lay_map_selection_location.setVisibility(View.GONE);
            lay_map_saved_location.setVisibility(View.VISIBLE);

            if (Constant.isOnline(getActivity())) {
                getRideAPIForConfirmBooking();
            }

//            getAvalableVehiclesAPI();
//            gpsLat = gps.getLatitude();
//            gpsLong = gps.getLongitude();
//            try {
//                mMap.setMyLocationEnabled(true);
//                origin = new LatLng(gpsLat, gpsLong);
//                geocoder = new Geocoder(getActivity(), Locale.getDefault());
//                List<Address> addresses;
//                LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//                addresses = geocoder.getFromLocation(gpsLat, gpsLong, 1);
//                if (addresses.size() > 0) {
//                    String address = addresses.get(0).getAddressLine(0);
//                    String locality = addresses.get(0).getSubLocality();
//                    String adminArea = addresses.get(0).getAdminArea();
//                    cityCurrent = addresses.get(0).getLocality();
//                    tv_pickup_from.setText("" + address + ", " + locality + ", " + cityCurrent + ", " + adminArea);
//                    greenMarker = mMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(gpsLat, gpsLong))
//                            .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));
//                    cameraPosition = new CameraPosition.Builder()
//                            .target(new LatLng(gpsLat, gpsLong))
//                            .zoom(15)
//                            .build();
//                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//
//            }
        } else {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);
            alertDialog.setTitle("GPS settings");
            alertDialog.setMessage("Your GPS seems to be disabled, do you want to enable it?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    getActivity().finish();
                    startActivity(intent);
                }
            });
            alertDialog.show();
        }
    }

    private void getRideAPIForConfirmBooking() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getActivity(), Preferences.RIDE_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
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
                        Preferences.setValue(getContext(), Preferences.VEHICLES_IMG, vehicle_type_data.getString("plotting_icon"));


                        JSONObject estimation = l_data.getJSONObject("estimation");
                        String vehicle_type = l_data.getString("vehicle_type");
                        String estimate_amount = estimation.getString("final_total");
                        String pickup_address = l_data.getString("pickup_address");
                        String destination_addres = l_data.getString("destination_address");
                        tv_saved_pickup_from.setText(pickup_address);
                        tv_saved_drop_location.setText(destination_addres);
                        if (lay_cancel_book_ride.getVisibility() == VISIBLE) {
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
                            if (Constant.isOnline(getContext())) {
                                getDriverLocationAPIForConfirmBooking();
                            }
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getDriverLocationAPIForConfirmBooking() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", Preferences.getValue_String(getActivity(), Preferences.DRIVER_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        mMap.clear();
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        final String plotting_icon = jsonObject.getString("plotting_icon");
                        final LatLng driver = new LatLng(driverLat, driverLong);
                        final LatLng customer = new LatLng(gpsLat, gpsLong);


                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(plotting_icon);
                                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    final Bitmap newBitmap = getResizedBitmap(bmp, 70, 70);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Marker dr = mMap.addMarker(new MarkerOptions()
                                                    .position(driver)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));

                                            Marker cu = mMap.addMarker(new MarkerOptions().position(customer).icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_driver))));
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(dr.getPosition());
                                            builder.include(cu.getPosition());
                                            LatLngBounds bounds = builder.build();
                                            int width = getResources().getDisplayMetrics().widthPixels;
                                            int height = getResources().getDisplayMetrics().heightPixels;
                                            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                                            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                            mMap.animateCamera(cameraUpdate);
                                            updateDriverLocationForConfirmBooking();
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

    private void updateDriverLocationForConfirmBooking() {

        h3.postDelayed(new Runnable() {
            public void run() {
                if (Constant.isOnline(getContext())) {
                    getDriverLocationAPIThreadForConfirmBooking();
                }
                h3.postDelayed(this, 6000); //now is every 2 minutes
            }
        }, 6000);
    }

    private void getDriverLocationAPIThreadForConfirmBooking() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", Preferences.getValue_String(getActivity(), Preferences.DRIVER_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        mMap.clear();
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        final String plotting_icon = jsonObject.getString("plotting_icon");
                        final LatLng driver = new LatLng(driverLat, driverLong);
                        final LatLng customer = new LatLng(gpsLat, gpsLong);
                        Log.e("########", "cu lat long :" + customer);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(plotting_icon);
                                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Marker dr = mMap.addMarker(new MarkerOptions()
                                                    .position(driver)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(bmp)));
                                            Marker cu = mMap.addMarker(new MarkerOptions().position(customer).icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_driver))));
                                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                            builder.include(dr.getPosition());
                                            builder.include(cu.getPosition());
                                            LatLngBounds bounds = builder.build();
                                            int width = getResources().getDisplayMetrics().widthPixels;
                                            int height = getResources().getDisplayMetrics().heightPixels;
                                            int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen
                                            cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                            mMap.animateCamera(cameraUpdate);
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

    private void onMapReadyGettingLocation() {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        gps = new GPSTracker(getActivity(), getActivity());
        if (gps.canGetLocation()) {
            if (Constant.isOnline(getContext())) {
                getAvalableVehiclesAPI();
            }
            gpsLat = gps.getLatitude();
            gpsLong = gps.getLongitude();
            gps.stopGpsTrackerLocationUpdate();
            if (Constant.isOnline(getContext())) {
                try {
                    mMap.setMyLocationEnabled(true);
                    origin = new LatLng(gpsLat, gpsLong);
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(gpsLat, gpsLong, 1);
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        String locality = addresses.get(0).getSubLocality();
                        String adminArea = addresses.get(0).getAdminArea();
                        cityCurrent = addresses.get(0).getLocality();
                        Log.w("city","City = "+cityCurrent);
                        Preferences.setValue(getActivity(), Preferences.CITY, cityCurrent);
                        tv_pickup_from.setText("" + address + ", " + locality + ", " + cityCurrent + ", " + adminArea);
                        greenMarker = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(gpsLat, gpsLong))
                                .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));
                        cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(gpsLat, gpsLong))
                                .zoom(15)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        } else {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);
            alertDialog.setTitle("GPS settings");
            alertDialog.setMessage("Your GPS seems to be disabled, do you want to enable it?");
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    getActivity().finish();
                    startActivity(intent);
                }
            });
            alertDialog.show();
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
            locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
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
                isChooseAddress = true;
                greenLatLng = place.getLatLng();
                gpsLat = origin.latitude;
                gpsLong = origin.longitude;
                mMap.clear();
                if (Constant.isOnline(getContext())) {
                    getVehiclesListAPI(vehicleTypes.get(posVehicleTypes).getType());
                }
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
                    redMarker.remove();
                    redMarker = mMap.addMarker(new MarkerOptions()
                            .position(redLatLng)
                            .title("" + place.getName())
                            .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_drop))));
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
            } else if (resultCode == RESULT_CANCELED) {
            }
        } else if (requestCode == REQUEST_CODE_DROP) {
            if (resultCode == RESULT_OK) {
                dest = place.getLatLng();
                redLatLng = place.getLatLng();
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
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    protected synchronized void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {
//        Toast.makeText(getActivity(), "onConnectionSuspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), " ConnectionFailed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (getActivity() != null) {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                changedLat = location.getLatitude();
                changedLong = location.getLongitude();
                List<Address> addresses;
                try {
                    geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        String address = addresses.get(0).getAddressLine(0);
                        String locality = addresses.get(0).getSubLocality();
                        String adminArea = addresses.get(0).getAdminArea();

                        cityCurrent = "";
                        if (addresses.get(0).getLocality() != null && !addresses.get(0).getLocality().isEmpty())
                            cityCurrent = addresses.get(0).getLocality();

                        updatedAddres = address + ", " + locality + ", " + cityCurrent + ", " + adminArea;
                        if (!isChooseAddress) {
                            tv_pickup_from.setText(updatedAddres);
//                    greenMarker.setPosition(latLng);
                            if (lay_map_selection_location.getVisibility() == VISIBLE) {
                                if (greenMarker != null) {
                                    greenMarker.remove();
                                }
                                greenMarker = mMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .icon(BitmapDescriptorFactory.fromBitmap(Constant.setMarkerPin(getActivity(), R.drawable.marker_pickup))));
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
            }
        }


//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (myhandler != null) {
            myhandler.removeCallbacksAndMessages(null);
        }
        if (h1 != null) {
            h1.removeCallbacksAndMessages(null);
        }
        if (h2 != null) {
            h2.removeCallbacksAndMessages(null);
        }
        if (h3 != null) {
            h3.removeCallbacksAndMessages(null);
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
                if (vehicleStatus == 0) {
                    Toast.makeText(getActivity(), "No vehicles found.", Toast.LENGTH_SHORT).show();
                } else {
                    if (Constant.isOnline(getActivity())) {
                        if (h1 != null) {
                            h1.removeCallbacksAndMessages(null);
                        }
                        if (h2 != null) {
                            h2.removeCallbacksAndMessages(null);
                        }
                        if (h3 != null) {
                            h3.removeCallbacksAndMessages(null);
                        }
                        if (Constant.isOnline(getContext())) {
                            downloadUrl();
                        }
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
        urlBuilder.addQueryParameter("latitude", String.valueOf(gpsLat));
        urlBuilder.addQueryParameter("longitude", String.valueOf(gpsLong));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                String value = String.valueOf(success);
                if (value.equals("0")) {
                    android.util.Log.e("GetVehicleTypeCharge", "onResult: Zero Value");
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
        Log.d("########", "URL_CONFIRM_RIDE : " + newurl);
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

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters.trim();
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
//        urlBuilder.addQueryParameter("estimate_amount", String.valueOf(total));
        urlBuilder.addQueryParameter("estimate_km", String.valueOf(rideDistance.replace("km", "").trim()));
        urlBuilder.addQueryParameter("estimate_time", String.valueOf(timeInMinutes));
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
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
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
                        android.util.Log.e("Vehicle Image", "BookYourFragment" + vehicle_type_data.getString("plotting_icon"));
                        Preferences.setValue(getContext(), Preferences.VEHICLES_IMG, vehicle_type_data.getString("plotting_icon"));


                        JSONObject estimation = l_data.getJSONObject("estimation");
                        String vehicle_type = l_data.getString("vehicle_type");
                        String estimate_amount = estimation.getString("final_total");
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
                            String strNumber = jsonObject.getString("v_pin");


//                            String[] textArray = splitStringEvery(strNumber, 4);


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
        });
    }


    public String[] splitStringEvery(String s, int interval) {
        int arrayLength = (int) Math.ceil(((s.length() / (double) interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = s.substring(j, j + interval);
            j += interval;
        } //Add the last bit
        result[lastIndex] = s.substring(j);
        return result;
    }

    private void getDriverLocationAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_DRIVER_LOCATIOIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("i_driver_id", Preferences.getValue_String(getActivity(), Preferences.DRIVER_ID));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        mMap.clear();
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        final LatLng driver = new LatLng(driverLat, driverLong);
                        final LatLng customer = new LatLng(gpsLat, gpsLong);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(vehicleTypes.get(posVehicleTypes).getPlotting_icon());
                                    final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                    final Bitmap newBitmap = getResizedBitmap(bmp, 70, 70);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            driverMarker = mMap.addMarker(new MarkerOptions()
                                                    .position(driver)
                                                    .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));

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

        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        if (driverMarker != null) {
                            driverMarker.remove();
                        }
                        driverLat = jsonObject.getDouble("l_latitude");
                        driverLong = jsonObject.getDouble("l_longitude");
                        final LatLng driver = new LatLng(driverLat, driverLong);
                        final LatLng customer = new LatLng(gpsLat, gpsLong);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (vehicleTypes.size() == 0) {
                                    } else {
                                        URL url = new URL(vehicleTypes.get(posVehicleTypes).getPlotting_icon());
                                        final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        final Bitmap newBitmap = getResizedBitmap(bmp, 70, 70);
                                        getActivity().runOnUiThread(new Runnable() { // java.lang.NullPointerException: Attempt to invoke virtual method 'void android.support.v4.app.FragmentActivity.runOnUiThread(java.lang.Runnable)' on a null object reference // solved
                                            @Override
                                            public void run() {
                                                driverMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(driver)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
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
                        thread.start();  // Error Here.
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
        VolleyRequestClass.allRequest(mContext, newurl, new RequestInterface() {
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
        }, true);

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
        VolleyRequestClass.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        dialog.hide();
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        dialog.hide();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.hide();
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
//      urlBuilder.addQueryParameter("city", cityCurrent);
        urlBuilder.addQueryParameter("v_code", et_add_promocode.getText().toString());
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getActivity(), Preferences.RIDE_ID));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject data = response.getJSONObject("data");
                        promocode_code = data.getString("promocode_code");
                        tv_enter_promocode.setVisibility(View.INVISIBLE);
                        mCanclePromocode.setVisibility(View.VISIBLE);

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


    private void updateVehicleList() {
        if (h2 != null) {
            h2.removeCallbacksAndMessages(null);
        }
        h1.postDelayed(new Runnable() {
            public void run() {
                if (Constant.isOnline(getContext())) {
                    getVehiclesListAPIThread(vehicleTypes.get(posVehicleTypes).getType());     // this method will contain your almost-finished HTTP calls
                }
                h1.postDelayed(this, 6000);
            }
        }, 6000);

    }

    private Handler h1 = new Handler();
    private Handler h2 = new Handler();
    private Handler h3 = new Handler();

    private void updateVehicleListLongTime() {
        if (h1 != null) {
            h1.removeCallbacksAndMessages(null);
        }
        h2.postDelayed(new Runnable() {
            public void run() {
                if (Constant.isOnline(getContext())) {
                    getVehiclesListAPIThread(vehicleTypes.get(posVehicleTypes).getType());
                }
                h2.postDelayed(this, 30000); //now is every 2 minutes
            }
        }, 30000);
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
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                String value = String.valueOf(success);
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
                        }

                        try {
                            if (Constant.isOnline(getContext())) {
                                getVehiclesListAPI(vehicleTypes.get(posVehicleTypes).getType());
                                getVehicleTypeCharge(vehicleTypes.get(0).getType());
                            }
                        } catch (Exception e) {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

   /**//* private void countAmount() {
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

    }*/


    private void getVehiclesListAPI(String vehicleType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_VEHICLE_LIST).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_type", vehicleType);
        urlBuilder.addQueryParameter("l_latitude", String.valueOf(gpsLat));
        urlBuilder.addQueryParameter("l_longitude", String.valueOf(gpsLong));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String message = response.optString("message").toString();
                vehicleStatus = response.optInt("status");
                android.util.Log.e("Hector", "onResult: vehicaleListAPI " + vehicleStatus);
                if (vehicleStatus == 0) {
                    updateVehicleList();
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        updateVehicleListLongTime();
                        JSONArray data = response.getJSONArray("data");
                        if (vehicleMarker != null) {
                            vehicleMarker.remove();
                        }
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            String lati = objData.getString("l_latitude");
                            String longi = objData.getString("l_longitude");
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
                                        android.util.Log.e("Vehicle Icon", "run: " + url);
                                        final Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                        final Bitmap newBitmap = getResizedBitmap(bmp, 70, 70);
                                        if (getActivity() == null)
                                            return;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                /*vehicleMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(point)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(bmp)));*/

                                                /*Hector Change*/
                                                vehicleMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(point)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
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
        });
    }

    /*Hector Image Bitmap Change*/
    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }


    private void getVehiclesListAPIThread(String vehicleType) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_VEHICLE_LIST).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_type", vehicleType);
        urlBuilder.addQueryParameter("l_latitude", String.valueOf(gpsLat));
        urlBuilder.addQueryParameter("l_longitude", String.valueOf(gpsLong));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {

                final String message = response.optString("message").toString();
                vehicleStatus = response.optInt("status");
                android.util.Log.e("Hector", "onResult: vehicleListAPIThread " + vehicleStatus);
                if (vehicleStatus == 0) {
                    android.util.Log.e("Hector", "onResult: Vehicle Status is 0");
                } else {
                    try {
                        JSONArray data = response.getJSONArray("data");
                        if (vehicleMarker != null) {
                            vehicleMarker.remove();
                        }
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            String lati = objData.getString("l_latitude");
                            String longi = objData.getString("l_longitude");
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

                                        /*Hector image bitmap change*/
                                        final Bitmap newBitmap = getResizedBitmap(bmp, 70, 70);
                                        if (getActivity() == null)
                                            return;
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                vehicleMarker = mMap.addMarker(new MarkerOptions()
                                                        .position(point)
                                                        .icon(BitmapDescriptorFactory.fromBitmap(newBitmap)));
                                                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
                                                cameraPosition = new CameraPosition.Builder()
                                                        .target(point)
                                                        .bearing(20)
                                                        .zoom(12)
                                                        .build();
                                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        });
    }


    private void downloadUrl() {
        String url1 = getUrl(origin, dest);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url1).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(mContext, newurl, new RequestInterface() {
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

                    //which is from server;

                    if (rideTime.contains("hours")) {
                        String time = rideTime;
                        String splitTime[] = time.split(" hours ");
                        String hours = splitTime[0];
                        String minutes = splitTime[1];
                        if (rideTime.contains("mins")) {
                            Integer mins = Integer.parseInt(minutes.replaceAll("mins", "").trim());
                            mtime = Integer.parseInt(hours) * 60;
                            timeInMinutes = (mtime + mins);
                        } else {
                            Integer mins = Integer.parseInt(minutes.replaceAll("min", "").trim());
                            mtime = Integer.parseInt(hours) * 60;
                            timeInMinutes = (mtime + mins);
                        }

                    } else if (rideTime.contains("hour")) {
                        String time = rideTime;
                        String splitTime[] = time.split(" hour ");
                        String hours = splitTime[0];
                        String minutes = splitTime[1];
                        if (rideTime.contains("mins")) {
                            Integer mins = Integer.parseInt(minutes.replaceAll("mins", "").trim());
                            mtime = Integer.parseInt(hours) * 60;
                            timeInMinutes = (mtime + mins);
                        } else {

                        }

                    } else {
                        if (rideTime.contains("mins")) {
                            Integer mins = Integer.parseInt(rideTime.replaceAll("mins", "").trim());
                            timeInMinutes = mins;
                        } else {

                        }

                    }

//                    String splitTime[]=time.split(" hours ");
//                    String hours=splitTime[0];
//                    String minutes=splitTime[1];
//
//                    Log.i("#########" + "hours", hours);

//                    if (Integer.parseInt(showEstimationCharge) == 1) {
//                        Log.d("#####", "estimation : " + showEstimationCharge);
//                        countAmount();
//                    } else {
//                        Log.d("#####", "estimation : " + "00:00");
//                    }
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
        });
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
                            if (vehicleTypes.get(position).getType().length() > 0)
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
                            if (vehicleTypes.get(position).getType().length() > 0)
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
        tv_enter_promocode = (LinearLayout) view.findViewById(R.id.ll_promocode);
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
        mCanclePromocode = (TextView) view.findViewById(R.id.txt_cancle_promocod);


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
        locationButton.setOnClickListener(this);


        MarkerPoints = new ArrayList<>();
        vehicleTypes = new ArrayList<RecyclerBookRideModel>();
        vhicleCharges = new ArrayList<ChargesModel>();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myhandler != null) {
            myhandler.removeCallbacksAndMessages(null);
        }
        if (h1 != null) {
            h1.removeCallbacksAndMessages(null);
        }
        if (h2 != null) {
            h2.removeCallbacksAndMessages(null);
        }
        if (h3 != null) {
            h3.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}





