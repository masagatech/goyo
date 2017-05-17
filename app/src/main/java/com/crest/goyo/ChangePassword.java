package com.crest.goyo;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

/**
 * Created by brittany on 3/23/17.
 */

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title;
    private EditText et_old_password, et_new_password, et_confirm_password;
    private Button bt_save;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        initUI();


    }

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        et_old_password = (EditText) findViewById(R.id.et_old_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_confirm_password = (EditText) findViewById(R.id.et_confirm_password);
        bt_save = (Button) findViewById(R.id.bt_save);


        actionbar_title.setText(R.string.actionbar_change_password);

        bt_save.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_save:
                changePasswordValidation();
                break;
        }

    }

    private void changePasswordValidation() {

        if (et_old_password.getText().toString().equals("")) {
            et_old_password.setError("Please enter old password.");
        } else {
            if(et_old_password.getText().toString().equals(et_new_password.getText().toString())){
                et_new_password.setError("Old password and New password are same.");
            }else {
                if (et_new_password.getText().toString().length() >= 6) {
                    if (et_confirm_password.getText().toString().matches(et_new_password.getText().toString())) {
                        if (Constant.isOnline(getApplicationContext())) {
                            changePasswordAPI();

                        }
                    } else {
                        et_confirm_password.setError("Please enter same password.");
                    }
                } else {
                    et_new_password.setError("Password must be six to ten charachets.");
                }
            }

        }
    }

    private void changePasswordAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_CHANGE_PASSWORD).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("v_password", et_confirm_password.getText().toString());
        urlBuilder.addQueryParameter("v_old_password", et_old_password.getText().toString());
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(ChangePassword.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        Toast.makeText(ChangePassword.this, message, Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(ChangePassword.this, message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }
}
