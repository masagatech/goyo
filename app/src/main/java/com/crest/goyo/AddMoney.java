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

public class AddMoney extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title,tv_money,tv_amount;
    private Button bt_add_money,bt_500,bt_1000,bt_1500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_money);

        initUI();

        if (Constant.isOnline(getApplicationContext())) {
            getUserWalletAPI();
        }
    }
    private void getUserWalletAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_WALLET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(AddMoney.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        tv_amount.setText("\u20B9" + " " + jsonObject.getString("wallet_amount"));
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
        tv_money=(TextView)findViewById(R.id.tv_money);
        tv_amount=(TextView)findViewById(R.id.tv_amount);
        bt_add_money = (Button) findViewById(R.id.bt_add_money);
        bt_500=(Button)findViewById(R.id.bt_500);
        bt_1000=(Button)findViewById(R.id.bt_1000);
        bt_1500=(Button)findViewById(R.id.bt_1500);
        bt_add_money.setOnClickListener(this);
        bt_500.setOnClickListener(this);
        bt_1000.setOnClickListener(this);
        bt_1500.setOnClickListener(this);

        actionbar_title.setText(R.string.actionbar_add_money);
        tv_money.setText("1000");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_money:
                Intent intent = new Intent(getApplicationContext(), AddMoneyDetail.class);
                intent.putExtra("addMoneyAmount",tv_money.getText().toString());
                startActivity(intent);
                break;

            case R.id.bt_500:
                bt_1000.setBackgroundResource(R.drawable.grey_corner);
                bt_1500.setBackgroundResource(R.drawable.grey_corner);
                bt_500.setBackgroundResource(R.drawable.button_money);
                tv_money.setText("500");
                break;

            case R.id.bt_1000:
                bt_1000.setBackgroundResource(R.drawable.button_money);
                bt_500.setBackgroundResource(R.drawable.grey_corner);
                bt_1500.setBackgroundResource(R.drawable.grey_corner);
                tv_money.setText("1000");
                break;

            case R.id.bt_1500:
                bt_1500.setBackgroundResource(R.drawable.button_money);
                bt_1000.setBackgroundResource(R.drawable.grey_corner);
                bt_500.setBackgroundResource(R.drawable.grey_corner);
                tv_money.setText("1500");
                break;
        }

    }
}
