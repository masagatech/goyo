package com.crest.goyo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class CancelMyRidesDetail extends AppCompatActivity {
    private TextView actionbar_title, tv_time, tv_pickup_from, tv_drop_loc, tv_reason, label_reason;
    private TextView rating_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_my_rides_detail);

        initUI();



        if(getIntent().getExtras()!=null){
            tv_pickup_from.setText(getIntent().getStringExtra("pickupAdd"));
            tv_drop_loc.setText(getIntent().getStringExtra("dropAdd"));
            String date =  DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("rideTime")), DateUtils.FORMAT_SHOW_DATE);
            String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("rideTime")), DateUtils.FORMAT_SHOW_TIME);
            tv_time.setText(date+ " AT "+time);
        }else {

        }
//        if (Constant.isOnline(getApplicationContext())) {
//            getRideDetail();
//        }
    }

    private void getRideDetail() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_RIDE_DETAIL).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", "");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(CancelMyRidesDetail.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        tv_pickup_from.setText(jsonObject.getJSONObject("l_data").getString("pickup_address"));
                        tv_drop_loc.setText(jsonObject.getJSONObject("l_data").getString("destination_address"));
                        String date =  DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_time")), DateUtils.FORMAT_SHOW_DATE);
                        String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(jsonObject.getString("d_time")), DateUtils.FORMAT_SHOW_TIME);
                        tv_time.setText(date+ " AT "+time);
                        if (jsonObject.getString("ride_l_comment").equals("")) {
                            label_reason.setVisibility(View.GONE);
                            tv_reason.setVisibility(View.GONE);
                        } else {
                            tv_reason.setText(jsonObject.getString("ride_l_comment"));
                        }
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);

    }

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_pickup_from = (TextView) findViewById(R.id.tv_pickup_from);
        tv_drop_loc = (TextView) findViewById(R.id.tv_drop_loc);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        label_reason = (TextView) findViewById(R.id.label_reason);
        actionbar_title.setText(R.string.nav_my_rides);
    }
}
