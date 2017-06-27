package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crest.goyo.ModelClasses.MyTicketsModel;
import com.crest.goyo.R;
import com.crest.goyo.Utils.FileUtils;

import java.util.ArrayList;

/**
 * Created by brittany on 3/25/17.
 */

public class MyTicketsAdapter extends RecyclerView.Adapter<MyTicketsAdapter.MyView> {

    ArrayList<MyTicketsModel> mMyTickets;
    Context context;

    public MyTicketsAdapter(Context context,ArrayList<MyTicketsModel> mMyTickets) {
        this.context = context;
        this.mMyTickets = mMyTickets;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_my_tickets, parent, false);
        context = parent.getContext();
        return new MyView(view);
    }

    @Override
    public void onBindViewHolder(MyView holder, final int position) {


        holder.txt_ticket_id.setText("Id : " + mMyTickets.get(position).getId());
        holder.txt_date.setText("" + FileUtils.setDate(context,mMyTickets.get(position).getDate()));
        holder.txt_status.setText("" + mMyTickets.get(position).getStatus());
        holder.txt_detail.setText("" + mMyTickets.get(position).getDetail());
        holder.txt_title.setText(""+mMyTickets.get(position).getTitle());

    }

    @Override
    public int getItemCount() {
        return mMyTickets.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView txt_ticket_id;
        TextView txt_status;
        TextView txt_date;
        TextView txt_title;
        TextView txt_detail;

        public MyView(View itemView) {
            super(itemView);
            txt_ticket_id = (TextView) itemView.findViewById(R.id.txt_ticket_id);
            txt_status = (TextView) itemView.findViewById(R.id.txt_status);
            txt_date = (TextView) itemView.findViewById(R.id.txt_date);
            txt_title = (TextView) itemView.findViewById(R.id.txt_title);
            txt_detail = (TextView) itemView.findViewById(R.id.txt_detail);

        }
    }
}
