package com.crest.goyo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crest.goyo.AdapterClasses.NotificationsAdapter;
import com.crest.goyo.ModelClasses.NotificationsModel;
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


public class NotificationsFragment extends Fragment {
    private RecyclerView rv_notifications;
    private NotificationsAdapter notificationsAdapter;
    private LinearLayout lay_notif, lay_no_notif;
    View view;
    public static ArrayList<NotificationsModel> notifList;

    public NotificationsFragment() {
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
        view = inflater.inflate(R.layout.fragment_notifications, container, false);

        initUI();


        if (Constant.isOnline(getActivity())) {
            getNotificationsAPI();
        }

        notifList = new ArrayList<NotificationsModel>();


        return view;
    }

    private void getNotificationsAPI() {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_GET_NOTIF).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getActivity(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getActivity(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("v_type", "pending");
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClass.allRequest(getActivity(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject objData = data.getJSONObject(i);
                            JSONObject l_data = objData.getJSONObject("l_data");
                            notifList.add(new NotificationsModel(objData.getString("id"), l_data.getString("title"), l_data.getString("content")));
                            notificationsAdapter = new NotificationsAdapter(notifList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                            rv_notifications.setLayoutManager(mLayoutManager);
                            rv_notifications.setItemAnimator(new DefaultItemAnimator());
                            rv_notifications.setAdapter(notificationsAdapter);
                        }
                    } else {
                        lay_no_notif.setVisibility(View.VISIBLE);
                        lay_notif.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

    private void initUI() {

        rv_notifications = (RecyclerView) view.findViewById(R.id.rv_notifications);
        lay_no_notif = (LinearLayout) view.findViewById(R.id.lay_no_notif);
        lay_notif = (LinearLayout) view.findViewById(R.id.lay_notif);

    }


}
