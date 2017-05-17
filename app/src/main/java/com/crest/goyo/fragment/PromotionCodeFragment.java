package com.crest.goyo.fragment;


import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crest.goyo.AdapterClasses.PromotionCodeAdapter;
import com.crest.goyo.ModelClasses.PromotionCodeModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.GPSTracker;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.HttpUrl;

import static android.content.Context.LOCATION_SERVICE;


public class PromotionCodeFragment extends Fragment {

    private RecyclerView rv_promotion_code;
    private View view;
    PromotionCodeAdapter promotionCodeAdapter;
    public static ArrayList<PromotionCodeModel> codeList;
    private Geocoder geocoder;
    private GPSTracker gps;
    private double latitude,longitude;
    private String cityCurrent;
    private LinearLayout no_promotion_code,lay_recyclerview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.crest.goyo.fragment
        view= inflater.inflate(R.layout.fragment_promotion_code, container, false);

        initUI();
        gps = new GPSTracker(getActivity(), getActivity());
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(LOCATION_SERVICE);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("######", "lat: " + latitude + "long :" + longitude);
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                cityCurrent = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(Constant.isOnline(getActivity())){
                getPromotionCodesAPI();
            }
        } else {
            gps.showSettingsAlert();
        }





        codeList = new ArrayList<PromotionCodeModel>();
        return view;




    }

    private void getPromotionCodesAPI() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_PROMOTION_CODES).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(),Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(),Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("city", cityCurrent);

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
                        codeList.clear();
                        if(data.length()==0){
                            no_promotion_code.setVisibility(View.VISIBLE);
                            lay_recyclerview.setVisibility(View.GONE);
                        }else if(data.length()>0){
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject objData = data.getJSONObject(i);


                                codeList.add(new PromotionCodeModel(objData.getString("v_title"),objData.getString("v_code"),objData.getString("d_end_date"),objData.getString("l_description")));


                                promotionCodeAdapter = new PromotionCodeAdapter(codeList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                                rv_promotion_code.setLayoutManager(mLayoutManager);
                                rv_promotion_code.setItemAnimator(new DefaultItemAnimator());
                                rv_promotion_code.setAdapter(promotionCodeAdapter);
                            }
                        }

                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void initUI() {

        rv_promotion_code=(RecyclerView)view.findViewById(R.id.rv_promotion_code);
        no_promotion_code=(LinearLayout)view.findViewById(R.id.no_promotion_code);
        lay_recyclerview=(LinearLayout)view.findViewById(R.id.lay_recyclerview);
    }

}
