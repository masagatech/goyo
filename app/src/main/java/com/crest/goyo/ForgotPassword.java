package com.crest.goyo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title;
    private EditText et_email;
    private Button bt_submit;
    String OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);

        initUI();
    }

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        et_email = (EditText) findViewById(R.id.et_email);
        bt_submit.setOnClickListener(this);

        actionbar_title.setText(R.string.actionbar_password_recovery);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
//                Intent intent=new Intent(getApplicationContext(),ResetPassword.class);
//                startActivity(intent);
                userForgotPassword();
                break;
        }
    }

    private void userForgotPassword() {

        if (et_email.getText().toString().equals("")) {
            et_email.setError("Please enter email or phone number.");
        } else {
            if (Constant.isOnline(getApplicationContext())) {
                userForgotPasswordAPI();
            }
        }
    }

    private void userForgotPasswordAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_FORGOT_PASSWORD).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_username", et_email.getText().toString());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(ForgotPassword.this, newurl, new RequestInterface() {
            @Override
            public void onResult(final JSONObject response) {

                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                JSONObject data = null;
                try {
                    data = response.getJSONObject("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String value = String.valueOf(success);
                if (value.equals("0")) {
                    Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(ForgotPassword.this, message, Toast.LENGTH_LONG).show();
                    try {
                        OTP = data.getString("v_otp");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), ResetPassword.class);
                    Preferences.setValue(getApplicationContext(),Preferences.FORGOT_PASSWORD_OTP,""+OTP);
                    Preferences.setValue(getApplicationContext(),Preferences.FORGOT_PASSWORD_EMAIL,""+et_email.getText().toString());
                    startActivity(intent);

                }

            }
        }, true);
    }


}
