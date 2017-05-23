package com.crest.goyo.school;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.crest.goyo.AdapterClasses.clnt_mykids_listAdapter;
import com.crest.goyo.ModelClasses.MyKidsTrips;
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

public class clnt_mykidstrips extends AppCompatActivity {

    //variable
    ListView lstmykids;
    private ProgressDialog loader;
    private List<MyKidsTrips> lstmykidsd;
    private MyKidsTrips mykid;
    MenuItem menu_refresh;
    private CustomDialog customDialog;
    private String studentId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clnt_mykids);
        setTitle("Today's Trip Status");
        getBundle();
        initUI();
    }

    private void initUI() {
        customDialog = new CustomDialog(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lstmykids = (ListView) findViewById(R.id.lstmykids);
        loader = new ProgressDialog(this);
        addListners();
        bindListView();

    }

    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        studentId = bundle.get("stdid").toString();

    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu_refresh = menu.findItem(R.id.menu_refresh);
        return true;
    }

    //set action bar button menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todaystrips_activity, menu);
        return true;
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

    private void addListners(){
//        lstmykids.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                mykid = ((MyKidsTrips) parent.getItemAtPosition(position));
//                if(mykid.stsi.equals("0")){
//                    Toast.makeText(clnt_mykidstrips.this,"Trip is not started! Once started you will be notify!",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Intent in = new Intent(clnt_mykidstrips.this, clnt_tripview.class);
//                in.putExtra("tripid",mykid.tripid);
//                in.putExtra("status",mykid.stsi);
//                startActivity(in);
//            }
//        });
    }

    private void bindListView() {
        JsonObject json = new JsonObject();
        json.addProperty("uid", Global.getUserID(getApplicationContext()));
        json.addProperty("studid",studentId );
        json.addProperty("flag", "todaystrip");
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
                            Type listType = new TypeToken<List<MyKidsTrips>>() {
                            }.getType();
                            lstmykidsd = (List<MyKidsTrips>) gson.fromJson(result.get("data"), listType);
                            bindCreawData(lstmykidsd);
                        } catch (Exception ea) {
                            ea.printStackTrace();
                        }
                        customDialog.hide();
                    }
                });
    }


    clnt_mykids_listAdapter _clnt_mykids_listAdapter;
    List<MyKidsTrips> _mykidlst;

    private void bindCreawData(List<MyKidsTrips> lst) {
        if (lst.size() > 0) {
            _mykidlst = lst;
            findViewById(R.id.txtNodata).setVisibility(View.GONE);
            _clnt_mykids_listAdapter = new clnt_mykids_listAdapter(this, lst, getResources());
            lstmykids.setAdapter(_clnt_mykids_listAdapter);
            notifyCrewChanges();

        } else {
            findViewById(R.id.txtNodata).setVisibility(View.VISIBLE);
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

    @Override
    public boolean onSupportNavigateUp() {
        this.finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

}
