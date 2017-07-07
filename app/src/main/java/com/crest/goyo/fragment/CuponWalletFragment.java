package com.crest.goyo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.AdapterClasses.WalletHistoryAdapter;
import com.crest.goyo.ModelClasses.WalletHistoryModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.FileUtils;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.HttpUrl;


public class CuponWalletFragment extends Fragment implements View.OnClickListener {

    private View view;
    private RecyclerView rv_my_wallet;
    private WalletHistoryAdapter myWalletAdapter;
    private TextView mWallet;
    private ProgressBar mProgressBar;
    public static ArrayList<WalletHistoryModel> walletList;

    public CuponWalletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.crest.goyo.fragment
        view = inflater.inflate(R.layout.fragment_my_cupon, container, false);

        initUI();

        walletList = new ArrayList<WalletHistoryModel>();

        myWalletAdapter = new WalletHistoryAdapter(walletList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rv_my_wallet.setLayoutManager(mLayoutManager);
        rv_my_wallet.setItemAnimator(new DefaultItemAnimator());
        rv_my_wallet.setAdapter(myWalletAdapter);
        if(Constant.isOnline(getContext()))
        {
            my_wallet_api();
        }

        return view;
    }

    private void initUI() {
        mWallet = (TextView) view.findViewById(R.id.txt_wallet_rs);
        rv_my_wallet=(RecyclerView) view.findViewById(R.id.rv_my_wallet);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        rv_my_wallet.setOnClickListener(this);
    }

    private void my_wallet_api() {

        FileUtils.showProgressBar(getActivity(),mProgressBar);

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_USER_WALLET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("wallet_type", "coupon");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",  Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        JSONObject data = response.getJSONObject("data");
                        mWallet.setText("₹ "+data.getString("wallet_amount"));

                        JSONArray wallet_history = data.getJSONArray("wallet_history");

                        for (int i = 0; i < wallet_history.length(); i++) {
                            walletList.add(new WalletHistoryModel(wallet_history.getJSONObject(i).getString("message"),
                                                                wallet_history.getJSONObject(i).getString("from")));
                        }
                        myWalletAdapter.notifyDataSetChanged();
                    } else {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}
