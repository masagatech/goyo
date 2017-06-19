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

public class ResetPassword extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title;
    private EditText et_auth, et_new_password, et_retry_password;
    private Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_reset_password);

        initUI();
    }

    private void initUI() {
        bt_submit = (Button) findViewById(R.id.bt_submit);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        et_auth = (EditText) findViewById(R.id.et_auth);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_retry_password = (EditText) findViewById(R.id.et_retry_password);

//        et_auth.setText(Preferences.getValue_String(getApplicationContext(), Preferences.FORGOT_PASSWORD_OTP));
        bt_submit.setOnClickListener(this);

        actionbar_title.setText(R.string.actionbar_reset_password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_submit:
                userResetPassword();
//                Intent intent = new Intent(getApplicationContext(), Login.class);
//                startActivity(intent);

                break;
        }
    }

    private void userResetPassword() {

        if (et_auth.getText().toString().equals("")) {
            et_auth.setError("Please enter authentication code.");
        } else {
            if (et_new_password.getText().toString().length() >= 6) {
                if (et_new_password.getText().toString().equals(et_retry_password.getText().toString())) {
                    userResetPasswordAPI();
                } else {
                    et_retry_password.setError("Please enter same password.");
                }
            } else {
                et_new_password.setError("Password must be six to ten characters.");
            }
        }
    }


    private void userResetPasswordAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_RESET_PASSWORD).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("v_username", Preferences.getValue_String(getApplicationContext(), Preferences.FORGOT_PASSWORD_EMAIL));
        urlBuilder.addQueryParameter("v_password", et_retry_password.getText().toString());
        urlBuilder.addQueryParameter("v_otp", et_auth.getText().toString());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(ResetPassword.this, newurl, new RequestInterface() {
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
                    Toast.makeText(ResetPassword.this, message, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ResetPassword.this, message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);

                }

            }
        }, true);

    }


}
