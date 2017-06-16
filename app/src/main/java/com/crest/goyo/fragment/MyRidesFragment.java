package com.crest.goyo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crest.goyo.AdapterClasses.MyRidesAdapter;
import com.crest.goyo.ModelClasses.MyRidesModel;
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


public class MyRidesFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    private RecyclerView rv_my_rides;
    MyRidesAdapter myRidesAdapter;
    View view;
    private TextView tv_error;
    public static ArrayList<MyRidesModel> rideList;

    public MyRidesFragment() {
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
        view = inflater.inflate(R.layout.fragment_my_rides, container, false);

        initUI();


        rideList = new ArrayList<MyRidesModel>();
//        for (int i = 0; i < 10; i++) {
//            rideList.add(new MyRidesModel("Annie TATE", "Completed", "Toyota", "Today", "at 3:26 PM", "350, Mauricio Walks to", "350, Mauricio Walks"));
//
//        }


        return view;
    }

    private void initUI() {
        rv_my_rides = (RecyclerView) view.findViewById(R.id.rv_my_rides);
        tv_error=(TextView)view.findViewById(R.id.tv_error);
        if(Constant.isOnline(getActivity())){
            getDriverRidesAPI();
        }


    }

    private void getDriverRidesAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_USER_RIDES).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(),Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(),Preferences.USER_AUTH_TOKEN));

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
                        JSONArray data=response.getJSONArray("data");
                        rideList.clear();
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            {
                                rideList.add(new MyRidesModel(objData.getString("id"),objData.getString("status"),objData.getString("ride_time"),objData.getString("vehicle_type"),objData.getString("pickup_address"),objData.getString("destination_address"),objData.getString("driver_name"),objData.getString("v_ride_code"),objData.getString("driver_v_id")));
                            }

                            myRidesAdapter = new MyRidesAdapter(rideList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            rv_my_rides.setLayoutManager(mLayoutManager);
                            rv_my_rides.setItemAnimator(new DefaultItemAnimator());
                            rv_my_rides.setAdapter(myRidesAdapter);
                        }
                    } else {
                        tv_error.setVisibility(View.VISIBLE);
                        rv_my_rides.setVisibility(View.GONE);
                        tv_error.setText(message);
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
            case R.id.rv_my_rides:
                break;

        }

    }
}