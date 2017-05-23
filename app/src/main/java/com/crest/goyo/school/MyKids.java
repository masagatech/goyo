package com.crest.goyo.school;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.crest.goyo.AdapterClasses.MyKidsListAdapter;
import com.crest.goyo.ModelClasses.MyKidsModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.CustomDialog;
import com.crest.goyo.Utils.Global;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyKids extends Fragment {

    View view;
    ListView lstmykids;
    private ProgressDialog loader;
    private List<MyKidsModel> lstmykidsd;
    private MyKidsModel mykid;
    MenuItem menu_refresh;
    private CustomDialog customDialog;

    FloatingActionButton btbtnAddNew;


    public MyKids() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.fragment_my_kids, container, false);
        initUI();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_todaystrips_activity,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    //action bar menu button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                bindListView();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    private void initUI() {
        customDialog = new CustomDialog(this.getActivity());
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btbtnAddNew = (FloatingActionButton) view.findViewById(R.id.btbtnAddNew);
        lstmykids = (ListView) view.findViewById(R.id.lstmykids);
        loader = new ProgressDialog(this.getActivity());
        addListners();
        bindListView();

    }

    private void addListners(){
        lstmykids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mykid = ((MyKidsModel) parent.getItemAtPosition(position));
                Intent in = new Intent(MyKids.this.getActivity(), clnt_mykidstrips.class);
//                in.putExtra("tripid",mykid.tripid);
                in.putExtra("stdid",mykid.StudId);
                startActivity(in);
            }
        });

        btbtnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MyKids.this.getActivity(), MyKidsRegistration.class),2);
            }
        });

    }

    // Call Back method  to get the Message form other Activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==2)
        {
            bindListView();
        }
    }

    private void bindListView() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", Global.getUserID(this.getActivity().getApplicationContext()));
        json.addProperty("flag", "mykidsreg");
        customDialog.show();
        Ion.with(this)
                .load(Global.urls.getmykids.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            // JSONObject jsnobject = new JSONObject(jsond);
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<MyKidsModel>>() {
                            }.getType();
                            lstmykidsd = (List<MyKidsModel>) gson.fromJson(result.get("data"), listType);
                            bindCreawData(lstmykidsd);
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        customDialog.hide();
                    }
                });
    }




    MyKidsListAdapter _clnt_mykids_listAdapter;
    List<MyKidsModel> _mykidlst;

    private void bindCreawData(List<MyKidsModel> lst) {
        if (lst.size() > 0) {
            _mykidlst = lst;
            view.findViewById(R.id.txtNodata).setVisibility(View.GONE);
            _clnt_mykids_listAdapter = new MyKidsListAdapter(this.getActivity(), lst);
            lstmykids.setAdapter(_clnt_mykids_listAdapter);
            lstmykids.setVisibility(View.VISIBLE);
            notifyCrewChanges();

        } else {
            lstmykids.setVisibility(View.GONE);
            view.findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
        }
    }

    private void notifyCrewChanges() {
        if (_clnt_mykids_listAdapter != null) {
            /*Collections.sort(_crewlst, new Comparator<model_crewdata>() {
                public int compare(model_crewdata o1, model_crewdata o2) {
                    return o1.stsi.compareToIgnoreCase(o2.stsi);
                }
            });*/
            _clnt_mykids_listAdapter.notifyDataSetChanged();
        }
    }



}
