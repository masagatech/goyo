package com.crest.goyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.FCM.MyFirebaseInstanceIDService;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.CustomDialog;
import com.crest.goyo.Utils.GPSTracker;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title, tv_forgot_password;
    private Button bt_login, bt_signup;
    private EditText et_password, et_email;
    private CustomDialog dialog;
    GPSTracker gps;
    double latitude, longitude;
    int REQUEST_INTERNET = 100;
    String imei;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        /*hector*/
        if (!isNetworkAvailable()) {
            //Show Information about why you need the permission
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setTitle("Internet Permission");
            builder.setMessage("Internet Permission Needed.");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    /*startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));*/

                    dialog.cancel();
                    startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
                    finish();
                }
            });
            builder.show();
        }

        initUI();


        if (ActivityCompat.checkSelfPermission(Login.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
                Log.e("TAG", "checkSelfPermission in");
            }

            Log.e("TAG", "checkSelfPermission out");
            //And finally ask for the permission
            ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.READ_PHONE_STATE}, 05);

            return;
        }
    }

    private void initUI() {
        Constant.CHECK_GPS = true;
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_forgot_password = (TextView) findViewById(R.id.tv_forgot_password);
        bt_login = (Button) findViewById(R.id.btn_login);
        bt_signup = (Button) findViewById(R.id.bt_signup);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);

        actionbar_title.setText(R.string.actionbar_login);
//        et_email.setText("amiee@gmail.com");
//        et_password.setText("111111");
//        et_email.setText("deven.crestinfotech@gmail.com");
//        et_email.setText("annietate.cis@gmail.com");
//        et_password.setText("12345678");

        tv_forgot_password.setOnClickListener(this);
        bt_signup.setOnClickListener(this);
        bt_login.setOnClickListener(this);
    }

    /*hector*/
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgot_password:
                Intent forgot_password = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(forgot_password);

                break;
            case R.id.bt_signup:
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivity(intent);

                break;
            case R.id.btn_login:

                Log.e("TAG", "checkSelfPermission");
                if (ActivityCompat.checkSelfPermission(Login.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        //If the user has denied the permission previously your code will come to this block
                        //Here you can explain why you need this permission
                        //Explain here why you need this permission
                        Log.e("TAG", "checkSelfPermission in");
                    }

                    Log.e("TAG", "checkSelfPermission out");
                    //And finally ask for the permission
                    ActivityCompat.requestPermissions(Login.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 05);

                    return;
                }

                gps = new GPSTracker(v.getContext(), Login.this);
                LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                if (gps.canGetLocation()) {

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    Log.d("######", "lat: " + latitude + "long :" + longitude);

                    startService(new Intent(Login.this, MyFirebaseInstanceIDService.class));
                    userLogin();
                } else {
                    gps.showSettingsAlert();
                }
                break;
        }
    }

    private void userLogin() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();
        Log.e("IMEI", "onClick: "+imei );
        if (et_email.getText().toString().equals("")) {
            et_email.setError("Please enter email or mobie no.");
        } else {
            if (et_password.getText().toString().equals("")) {
                et_password.setError("Please enter password.");
            } else {
                if (Constant.isOnline(getApplicationContext())) {
                    userLoginAPI();
                }
            }
        }
    }

    private void userLoginAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_LOGIN).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_username", et_email.getText().toString().trim());
        urlBuilder.addQueryParameter("v_password", et_password.getText().toString());
        urlBuilder.addQueryParameter("v_device_token", FirebaseInstanceId.getInstance().getToken());
        urlBuilder.addQueryParameter("v_imei_number", imei);
        Log.e("Firebase Device Token", "userLoginAPI: " + FirebaseInstanceId.getInstance().getToken());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(Login.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                        JSONObject jsonObject = response.getJSONObject("data");
                        Preferences.setValue(getApplicationContext(), Preferences.USER_ID, jsonObject.getString("id"));
                        Preferences.setValue(getApplicationContext(), Preferences.USER_AUTH_TOKEN, jsonObject.getString("v_token"));
                        Preferences.setValue(getApplicationContext(), Preferences.V_ID, jsonObject.getString("v_id"));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                    } else if (responce_status == 2) {
                        JSONObject data = response.getJSONObject("data");
                        Intent intent = new Intent(getApplicationContext(), VerifyAccountActivity.class);
                        intent.putExtra("id", data.getString("id"));
                        intent.putExtra("phone", data.getString("phone"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(Login.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}

