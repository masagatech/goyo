package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crest.goyo.ModelClasses.NotificationsModel;
import com.crest.goyo.NotificationInfo;
import com.crest.goyo.R;

import java.util.List;

/**
 * Created by brittany on 3/25/17.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyView> {

    List<NotificationsModel> list;
    Context context;

    public NotificationsAdapter(List<NotificationsModel> list) {
        this.list = list;
    }

    @Override
    public NotificationsAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_list_items, parent, false);
        context = parent.getContext();
        return new NotificationsAdapter.MyView(view);
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.MyView holder, final int position) {


        holder.tv_title.setText("" + list.get(position).getTitle());
        holder.tv_detail.setText(list.get(position).getDetail());


        holder.lay_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NotificationInfo.class);
                intent.putExtra("notifId",list.get(position).getId());
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView tv_title, tv_detail;
        LinearLayout lay_notif;


        public MyView(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_detail = (TextView) itemView.findViewById(R.id.tv_detail);
            lay_notif = (LinearLayout) itemView.findViewById(R.id.lay_notif);

        }
    }
}
