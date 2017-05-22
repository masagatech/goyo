package com.crest.goyo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.CustomDialog;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.ServiceHandler;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title;
    private Button bt_submit;
    private EditText et_full_name, et_email, et_mo_no, et_pasword, et_confirm_password;
    private CustomDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        initUI();
    }

    private void initUI() {

        bt_submit = (Button) findViewById(R.id.bt_submit);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        et_full_name = (EditText) findViewById(R.id.et_full_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mo_no = (EditText) findViewById(R.id.et_mo_no);
        et_pasword = (EditText) findViewById(R.id.et_pasword);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);

        bt_submit.setOnClickListener(this);

        actionbar_title.setText(R.string.actionbar_signup);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:

                userSignup();

                break;
        }
    }

    private void userSignup() {

        if (et_full_name.getText().toString().equals("")) {
            et_full_name.setError("Please enter full name.");
        } else {
            if (!et_email.getText().toString().equals("")) {
                if (et_email.getText().toString().matches(Constant.emailPattern)) {
                    if (et_mo_no.getText().toString().length() == 10) {
                        if (et_pasword.getText().toString().length() >= 6) {
                            if (et_pasword.getText().toString().matches(et_confirm_password.getText().toString())) {
                                if (Constant.isOnline(getApplicationContext())) {
                                    userSignupAPI();
//                                userSignUp();
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
            } else {
                et_email.setError("Please enter email.");
            }

        }
    }

    private void userSignUp() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_SIGNUP).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_name", et_full_name.getText().toString());
        urlBuilder.addQueryParameter("v_email", "" + et_email.getText().toString());
        urlBuilder.addQueryParameter("v_phone", et_mo_no.getText().toString());
        urlBuilder.addQueryParameter("v_password", et_pasword.getText().toString());
        urlBuilder.addQueryParameter("v_device_token", FirebaseInstanceId.getInstance().getToken());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        Request request = new Request.Builder()
                .url(newurl)
                .build();
        Log.e("#########", "device :" + url);

        new getSignUp().execute(newurl);
    }

    private void userSignupAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_SIGNUP).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_name", et_full_name.getText().toString());
        urlBuilder.addQueryParameter("v_email", et_email.getText().toString().trim());
        urlBuilder.addQueryParameter("v_phone", et_mo_no.getText().toString().trim());
        urlBuilder.addQueryParameter("v_password", et_pasword.getText().toString());
        urlBuilder.addQueryParameter("v_device_token", FirebaseInstanceId.getInstance().getToken());
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

                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private class getSignUp extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            customDialog = new CustomDialog(SignUp.this);
            customDialog.show();

        }

        @Override
        protected Void doInBackground(String... url) {

            ServiceHandler sh = new ServiceHandler();
            String jsonStr = sh.makeServiceCall(url[0], ServiceHandler.GET);
            Log.e("sign in", "    " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Log.e("json obj ", "    " + jsonObj);
                    final String success = jsonObj.optString("status").toString();
                    Log.e("success", "    " + success);
                    final String message = jsonObj.optString("message").toString();
                    String value = String.valueOf(success);
                    Log.e("value", "    " + value);
                    if (value.equals("0")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                startActivity(intent);
                            }
                        });

                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            customDialog.hide();
        }
    }

//    private void userSignupAPI() {
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_SIGNUP).newBuilder();
//        urlBuilder.addQueryParameter("device", "ANDROID");
//        urlBuilder.addQueryParameter("v_name", et_full_name.getText().toString());
//        urlBuilder.addQueryParameter("v_email", et_email.getText().toString());
//        urlBuilder.addQueryParameter("v_phone", et_mo_no.getText().toString());
//        urlBuilder.addQueryParameter("v_password", et_pasword.getText().toString());
//        urlBuilder.addQueryParameter("v_device_token", FirebaseInstanceId.getInstance().getToken());
//        String url = urlBuilder.build().toString();
//        String newurl = url.replaceAll(" ", "%20");
//
//        VolleyRequestClass.allRequest(SignUp.this, newurl, new RequestInterface() {
//            @Override
//            public void onResult(JSONObject response) {
//                Log.d("#####","request : "+response);
//                final String success = response.optString("status").toString();
//                Log.e("success", "    " + success);
//                final String message = response.optString("message").toString();
//                String value = String.valueOf(success);
//                Log.e("value", "    " + value);
//                if (value.equals("0")) {
//                    Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(SignUp.this, message, Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(getApplicationContext(), Login.class);
//                    startActivity(intent);
//                }
//
//            }
//        }, true);
//    }


}
