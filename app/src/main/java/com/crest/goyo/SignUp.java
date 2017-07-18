package com.crest.goyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.ModelClasses.CityModel;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.CustomDialog;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title;
    private Button bt_submit;
    private EditText et_full_name, et_email, et_mo_no, et_pasword, et_confirm_password, et_referral_code;
    private CustomDialog customDialog;
    private RadioGroup mGenderGrup;
    private RadioButton mGender;
    private Spinner spinner_city_list;
    private ArrayAdapter<String> cityListAdapter;
    private List<CityModel> cityList = new ArrayList<>();
    final static int REQUEST_INTERNET = 100;
    String imei;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        /*hector*/
        if (!isNetworkAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp.this);
            builder.setTitle("Internet Permission");
            builder.setMessage("Internet Permission Needed.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }

        initUI();

        cityListAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_item);
        cityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city_list.setAdapter(cityListAdapter);

        getCitiesAPI();
    }

    private void initUI() {

        bt_submit = (Button) findViewById(R.id.bt_submit);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        et_full_name = (EditText) findViewById(R.id.et_full_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mo_no = (EditText) findViewById(R.id.et_mo_no);
        et_pasword = (EditText) findViewById(R.id.et_pasword);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        mGenderGrup = (RadioGroup) findViewById(R.id.g1);
        spinner_city_list = (Spinner) findViewById(R.id.spinner_city_list);
        et_referral_code = (EditText) findViewById(R.id.et_referral_code);

        bt_submit.setOnClickListener(this);
        actionbar_title.setText(R.string.actionbar_signup);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:

                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                imei = telephonyManager.getDeviceId();
                Log.e("IMEI", "onClick: " + imei);
                userSignup();

                break;
        }
    }

    /*hector*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void userSignup() {

        if (et_full_name.getText().toString().equals("")) {
            et_full_name.setError("Please enter full name.");
        } else {
            if (et_email.getText().toString().trim().equals("")) {
                et_email.setError("Please enter email.");
            } else {
                /*if (et_email.getText().toString().trim().matches(Constant.emailPattern)) {*/
                if (isValidEmail(et_email.getText().toString().trim())) {

                    if (et_mo_no.getText().toString().length() == 10) {
                        if (et_pasword.getText().toString().length() >= 6) {
                            if (et_pasword.getText().toString().matches(et_confirm_password.getText().toString())) {
                                if (Constant.isOnline(getApplicationContext())) {
                                    int selectedId = mGenderGrup.getCheckedRadioButtonId();
                                    mGender = (RadioButton) findViewById(selectedId);
                                    String gender = mGender.getText().toString();
                                    if (Constant.isOnline(SignUp.this)) {
                                        userSignupAPI(gender);
                                    }
                                }
                            } else {
                                et_confirm_password.setError("Please enter same password");
                            }
                        } else {
                            et_pasword.setError("Password must be six to ten charachets.");
                        }
                    } else {
                        et_mo_no.setError("Please enter 10 digit mobile no.");
                    }
                } else {
                    et_email.setError("Please enter valid email.");
                }
            }
        }
    }

    private void getCitiesAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_CITIES).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(SignUp.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONArray data = response.getJSONArray("data");
                        Log.e("TAG", "City length = " + data.length());
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            CityModel cityModel = new CityModel(objData.getString("id"), objData.getString("v_name"));
                            cityList.add(cityModel);
                            cityListAdapter.add(cityModel.getName());
                        }
                        cityListAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void userSignupAPI(String gender) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_SIGNUP).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_name", et_full_name.getText().toString());
        urlBuilder.addQueryParameter("v_gender", gender);
        urlBuilder.addQueryParameter("v_email", et_email.getText().toString().trim());
        urlBuilder.addQueryParameter("v_phone", et_mo_no.getText().toString().trim());
        urlBuilder.addQueryParameter("v_password", et_pasword.getText().toString());
        urlBuilder.addQueryParameter("refferal_code", et_referral_code.getText().toString());
        urlBuilder.addQueryParameter("v_device_token", FirebaseInstanceId.getInstance().getToken());
        urlBuilder.addQueryParameter("i_city_id", cityList.get(spinner_city_list.getSelectedItemPosition()).getId());
        urlBuilder.addQueryParameter("v_imei_number", imei);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(SignUp.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
                        JSONObject data = response.getJSONObject("data");
                        Intent intent = new Intent(getApplicationContext(), VerifyAccountActivity.class);
                        intent.putExtra("id", data.getString("id"));
                        intent.putExtra("phone", data.getString("v_phone"));
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }
    public static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
}
