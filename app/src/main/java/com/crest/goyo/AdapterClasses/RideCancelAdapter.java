package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crest.goyo.ModelClasses.RideCancelModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.Preferences;

import java.util.List;

/**
 * Created by annie on 15/4/17.
 */

public class RideCancelAdapter extends RecyclerView.Adapter<RideCancelAdapter.MyView>{

    List<RideCancelModel> list;
    private Context context;
    private int index;

    public RideCancelAdapter(List<RideCancelModel> list) {
        this.list = list;
    }

    @Override
    public RideCancelAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ride_req_cancel, parent, false);
        context = parent.getContext();
        return new RideCancelAdapter.MyView(view);
    }

    @Override
    public void onBindViewHolder(RideCancelAdapter.MyView holder, final int position) {

        holder.tv_req_can.setText("" + list.get(position).getTv_req_can());


        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.setValue(context.getApplicationContext(),"cancel_id",list.get(position).getmRideId());
                index = position;
                notifyDataSetChanged();
            }
        });

        holder.rb_req_can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.setValue(context.getApplicationContext(),"cancel_id",list.get(position).getmRideId());
                index = position;
                notifyDataSetChanged();
            }
        });

        if (index == position){
            holder.rb_req_can.setChecked(true);
        }else {
            holder.rb_req_can.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        private TextView tv_req_can;
        private RadioButton rb_req_can;
        private  RelativeLayout mRootView;


        public MyView(View itemView) {
            super(itemView);

            mRootView = (RelativeLayout)itemView.findViewById(R.id.rl_root_ridecancle);
            rb_req_can = (RadioButton) itemView.findViewById(R.id.rb_req_can);
            tv_req_can = (TextView) itemView.findViewById(R.id.tv_req_can);
//            rl_ride_cancel = (RelativeLayout) itemView.findViewById(R.id.rl_ride_cancel);

        }
    }


}
