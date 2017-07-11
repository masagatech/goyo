package com.crest.goyo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.R;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.HttpUrl;


public class TermsAndConditionsFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the com.crest.goyo.fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private View view;
    private TextView tv_terms;
    ;

    public TermsAndConditionsFragment() {
        // Required empty public constructor
    }

    public static TermsAndConditionsFragment newInstance(){
        TermsAndConditionsFragment termsAndConditionsFragment = new TermsAndConditionsFragment();
        return termsAndConditionsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this com.crest.goyo.fragment
        view = inflater.inflate(R.layout.terms_and_conditions, container, false);

        initUI();
        if(Constant.isOnline(getContext()))
        {
            validation();
        }
        return view;
    }

    private void initUI() {
        tv_terms=(TextView)view.findViewById(R.id.tv_terms);
    }


    @Override
    public void onClick(View v) {

    }

    private void validation() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_TERMS_COND).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("key", "terms-condition");
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
                        JSONObject data=response.getJSONObject("data");
                        JSONObject j_content=data.getJSONObject("j_content");
                        String en=j_content.getString("en");
                        tv_terms.setText(Html.fromHtml(en));
                        tv_terms.setMovementMethod(LinkMovementMethod.getInstance());

                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

