package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.ModelClasses.MyKidsTrips;
import com.crest.goyo.R;
import com.crest.goyo.school.clnt_tripview;

import java.util.ArrayList;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;


/**
 * Created by mTech on 02-May-2017.
 */
public class clnt_mykids_listAdapter extends BaseAdapter  implements PinnedSectionListView.PinnedSectionListAdapter {

    List<MyKidsTrips> list = new ArrayList<MyKidsTrips>();
    LayoutInflater inflater;
    Context context;
    String _drop, _pickup;
    private static String headerText = "";
    public static final int ITEM = 0;
    public static final int SECTION = 1;

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
        final MyKidsTrips mykid =  list.get(position);

        String header = mykid.pd + " - " + mykid.btch + " - " + mykid.time;

        if (mykid.Type == SECTION) {

            headerText = header;
            mViewHolder.header.setVisibility(View.VISIBLE);
//            mViewHolder.header.setBackgroundColor(parent.getResources().getColor(Color.GRAY));
            mViewHolder._item.setVisibility(View.GONE);
            mViewHolder.titleTxt.setText(mykid.btch);
            //Log.e("date",mykid.get(Tables.tbl_driver_info.createon));
            mViewHolder.Date.setText(mykid.date + " " + mykid.time);
            if (mykid.stsi.equals("1")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_play);
            } else if (mykid.stsi.equals("2")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_done);
            } else if (mykid.stsi.equals("0")) {
                mViewHolder.uploadonRes.setBackgroundResource(R.drawable.ic_action_wait);
            }
            //mViewHolder.txtMargin.setVisibility(View.VISIBLE);
            mViewHolder.btnTrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mykid.stsi.equals("0")) {
                        Toast.makeText(context, "Trip is not started! Once started you will be notify!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent in = new Intent(context, clnt_tripview.class);
                    in.putExtra("tripid", mykid.tripid);
                    in.putExtra("status", mykid.stsi);
                    context.startActivity(in);
                }
            });

        } else {
            //mViewHolder.txtMargin.setVisibility(View.GONE);
            mViewHolder.header.setVisibility(View.GONE);
            if(list.size() - 1 != position){
                if(list.get(position + 1).Type == SECTION){
                    mViewHolder.itembottom.setVisibility(View.VISIBLE);
                }
            }


        }
        if (mykid.pd.equalsIgnoreCase("p")) {
            mViewHolder.txtSideColor.setBackgroundColor(Color.parseColor("#18b400"));
            mViewHolder.povTitle.setText(_pickup);
            mViewHolder.povTitle.setTextColor(Color.parseColor("#18b400"));
        } else {
            mViewHolder.txtSideColor.setBackgroundColor(Color.RED);
            mViewHolder.povTitle.setText(_drop);

            mViewHolder.povTitle.setTextColor(Color.RED);
        }
        if (mykid.stdsi.equals("1")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_done);
        } else if (mykid.stdsi.equals("2")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_cancel);
        } else if (mykid.stdsi.equals("0")) {
            mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_wait);
        } else {
            //mViewHolder.txtKidStatus.setBackgroundResource(R.drawable.ic_action_cancel);
        }

        mViewHolder.txtkidName.setText(mykid.nm);


        return convertView;
    }



    @Override public int getViewTypeCount() {
        return 2;
    }

    @Override public int getItemViewType(int position) {
        return list.get(position).Type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType  == SECTION;
    }


    private class MyViewHolder {
        private TextView titleTxt, povTitle, uploadonRes, Date, txtSideColor, txtkidName, txtKidStatus,itembottom;
        private RelativeLayout header, _item;
        private ImageButton btnTrack;

        public MyViewHolder(View item) {
            titleTxt = (TextView) item.findViewById(R.id.titleTxt);
            povTitle = (TextView) item.findViewById(R.id.povTitle);
            uploadonRes = (TextView) item.findViewById(R.id.uploadonRes);
            Date = (TextView) item.findViewById(R.id.Date);
            txtSideColor = (TextView) item.findViewById(R.id.txtSideColor);
            txtkidName = (TextView) item.findViewById(R.id.txtkidName);
            header = (RelativeLayout) item.findViewById(R.id.header);
            _item =(RelativeLayout) item.findViewById(R.id.item);
            txtKidStatus = (TextView) item.findViewById(R.id.txtKidStatus);
            btnTrack = (ImageButton) item.findViewById(R.id.btnStartTrack);
            itembottom = (TextView) item.findViewById(R.id.itembottom);


        }
    }
}
