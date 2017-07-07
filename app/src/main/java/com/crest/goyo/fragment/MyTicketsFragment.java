package com.crest.goyo.fragment;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.AdapterClasses.MyTicketsAdapter;
import com.crest.goyo.ModelClasses.MyTicketsModel;
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
import java.util.HashMap;

import okhttp3.HttpUrl;

public class MyTicketsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView mAddTicket;
    private TextView mNoTicketLbl;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private MyTicketsAdapter myTicketsAdapter;

    public static ArrayList<MyTicketsModel> myTicketsModelArrayList = new ArrayList<>();
    private ArrayList<String> mSpineerItemsArray = new ArrayList<>();
    private ArrayList<String> mSpineerItemsArrayKey = new ArrayList<>();
    private HashMap<String,String> hashMap = new HashMap<String, String>();

    public MyTicketsFragment() {
        // Required empty public constructor
    }

    public static MyTicketsFragment newInstance(String param1, String param2) {
        MyTicketsFragment fragment = new MyTicketsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_tickets, container, false);

        initView(view);

        return view;
    }

    private void initView(View mView){

        mNoTicketLbl = (TextView) mView.findViewById(R.id.txt_lable);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.rc_my_tickets);
        mAddTicket = (TextView) mView.findViewById(R.id.txt_add_new_ticket);

        mNoTicketLbl.setVisibility(View.GONE);

        mAddTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTicketDialog();
            }
        });


        if(Constant.isOnline(getContext()))
        {
            getSupportTypes();

            setMyTicketsAdapter();

            getMyTicketApi();
        }


    }


    private void setMyTicketsAdapter(){
        myTicketsAdapter = new MyTicketsAdapter(getActivity(),myTicketsModelArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(myTicketsAdapter);
    }


    private void showAddTicketDialog(){
        // Create custom dialog object
        final Dialog dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_add_ticket);
        // Set dialog title
        dialog.setTitle("Add new ticket");

        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner_ticket_type);
        Button submitButton = (Button) dialog.findViewById(R.id.btn_apply);
        Button declineButton = (Button) dialog.findViewById(R.id.btn_cancel);
        final EditText mDescription = (EditText) dialog.findViewById(R.id.txt_discription);

        ArrayAdapter<String> dataAdapter=new ArrayAdapter<String>(getActivity(),
                R.layout.custom_xml_spinner_layout,mSpineerItemsArray);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        dialog.show();


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mText = mDescription.getText().toString().trim();
                if(!mText.isEmpty() && mText != null){
                    createTicket(hashMap.get(spinner.getSelectedItem()),mText);
                    dialog.dismiss();
                }else {
                    mDescription.setError("Pease enter discription");
                }
            }
        });


        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
    }


    private void getMyTicketApi() {
        FileUtils.showProgressBar(getActivity(),mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_MY_TICKET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(getContext(),Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClassNew.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        mNoTicketLbl.setVisibility(View.GONE);
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        JSONArray data = response.getJSONArray("data");

                        if(!myTicketsModelArrayList.isEmpty())
                            myTicketsModelArrayList.clear();

                        for(int i=0;i<data.length();i++){
                                MyTicketsModel myTicketsModel = new MyTicketsModel(data.getJSONObject(i).getString("v_support_id"),
                                    data.getJSONObject(i).getString("e_status"),
                                    data.getJSONObject(i).getString("d_added"),
                                    data.getJSONObject(i).getString("title"),
                                    data.getJSONObject(i).getString("text"));
                            myTicketsModelArrayList.add(myTicketsModel);
                        }
                        myTicketsAdapter.notifyDataSetChanged();

                    }else {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        mNoTicketLbl.setText(message);
                        mNoTicketLbl.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    FileUtils.hideProgressBar(getActivity(),mProgressBar);
                }
            }
        });
    }

    private void createTicket(String i_type_id,String v_support_text) {

        FileUtils.showProgressBar(getActivity(),mProgressBar);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.CREATE_TICKET).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(getContext(),Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("v_type","support_type");
        urlBuilder.addQueryParameter("i_type_id",i_type_id);
        urlBuilder.addQueryParameter("v_support_text",v_support_text);

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClassNew.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        getMyTicketApi();
                    }else {
                        FileUtils.hideProgressBar(getActivity(),mProgressBar);
                        Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    FileUtils.hideProgressBar(getActivity(),mProgressBar);
                    e.printStackTrace();
                }
            }
        });
    }

    private void getSupportTypes() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_SUPPORT_TYPE).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token",Preferences.getValue_String(getContext(),Preferences.USER_AUTH_TOKEN));

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();

        VolleyRequestClassNew.allRequest(getContext(), newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONArray data = response.getJSONArray("data");

                        for(int i=0;i<data.length();i++){
                            mSpineerItemsArray.add( data.getJSONObject(i).getString("j_title"));
                            hashMap.put(data.getJSONObject(i).getString("j_title"),
                                    data.getJSONObject(i).getString("id"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
     //   mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
