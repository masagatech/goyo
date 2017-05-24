package com.crest.goyo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crest.goyo.R;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;


public class ReferralCodeFragment extends Fragment implements View.OnClickListener{

    private TextView actionbar_title, tv_code, tv_earn_money;
    private Button bt_invite;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.crest.goyo.fragment
        view = inflater.inflate(R.layout.activity_referral_code, container, false);

        initUI();

        if (Constant.isOnline(getActivity())) {
            getReferralCode();
        }

        return view;
    }



    private void initUI() {

        actionbar_title=(TextView)view.findViewById(R.id.actionbar_title);
        tv_code=(TextView)view.findViewById(R.id.tv_code);
        tv_earn_money=(TextView)view.findViewById(R.id.tv_earn_money);
        bt_invite=(Button)view.findViewById(R.id.bt_invite);
        actionbar_title.setText(R.string.actionbar_referralcode);


        bt_invite.setOnClickListener(this);
    }
    private void getReferralCode() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_REFERRAL_CODE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id",Preferences.getValue_String(getActivity(),Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(),Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        if(jsonObject.getString("v_referral_code").equals("")){
                            tv_code.setVisibility(View.GONE);
                            tv_earn_money.setText("No Referral Code Available");
                            bt_invite.setEnabled(false);
                        }else{
                            tv_code.setText(jsonObject.getString("v_referral_code"));
                            tv_earn_money.setText("Share your referral code and get "+"\u20B9"+" "+jsonObject.getString("earn_money")+". So share your code and get your money.");
                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_invite:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, tv_code.getText().toString());
                intent.setType("text/plain");
                startActivity(intent);
                break;
        }

    }
}
