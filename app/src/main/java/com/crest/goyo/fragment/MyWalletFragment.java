package com.crest.goyo.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crest.goyo.AdapterClasses.WalletHistoryAdapter;
import com.crest.goyo.AddMoney;
import com.crest.goyo.ModelClasses.WalletHistoryModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.HttpUrl;


public class MyWalletFragment extends Fragment implements View.OnClickListener {

    private Button bt_add_money;
    private View view;
    private TextView tv_amount;
    public static ArrayList<WalletHistoryModel> historyList;
    private WalletHistoryAdapter walletHistoryAdapter;
    private RecyclerView rv_history;

    public MyWalletFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.crest.goyo.fragment
        view = inflater.inflate(R.layout.fragment_my_wallet, container, false);

        initUI();
        historyList = new ArrayList<WalletHistoryModel>();

        return view;
    }

    private void getUserWalletAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_WALLET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getContext(), Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray history = data.getJSONArray("wallet_history");
                        for (int i = 0; i < history.length(); i++) {
                            historyList.clear();
                            JSONObject objData = history.getJSONObject(i);

                            {
                                historyList.add(new WalletHistoryModel(objData.getString("message"), objData.getString("from")));
                            }
                            walletHistoryAdapter = new WalletHistoryAdapter(historyList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            rv_history.setLayoutManager(mLayoutManager);
                            rv_history.setItemAnimator(new DefaultItemAnimator());
                            rv_history.setAdapter(walletHistoryAdapter);
                        }

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
        bt_add_money = (Button) view.findViewById(R.id.bt_add_money);
        tv_amount = (TextView) view.findViewById(R.id.tv_amount);
        rv_history = (RecyclerView) view.findViewById(R.id.rv_history);
        bt_add_money.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_money:
                Intent intent = new Intent(getActivity(), AddMoney.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onStart() {
        if (Constant.isOnline(getActivity())) {
            getUserWalletAPI();
        }
        super.onStart();
    }
}