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

import com.crest.goyo.ModelClasses.MyKidsModel;
import com.crest.goyo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llc on 5/21/2017.
 */

public class MyKidsListAdapter extends BaseAdapter {

    List<MyKidsModel> list = new ArrayList<MyKidsModel>();
    LayoutInflater inflater;
    Context context;
    String _drop, _pickup;
    private static String headerText = "";
    public MyKidsListAdapter(Context context, List<MyKidsModel> lst) {
        this.list = lst;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
            convertView = inflater.inflate(R.layout.layout_mychild_rowlist, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        MyKidsModel md = list.get(position);

        mViewHolder.txtTitle.setText(md.Name);
        mViewHolder.txtTitle1.setText("Div : "  + md.Div);
        mViewHolder.txtTitle2.setText(md.School);

        return convertView;
    }


    private class MyViewHolder {
        private TextView txtTitle, txtTitle1, txtTitle2;
        public MyViewHolder(View item) {
            txtTitle = (TextView) item.findViewById(R.id.title);
            txtTitle1 = (TextView) item.findViewById(R.id.title1);
            txtTitle2 = (TextView) item.findViewById(R.id.title2);
        }
    }
}

