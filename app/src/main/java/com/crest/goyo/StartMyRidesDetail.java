package com.crest.goyo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class StartMyRidesDetail extends AppCompatActivity {
    private TextView actionbar_title, tv_time, tv_pickup_from, tv_drop_loc, tv_track;
    private String rideID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_my_rides_detail);

        initUI();

        if (getIntent().getExtras() != null) {
            rideID = getIntent().getStringExtra("rideID");
            if(Constant.isOnline(StartMyRidesDetail.this))
            {
                getRideAPI();
            }
        }


        tv_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StartRideActivity.class);
                intent.putExtra("i_ride_id", rideID);
                intent.putExtra("comeFrom", "startRideDetail");
                finish();
                startActivity(intent);
            }
        });

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
                        tv_pickup_from.setText(l_data.getString("pickup_address"));
                        tv_drop_loc.setText(l_data.getString("destination_address"));

                        /*Hector*/
                        JSONObject vehicle_type_data = jsonObject.getJSONObject("vehicle_type_data");
                        Preferences.setValue(getApplicationContext(), Preferences.VEHICLES_IMG, vehicle_type_data.getString("plotting_icon"));

                        try {
                            String date = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong((jsonObject.getString("d_time"))), DateUtils.FORMAT_SHOW_DATE);
                            String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_time")), DateUtils.FORMAT_SHOW_TIME);
                            tv_time.setText(date + " AT " + time);
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

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_pickup_from = (TextView) findViewById(R.id.tv_pickup_from);
        tv_drop_loc = (TextView) findViewById(R.id.tv_drop_loc);
        tv_track = (TextView) findViewById(R.id.tv_track);

        actionbar_title.setText(R.string.nav_my_rides);
    }
}
