package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crest.goyo.ModelClasses.MyKidsTrips;
import com.crest.goyo.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mTech on 02-May-2017.
 */
public class clnt_mykids_listAdapter extends BaseAdapter {

    List<MyKidsTrips> list = new ArrayList<MyKidsTrips>();
    LayoutInflater inflater;
    Context context;
    String _drop, _pickup;
    private static String headerText = "";
    public clnt_mykids_listAdapter(Context context, List<MyKidsTrips> lst, Resources rs) {
        this.list = lst;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        _drop = rs.getString(R.string.drop);
        _pickup = rs.getString(R.string.pickup);
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
        MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_clnt_mykids_list, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        String header = list.get(position).pd + " - " + list.get(position).btch + " - " + list.get(position).time;
        if(headerText.equals(header) != true){
            if(headerText.equals("")!=true){mViewHolder.txtBorder.setVisibility(View.VISIBLE);}

            headerText = header;
            mViewHolder.header.setVisibility(View.VISIBLE);
            mViewHolder.titleTxt.setText(list.get(position).btch);

            //Log.e("date",list.get(position).get(Tables.tbl_driver_info.createon));
            mViewHolder.Date.setText(list.get(position).time);

            if (list.get(position).stsi.equals("1")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_play);

            } else if (list.get(position).stsi.equals("2")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_done);
            } else if (list.get(position).stsi.equals("0")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_wait);
            }

        }else{

           mViewHolder.header.setVisibility(View.GONE);
        }
        if (list.get(position).pd.equalsIgnoreCase("p")) {
            mViewHolder.txtSideColor.setBackgroundColor(Color.parseColor("#18b400"));
            mViewHolder.povTitle.setText(_pickup);
            mViewHolder.povTitle.setTextColor(Color.parseColor("#18b400"));
        } else {
            mViewHolder.txtSideColor.setBackgroundColor(Color.RED);
            mViewHolder.povTitle.setText(_drop);

            mViewHolder.povTitle.setTextColor(Color.RED);
        }
        if (list.get(position).stdsi.equals("1")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_done);
        } else if (list.get(position).stdsi.equals("2")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_cancel);
        } else if (list.get(position).stdsi.equals("0")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_wait);
        }else {
            //mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_cancel);
        }

        mViewHolder.txtkidName.setText(list.get(position).nm);

        return convertView;
    }


    private class MyViewHolder {
        private TextView titleTxt, povTitle, uploadonRes, Date, hidid, txtSideColor, txtkidName,txtBorder,txtKidStatus;
        private RelativeLayout header;

        public MyViewHolder(View item) {
            hidid = (TextView) item.findViewById(R.id.hidid);
            titleTxt = (TextView) item.findViewById(R.id.titleTxt);
            povTitle = (TextView) item.findViewById(R.id.povTitle);
            uploadonRes = (TextView) item.findViewById(R.id.uploadonRes);
            Date = (TextView) item.findViewById(R.id.Date);
            txtSideColor = (TextView) item.findViewById(R.id.txtSideColor);
            txtkidName = (TextView) item.findViewById(R.id.txtkidName);
            header = (RelativeLayout) item.findViewById(R.id.header);
            txtBorder = (TextView)item.findViewById(R.id.txtBorder);

            txtKidStatus= (TextView)item.findViewById(R.id.txtKidStatus);
        }
    }
}
