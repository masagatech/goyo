package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crest.goyo.ModelClasses.RecyclerBookRideModel;
import com.crest.goyo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brittany on 3/25/17.
 */

public class RecyclerBookRidesAdapter extends RecyclerView.Adapter<RecyclerBookRidesAdapter.MyView> {

    List<RecyclerBookRideModel> list;
    Context context;
    boolean color =true;
    ArrayList<Boolean> isSelected = new ArrayList<>();
    int row_index;

    public RecyclerBookRidesAdapter(List<RecyclerBookRideModel> list) {
        this.list = list;
    }

    @Override
    public RecyclerBookRidesAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_rides_recycler_list_items, parent, false);
        context = parent.getContext();
        return new RecyclerBookRidesAdapter.MyView(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerBookRidesAdapter.MyView holder, final int position) {
        holder.tv_type.setText("" + list.get(position).getName());
        isSelected.add(true);

        holder.lay_my_rides.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                row_index=position;
                notifyDataSetChanged();
            }
        });
        Glide.with(context).load(list.get(position).getList_icon())
                .crossFade()
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_vehicle);
        if(row_index==position){
            holder.lay_my_rides.setBackgroundResource(R.color.colorPrimary);
        }
        else
        {
            holder.lay_my_rides.setBackgroundResource(R.color.colorWhite);
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView tv_type, tv_time;
        LinearLayout lay_my_rides;
        ImageView iv_vehicle;


        public MyView(View itemView) {
            super(itemView);
            tv_type = (TextView) itemView.findViewById(R.id.tv_type);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            lay_my_rides = (LinearLayout) itemView.findViewById(R.id.lay_my_rides);
            iv_vehicle=(ImageView)itemView.findViewById(R.id.iv_vehicle);

        }
    }
}
