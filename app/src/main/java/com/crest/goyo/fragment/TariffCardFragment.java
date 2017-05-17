package com.crest.goyo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.List;

import okhttp3.HttpUrl;


public class TariffCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private View view;
    private TextView tv_base_fare_label, tv_upto_km_label, tv_after_km_label, tv_ride_time_charge_label, tv_min_fare_label, tv_ride_time_charges_label, tv_base_fare, tv_upto_km, tv_after_km, tv_ride_time_charge, tv_ride_time_charge_detail, tv_min_fare, tv_ride_time_charges, tv_ride_time_charges_detail, tv_service_tax, tv_service_tax_detail;
    private Spinner spinner_city, spinner_vehicle;
    private ArrayAdapter<String> vehicleListAdapter;
    private ArrayAdapter<String> cityListAdapter;
    private List<String> vehicleTypeList = new ArrayList<>();
    private List<String> cityData = new ArrayList<String>();
    private ArrayList<String> cityArray = new ArrayList<String>();
    private int cityId = 0;
    private List<String> vehicleData = new ArrayList<String>();
    private ArrayList<String> vehicleArray = new ArrayList<String>();
    private int vehicleId = 0;

    public TariffCardFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tariff_card, container, false);
        initUI();
        if (Constant.isOnline(getActivity())) {
            vehicleTypeAPI();
        }
        vehicleListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_list_item, vehicleTypeList);
        vehicleListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_vehicle.setAdapter(vehicleListAdapter);
        spinner_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                vehicleId = position;
                try {
                    vehicleArray.get(vehicleId);

                    getTeriffCardAPI(spinner_city.getSelectedItemId(), spinner_vehicle.getSelectedItemId());
                } catch (Exception e) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (Constant.isOnline(getActivity())) {
            cityListAPI();
        }
        cityListAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_list_item, cityData);
        cityListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_city.setAdapter(cityListAdapter);
        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                cityId = position;
                try {
                    cityArray.get(cityId);
                    getTeriffCardAPI(spinner_city.getSelectedItemId(), spinner_vehicle.getSelectedItemId());
                }catch (Exception e){

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    private void cityListAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_CITY_TYPE).newBuilder();
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String msg = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        JSONObject jsonObject;
                        cityData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            cityData.add(jsonObject.getString("v_name"));
                            cityArray.add(jsonObject.getString("id"));
                        }
                        cityListAdapter.notifyDataSetChanged();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, false);
    }

    private void vehicleTypeAPI() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_VEHICLE_TYPES).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("city", "");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClass.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                final String success = response.optString("status").toString();
                final String message = response.optString("message").toString();
                String value = String.valueOf(success);
                android.util.Log.e("value", "    " + value);
                if (value.equals("0")) {
                } else {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        for (int i = 1; i < data.length() + 1; i++) {
                            JSONObject objData = data.getJSONObject(String.valueOf(i));
                            vehicleListAdapter.add(objData.getString("v_name"));
                            vehicleData.add(objData.getString("v_name"));
                            vehicleArray.add(objData.getString("id"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, false);

    }

    private void getTeriffCardAPI(long city, long vehicle) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_TERIFF_CARD).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("i_city_id", cityArray.get(cityId));
        urlBuilder.addQueryParameter("i_vehicle_type_id", vehicleArray.get(vehicleId));
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
                        JSONObject jsonObject = response.getJSONObject("data");
                        JSONObject l_data = jsonObject.getJSONObject("l_data");
                        JSONObject charges = l_data.getJSONObject("charges");
                        tv_base_fare.setText("\u20B9" + " " + charges.getString("base_fare"));
                        tv_upto_km_label.setText("0 - " + charges.getString("upto_km") + " KM");
                        tv_upto_km.setText("\u20B9" + " " + charges.getString("upto_km_charge") + " per Km");
                        tv_after_km_label.setText("After " + charges.getString("upto_km") + " KM");
                        tv_after_km.setText("\u20B9" + " " + charges.getString("after_km_charge") + " per Km");
                        tv_ride_time_charge.setText("\u20B9" + " " + charges.getString("ride_time_charge") + " per Min");
                        tv_min_fare.setText("\u20B9" + " " + charges.getString("min_charge"));
                        tv_ride_time_charges.setText("\u20B9" + " " + charges.getString("ride_time_pick_charge") + " per Min");
                        tv_service_tax.setText("\u20B9" + " " + charges.getString("ride_time_pick_charge"));
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);


    }

    private void initUI() {
        tv_base_fare = (TextView) view.findViewById(R.id.tv_base_fare);
        tv_upto_km = (TextView) view.findViewById(R.id.tv_upto_km);
        tv_after_km = (TextView) view.findViewById(R.id.tv_after_km);
        tv_ride_time_charge = (TextView) view.findViewById(R.id.tv_ride_time_charge);
        tv_ride_time_charge_detail = (TextView) view.findViewById(R.id.tv_ride_time_charge_detail);
        tv_ride_time_charges = (TextView) view.findViewById(R.id.tv_ride_time_charges);
        tv_ride_time_charges_detail = (TextView) view.findViewById(R.id.tv_ride_time_charges_detail);
        tv_service_tax = (TextView) view.findViewById(R.id.tv_service_tax);
        tv_service_tax_detail = (TextView) view.findViewById(R.id.tv_service_tax_detail);
        tv_base_fare_label = (TextView) view.findViewById(R.id.tv_base_fare_label);
        tv_upto_km_label = (TextView) view.findViewById(R.id.tv_upto_km_label);
        tv_after_km_label = (TextView) view.findViewById(R.id.tv_after_km_label);
        tv_ride_time_charge_label = (TextView) view.findViewById(R.id.tv_ride_time_charge_label);
        tv_min_fare_label = (TextView) view.findViewById(R.id.tv_min_fare_label);
        tv_ride_time_charges_label = (TextView) view.findViewById(R.id.tv_ride_time_charges_label);
        spinner_city = (Spinner) view.findViewById(R.id.spinner_city);
        tv_min_fare = (TextView) view.findViewById(R.id.tv_min_fare);
        spinner_vehicle = (Spinner) view.findViewById(R.id.spinner_vehicle);
    }

}
