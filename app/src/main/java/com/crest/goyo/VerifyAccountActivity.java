package com.crest.goyo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.FileUtils;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;

public class VerifyAccountActivity extends AppCompatActivity {

    private ImageView mBack;
    private TextView mMobileNo;
    private TextView mVarifyCode;
    private Button mVeryfy;
    private Button mResendOtp;
    private ProgressBar mProgressBar;
    private String id = "";
    private String mobile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verify_account);

        mBack = (ImageView) findViewById(R.id.img_back);
        mMobileNo = (TextView) findViewById(R.id.txt_mobile);
        mVarifyCode = (TextView) findViewById(R.id.txt_varify_code);
        mVeryfy = (Button) findViewById(R.id.btn_veryfy);
        mResendOtp = (Button) findViewById(R.id.btn_resend_otp);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        if (getIntent().getExtras() != null) {
            id = getIntent().getExtras().getString("id", "");
            mobile = getIntent().getExtras().getString("phone", "");

            mMobileNo.setText(mobile);
        }
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mVeryfy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verify_mobileno_api(mMobileNo.getText().toString(), mVarifyCode.getText().toString());
            }
        });
        mResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend_otp_api(id, mobile);
            }
        });
    }

    private void verify_mobileno_api(String mMobileNo, String mOtp) {
        FileUtils.showProgressBar(VerifyAccountActivity.this, mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.VERIFY_ACCOUNT).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("v_username", mMobileNo);
        urlBuilder.addQueryParameter("v_otp", mOtp);
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
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, true);
                    } else {
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    private void resend_otp_api(String mId, String mMobileNo) {
        FileUtils.showProgressBar(VerifyAccountActivity.this, mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.RESEND_OTP).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("type", "user");
        urlBuilder.addQueryParameter("v_phone", mMobileNo);
        urlBuilder.addQueryParameter("id", mId);
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
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, false);
                    } else {
                        FileUtils.hideProgressBar(VerifyAccountActivity.this, mProgressBar);
                        showAlertDialog(message, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    private void showAlertDialog(String message, final boolean isSuccess) {
        final AlertDialog.Builder builder1 = new AlertDialog.Builder(VerifyAccountActivity.this);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isSuccess) {
                            Intent intent = new Intent(getApplicationContext(), Login.class);
                            startActivity(intent);
                            dialog.cancel();
                        } else {
                            dialog.cancel();
                        }
                    }
                });
        AlertDialog alertDialogg = builder1.create();
        alertDialogg.show();
    }
}
