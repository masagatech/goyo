package com.crest.goyo.AdapterClasses;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crest.goyo.ModelClasses.WalletHistoryModel;
import com.crest.goyo.R;

import java.util.List;

/**
 * Created by brittany on 3/25/17.
 */

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.MyView> {

    List<WalletHistoryModel> list;
    Context context;

    public WalletHistoryAdapter(List<WalletHistoryModel> list) {
        this.list = list;
    }

    @Override
    public WalletHistoryAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_history_list_items, parent, false);
        context = parent.getContext();
        return new WalletHistoryAdapter.MyView(view);
    }

    @Override
    public void onBindViewHolder(WalletHistoryAdapter.MyView holder, final int position) {
        holder.tv_title.setText("" + list.get(position).getMessage());
        holder.tv_from.setText("From : " + list.get(position).getFrom());
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView tv_title, tv_from;


        public MyView(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_from = (TextView) itemView.findViewById(R.id.tv_from);

        }
    }
}
