package com.crest.goyo;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class CompleteRide extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title, tv_start_point, end_point, tv_payable_amount;
    private Button bt_rate_ride;
    private float mAmount = 0;
    private AlertDialog.Builder builder;
    private String mRideid;
    private BroadcastReceiver mReceiveMessageFromNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_complete_ride);

        initUI();

        if(getIntent().getExtras()!=null){
         mRideid=getIntent().getExtras().getString("i_ride_id");
        }

        builder = new AlertDialog.Builder(CompleteRide.this, R.style.MyAlertDialogStyle);
        if (Constant.isOnline(getApplicationContext())) {
            completeRideAPI();
        }


    }

    private void completeRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_RIDE_PAYMENT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", mRideid);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(CompleteRide.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject data = response.getJSONObject("data");
                        JSONObject ride=data.getJSONObject("ride");
                        JSONObject l_data=ride.getJSONObject("l_data");
                        JSONArray pay_data = data.getJSONArray("payment_data");
                        for (int i = 0; i < pay_data.length(); i++) {
                            JSONObject objData = pay_data.getJSONObject(i);
                            float totalAmount = Float.parseFloat(objData.getString("f_amount"));
                            mAmount = mAmount + totalAmount;
                        }
                        tv_payable_amount.setText("\u20B9"+" " + mAmount);
                        tv_start_point.setText(l_data.getString("pickup_address"));
                        end_point.setText(l_data.getString("destination_address"));
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
        bt_rate_ride = (Button) findViewById(R.id.bt_rate_ride);
        tv_start_point = (TextView) findViewById(R.id.tv_start_point);
        end_point = (TextView) findViewById(R.id.end_point);
        tv_payable_amount = (TextView) findViewById(R.id.tv_payable_amount);

        bt_rate_ride.setOnClickListener(this);

        actionbar_title.setText(R.string.actionbar_complete_ride);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_rate_ride:
                Intent pIntent = new Intent(getApplicationContext(), RateThisRide.class);
                startActivity(pIntent);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
