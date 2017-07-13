package com.crest.goyo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class CompleteMyRidesDetail extends AppCompatActivity {
    private TextView title_comment, actionbar_title, tv_comment, tv_total_fare, tv_total_distance, tv_time, tv_pickup_from, tv_drop_loc, tv_start_time, tv_end_time, tv_total_duration;
    private RatingBar rating_bar;
    private String rideID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_my_rides_detail);

        initUI();


        if (getIntent().getExtras() != null) {
            rideID = getIntent().getStringExtra("rideID");
            if(Constant.isOnline(CompleteMyRidesDetail.this))
            {
                getRideAPI();
            }
        }
//
//        if(getIntent().getExtras()!=null){
//            tv_pickup_from.setText(getIntent().getStringExtra("pickupAdd"));
//            tv_drop_loc.setText(getIntent().getStringExtra("dropAdd"));
//            tv_total_fare.setText("\u20B9" + " " + getIntent().getStringExtra("finalAmount"));
//            tv_total_distance.setText(getIntent().getStringExtra("finalDistance")+ " km");
//            tv_total_duration.setText(getIntent().getStringExtra("tripDuration")+" min");
//            try {
//                String date = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong((getIntent().getStringExtra("rideTime"))), DateUtils.FORMAT_SHOW_DATE);
//                String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("rideTime")), DateUtils.FORMAT_SHOW_TIME);
//                String startDate = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("startTime")), DateUtils.FORMAT_SHOW_DATE);
//                String startTime = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("startTime")), DateUtils.FORMAT_SHOW_TIME);
//                String endDate = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("endTime")), DateUtils.FORMAT_SHOW_DATE);
//                String endTime = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("endTime")), DateUtils.FORMAT_SHOW_TIME);
//                Log.d("######","time : "+date + " AT " + time);
//                tv_time.setText(date + " AT " + time);
//                tv_start_time.setText(startDate + " AT " + startTime);
//                tv_end_time.setText(endDate + " AT " + endTime);
//
////                if (jsonObject.getString("ride_l_comment").equals("")) {
////                    tv_comment.setVisibility(View.GONE);
////                    title_comment.setVisibility(View.GONE);
////                } else {
////                    tv_comment.setText(jsonObject.getString("ride_l_comment"));
////                }
//            } catch (Exception e) {
//                Log.d("######","Exception : "+e);
//            }
//        }else {
//
//        }

        if (Constant.isOnline(getApplicationContext())) {
//            getRideDetail();
            getRideAPI();
        }
    }

    private void getRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", rideID);
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
                        JSONObject l_data = jsonObject.getJSONObject("l_data");
                        JSONObject driver_rate= jsonObject.getJSONObject("driver_rate");

                          /*hector*/
                        JSONObject vehicle_type_data = jsonObject.getJSONObject("vehicle_type_data");
                        Preferences.setValue(getApplicationContext(), Preferences.VEHICLES_IMG, vehicle_type_data.getString("plotting_icon"));


                        JSONObject rate = jsonObject.getJSONObject("rate");
                        tv_pickup_from.setText(l_data.getString("pickup_address"));
                        tv_drop_loc.setText(l_data.getString("destination_address"));
                        tv_total_fare.setText("\u20B9" + " " + l_data.getString("final_amount"));
                        tv_total_distance.setText(l_data.getString("actual_distance") + " km");

                        rating_bar.setRating(Float.parseFloat(driver_rate.getString("i_rate")));

                        tv_total_duration.setText(l_data.getString("trip_time_in_min") + " min");
                        if (rate.getString("rate_cmment").equals("")) {
                            tv_comment.setVisibility(View.GONE);
                            title_comment.setVisibility(View.GONE);
                        } else {
                            tv_comment.setText(rate.getString("rate_cmment"));
                        }
                        try {
                            String date = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong((jsonObject.getString("d_time"))), DateUtils.FORMAT_SHOW_DATE);
                            String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_time")), DateUtils.FORMAT_SHOW_TIME);
                            String startDate = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_start")), DateUtils.FORMAT_SHOW_DATE);
                            String startTime = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_start")), DateUtils.FORMAT_SHOW_TIME);
                            String endDate = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_end")), DateUtils.FORMAT_SHOW_DATE);
                            String endTime = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_end")), DateUtils.FORMAT_SHOW_TIME);
                            tv_time.setText(date + " AT " + time);
                            tv_start_time.setText(startDate + " AT " + startTime);
                            tv_end_time.setText(endDate + " AT " + endTime);

                        } catch (Exception e) {

                        }
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    private void getRideDetail() {
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_RIDE_DETAIL).newBuilder();
//        urlBuilder.addQueryParameter("device", "ANDROID");
//        urlBuilder.addQueryParameter("lang", "en");
//        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
//        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
//        urlBuilder.addQueryParameter("i_ride_id", "");
//        String url = urlBuilder.build().toString();
//        String newurl = url.replaceAll(" ", "%20");
//        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
//        VolleyRequestClass.allRequest(CompleteMyRidesDetail.this, newurl, new RequestInterface() {
//            @Override
//            public void onResult(JSONObject response) {
//                try {
//                    int responce_status = response.getInt(VolleyTAG.status);
//                    String message = response.getString(VolleyTAG.message);
//                    if (responce_status == VolleyTAG.response_status) {
//                        JSONObject jsonObject = response.getJSONObject("data");
//                        tv_pickup_from.setText(jsonObject.getJSONObject("l_data").getString("pickup_address"));
//                        tv_drop_loc.setText(jsonObject.getJSONObject("l_data").getString("destination_address"));
//                        tv_total_fare.setText("\u20B9" + " " + jsonObject.getJSONObject("l_data").getString("final_amount"));
//                        tv_total_distance.setText(jsonObject.getJSONObject("l_data").getString("estimate_km"));
//                        tv_total_duration.setText(jsonObject.getJSONObject("l_data").getString("estimate_time"));
//
//
//                        rating_bar.setRating(Float.parseFloat(jsonObject.getString("ride_i_rate")));
//                        try {
//                            String date = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_time")), DateUtils.FORMAT_SHOW_DATE);
//                            String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_time")), DateUtils.FORMAT_SHOW_TIME);
//                            String startDate = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_start")), DateUtils.FORMAT_SHOW_DATE);
//                            String startTime = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_start")), DateUtils.FORMAT_SHOW_TIME);
//                            String endDate = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_end")), DateUtils.FORMAT_SHOW_DATE);
//                            String endTime = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_end")), DateUtils.FORMAT_SHOW_TIME);
//                            tv_time.setText(date + " AT " + time);
//                            tv_start_time.setText(startDate + " AT " + startTime);
//                            tv_end_time.setText(endDate + " AT " + endTime);
//
//                            if (jsonObject.getString("ride_l_comment").equals("")) {
//                                tv_comment.setVisibility(View.GONE);
//                                title_comment.setVisibility(View.GONE);
//                            } else {
//                                tv_comment.setText(jsonObject.getString("ride_l_comment"));
//                            }
//                        } catch (Exception e) {
//
//                        }
//
//
//                    } else {
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, true);
//
//    }

    private void initUI() {

        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_comment = (TextView) findViewById(R.id.tv_comment);
        tv_total_fare = (TextView) findViewById(R.id.tv_total_fare);
        tv_total_distance = (TextView) findViewById(R.id.tv_total_distance);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_pickup_from = (TextView) findViewById(R.id.tv_pickup_from);
        tv_drop_loc = (TextView) findViewById(R.id.tv_drop_loc);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        tv_total_duration = (TextView) findViewById(R.id.tv_total_duration);
        title_comment = (TextView) findViewById(R.id.title_comment);
        rating_bar = (RatingBar) findViewById(R.id.rating_bar);
        actionbar_title.setText(R.string.nav_my_rides);
    }
}
