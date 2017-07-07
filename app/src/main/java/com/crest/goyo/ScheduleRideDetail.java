package com.crest.goyo;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.AdapterClasses.RideCancelAdapter;
import com.crest.goyo.ModelClasses.RideCancelModel;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.StringTokenizer;

import okhttp3.HttpUrl;

public class ScheduleRideDetail extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_ride_date,tv_ride_time,actionbar_title, tv_distance, tv_total, tv_vehicle_type, tv_saved_pickup_from, tv_saved_drop_location;
    private RecyclerView rv_cancel_reason;
    private EditText et_reason;
    private Button bt_cancel,bt_done;
    private RideCancelAdapter adapter;
    private LinearLayout lay_done,lay_cancel_schedule;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Calendar calendar;
    private List<RideCancelModel> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_schedule_ride_detail);

        initUI();

        calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);

        if(Constant.isOnline(ScheduleRideDetail.this))
        {
            getRideAPI();
        }

    }

    private void getRideAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_RIDE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getApplicationContext(), Preferences.RIDE_ID));
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
                        String vehicle_type = l_data.getString("vehicle_type");
//                        String estimate_amount = l_data.getString("estimate_amount");
                        String pickup_address = l_data.getString("pickup_address");
                        String destination_addres = l_data.getString("destination_address");
                        StringTokenizer tokens = new StringTokenizer(l_data.getString("ride_time"), " ");
//                        tv_total.setText("\u20B9" + " " + estimate_amount);
                        tv_vehicle_type.setText(vehicle_type);
                        tv_saved_pickup_from.setText(pickup_address);
                        tv_saved_drop_location.setText(destination_addres);
                        tv_distance.setText(l_data.getString("estimate_km"));
                        tv_ride_date.setText(tokens.nextToken());
                        tv_ride_time.setText(tokens.nextToken());
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initUI() {
        lay_done = (LinearLayout) findViewById(R.id.lay_done);
        lay_cancel_schedule=(LinearLayout)findViewById(R.id.lay_cancel_schedule);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_total = (TextView) findViewById(R.id.tv_total);
        tv_vehicle_type = (TextView) findViewById(R.id.tv_vehicle_type);
        tv_saved_pickup_from = (TextView) findViewById(R.id.tv_saved_pickup_from);
        tv_saved_drop_location = (TextView) findViewById(R.id.tv_saved_drop_location);
        tv_ride_time=(TextView)findViewById(R.id.tv_ride_time);
        tv_ride_date=(TextView)findViewById(R.id.tv_ride_date);
        actionbar_title.setText(R.string.actionbar_schedule_detail);

        lay_done.setOnClickListener(this);
        lay_cancel_schedule.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lay_done:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;

            case R.id.lay_cancel_schedule:
                showReqCancelDialog();
                break;

        }

    }

    private void showReqCancelDialog() {
        final Dialog dialog = new Dialog(ScheduleRideDetail.this);
        dialog.setContentView(R.layout.dialog_request_cancel_reason);

        rv_cancel_reason = (RecyclerView) dialog.findViewById(R.id.rv_cancel_reason);
        et_reason = (EditText) dialog.findViewById(R.id.et_reason);
        bt_cancel = (Button) dialog.findViewById(R.id.bt_cancel);
        bt_done = (Button) dialog.findViewById(R.id.bt_done);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_cancel_reason.setLayoutManager(layoutManager);
        adapter = new RideCancelAdapter(list);
        rv_cancel_reason.setAdapter(adapter);

        if (Constant.isOnline(getApplicationContext())) {
            getRideCancelReasonApi();
        }

        bt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.isOnline(getApplicationContext())) {
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

    private void userCancelScheduleRide(final Dialog dialog) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_RIDE_CANCEL).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_ride_id", Preferences.getValue_String(getApplicationContext(), Preferences.RIDE_ID));
        urlBuilder.addQueryParameter("cancel_reason_id",Preferences.getValue_String(getApplicationContext(),"cancel_id") );
        urlBuilder.addQueryParameter("cancel_reason_text", et_reason.getText().toString());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClassNew.allRequest(getApplicationContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
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
        urlBuilder.addQueryParameter("v_token",  Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("lang", "en");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getApplicationContext(), newurl, new RequestInterface() {
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
                            list.add(new RideCancelModel(jsonObject.getString("j_title"),jsonObject.getString("id")));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}
