package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crest.goyo.CancelMyRidesDetail;
import com.crest.goyo.CompleteMyRidesDetail;
import com.crest.goyo.ModelClasses.MyRidesModel;
import com.crest.goyo.R;
import com.crest.goyo.StartMyRidesDetail;

import java.util.List;

/**
 * Created by brittany on 3/25/17.
 */

public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.MyView> {

    List<MyRidesModel> list;
    Context context;

    public MyRidesAdapter(List<MyRidesModel> list) {
        this.list = list;
    }

    @Override
    public MyRidesAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.may_rides_list_items, parent, false);
        context = parent.getContext();
        return new MyRidesAdapter.MyView(view);
    }

    @Override
    public void onBindViewHolder(MyRidesAdapter.MyView holder, final int position) {


        holder.tv_dr_name.setText("" + list.get(position).getDriver_name());
        holder.tv_vehicle_detail.setText("" + list.get(position).getVehicle_type());

        holder.tv_pickup.setText("" + list.get(position).getPickup_address() + " to");
        holder.tv_drop.setText("" + list.get(position).getDestination_address());

        String date =  DateUtils.formatDateTime(context, Long.parseLong(list.get(position).getRide_time()), DateUtils.FORMAT_SHOW_DATE);
        String time = DateUtils.formatDateTime(context, Long.parseLong(list.get(position).getRide_time()), DateUtils.FORMAT_SHOW_TIME);
        holder.tv_date_time.setText("" +date+" "+ time);

        if (list.get(position).getStatus().equals("complete")) {
            holder.view_status.setBackgroundResource(R.color.colorGreen);
            holder.tv_ride_status.setText("" + list.get(position).getStatus());
            holder.tv_ride_status.setTextColor(context.getResources().getColor(R.color.colorGreen));
        } else if (list.get(position).getStatus().equals("scheduled")) {
            holder.view_status.setBackgroundResource(R.color.colorBlue);
            holder.tv_ride_status.setText("" + list.get(position).getStatus());
            holder.tv_ride_status.setTextColor(context.getResources().getColor(R.color.colorBlue));
        } else if (list.get(position).getStatus().equals("cancel")) {
            holder.view_status.setBackgroundResource(R.color.colorRed);
            holder.tv_ride_status.setText("" + list.get(position).getStatus());
            holder.tv_ride_status.setTextColor(context.getResources().getColor(R.color.colorRed));
        }else if(list.get(position).getStatus().equals("start")){
            holder.view_status.setBackgroundResource(R.color.colorPrimary);
            holder.tv_ride_status.setText("" + list.get(position).getStatus());
            holder.tv_ride_status.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        holder.lay_my_rides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list.get(position).getStatus().equals("cancel")){
                    Intent intent = new Intent(context, CancelMyRidesDetail.class);
                    intent.putExtra("rideID",list.get(position).getId());
                    context.startActivity(intent);
                }else if(list.get(position).getStatus().equals("start")){
                    Intent intent = new Intent(context, StartMyRidesDetail.class);
                    intent.putExtra("rideID",list.get(position).getId());
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, CompleteMyRidesDetail.class);
                    intent.putExtra("rideID",list.get(position).getId());
                    context.startActivity(intent);
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView tv_dr_name, tv_vehicle_detail, tv_ride_status, tv_date_time, tv_pickup, tv_drop;
        LinearLayout lay_my_rides;
        View view_status;


        public MyView(View itemView) {
            super(itemView);
            tv_dr_name = (TextView) itemView.findViewById(R.id.tv_dr_name);
            tv_vehicle_detail = (TextView) itemView.findViewById(R.id.tv_vehicle_detail);
            tv_ride_status = (TextView) itemView.findViewById(R.id.tv_ride_status);
            tv_date_time = (TextView) itemView.findViewById(R.id.tv_date_time);
            tv_pickup = (TextView) itemView.findViewById(R.id.tv_pickup);
            tv_drop = (TextView) itemView.findViewById(R.id.tv_drop);
            lay_my_rides = (LinearLayout) itemView.findViewById(R.id.lay_my_rides);
            view_status = (View) itemView.findViewById(R.id.view_status);

        }
    }
}
