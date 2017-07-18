/*
package com.crest.goyo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class ReferralCode extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title, tv_code, tv_earn_money;
    private Button bt_invite;
    String referral_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_referral_code);

        initUI();

        if (Constant.isOnline(getApplicationContext())) {
            getReferralCode();
        }
    }

    private void getReferralCode() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_REFERRAL_CODE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClass.allRequest(ReferralCode.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        tv_code.setText(jsonObject.getString("v_referral_code"));
                        referral_message = jsonObject.getString("referral_message");
                        tv_earn_money.setText(jsonObject.getString("message"));
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
        tv_code = (TextView) findViewById(R.id.tv_code);
        tv_earn_money = (TextView) findViewById(R.id.tv_earn_money);
        bt_invite = (Button) findViewById(R.id.bt_invite);
        actionbar_title.setText(R.string.actionbar_referralcode);
        bt_invite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_invite:
                */
/*https://play.google.com/store/apps/details?id=com.crest.goyo&hl=en*//*


                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, referral_message+"https://play.google.com/store/apps/details?id=com.crest.goyo&hl=en");
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }

    }
}
*/
