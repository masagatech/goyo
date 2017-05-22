package com.crest.goyo.AdapterClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.ModelClasses.MyKidsModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.CustomDialog;
import com.crest.goyo.Utils.Global;
import com.crest.goyo.school.MyKidsRegistration;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llc on 5/22/2017.
 */

public class MyKidsRegListAdapter extends BaseAdapter {

    List<MyKidsModel> list = new ArrayList<MyKidsModel>();
    LayoutInflater inflater;
    Context context;
    String _drop, _pickup;
    private CustomDialog customDialog;
    private static String headerText = "";

    public MyKidsRegListAdapter(Context context, List<MyKidsModel> lst, CustomDialog _customDialog) {
        this.list = lst;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        customDialog = _customDialog;

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyKidsRegListAdapter.MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_mykidsreg_rowlist, parent, false);
            mViewHolder = new MyKidsRegListAdapter.MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyKidsRegListAdapter.MyViewHolder) convertView.getTag();
        }

        final MyKidsModel md = list.get(position);

        mViewHolder.txtTitle.setText(md.Name);
        mViewHolder.txtTitle1.setText("Div : " + md.Div);
        mViewHolder.txtTitle2.setText(md.School);
        mViewHolder.swtchActive.setChecked(md.Activated);

        mViewHolder.swtchActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch sch = (Switch) v.findViewById(R.id.switchactive);
                activatedeactivatekid(sch.isChecked(), md.StudId + "");
            }
        });

        return convertView;
    }

    private void activatedeactivatekid(final Boolean isActivate, String Studid) {
        JsonObject json = new JsonObject();
        json.addProperty("uid", Global.getUserID(context.getApplicationContext()));
        json.addProperty("stdid", Studid);
        json.addProperty("parent_uid", "0");
        json.addProperty("active", isActivate + "");
        customDialog.show();
        Ion.with(context)
                .load(Global.urls.activatekid.value)
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        try {
                            if (result != null) Log.v("result", result.toString());
                            JsonObject res = result.get("data").getAsJsonObject();
                            if (res.get("resstatus").getAsBoolean()) {
                                Toast.makeText(context, res.get("resmessage").toString(), Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(context, res.get("resmessage").toString(), Toast.LENGTH_SHORT).show();
                            }

                            // JSONObject jsnobject = new JSONObject(jsond);
//                            Gson gson = new Gson();
//                            Type listType = new TypeToken<List<MyKidsModel>>() {
//                            }.getType();
//                            lstmykidsd = (List<MyKidsModel>) gson.fromJson(result.get("data"), listType);
//                            bindCreawData(lstmykidsd);
                        } catch (Exception ea) {
                            ea.printStackTrace();
                            Toast.makeText(context, "Opps! Something went's wrong! Please try again later!", Toast.LENGTH_SHORT).show();
                        }
                        customDialog.hide();
                    }
                });

    }


    private class MyViewHolder {
        private TextView txtTitle, txtTitle1, txtTitle2;
        private Switch swtchActive;

        public MyViewHolder(View item) {
            txtTitle = (TextView) item.findViewById(R.id.title);
            txtTitle1 = (TextView) item.findViewById(R.id.title1);
            txtTitle2 = (TextView) item.findViewById(R.id.title2);
            swtchActive = (Switch) item.findViewById(R.id.switchactive);
        }
    }

}
