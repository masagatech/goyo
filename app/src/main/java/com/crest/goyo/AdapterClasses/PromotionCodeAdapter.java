package com.crest.goyo.AdapterClasses;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crest.goyo.ModelClasses.PromotionCodeModel;
import com.crest.goyo.R;

import java.util.List;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by brittany on 3/25/17.
 */

public class PromotionCodeAdapter extends RecyclerView.Adapter<PromotionCodeAdapter.MyView> {

    List<PromotionCodeModel> list;
    Context context;
    ClipboardManager myClipboard;
    ClipData myClip;

    public PromotionCodeAdapter(List<PromotionCodeModel> list) {
        this.list = list;
    }

    @Override
    public PromotionCodeAdapter.MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.promotion_code_list_items, parent, false);
        context = parent.getContext();
        return new PromotionCodeAdapter.MyView(view);
    }

    @Override
    public void onBindViewHolder(PromotionCodeAdapter.MyView holder, final int position) {
        holder.tv_title.setText("" + list.get(position).getTitle());
        holder.tv_description.setText("" + list.get(position).getDetail());


        String date = DateUtils.formatDateTime(context, Long.parseLong(list.get(position).getDate()), DateUtils.FORMAT_SHOW_DATE);
        holder.tv_date.setText("Valid till :- " + date);
        holder.tv_code.setText("Use Code : " + list.get(position).getCode());
//        holder.lay_promotion_code.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ReferralCode.class);
//                context.startActivity(intent);
//            }
//        });

        holder.bt_copy_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                String text = list.get(position).getCode();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);*/

                String text = list.get(position).getCode();
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Promotion Code", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Copy Code : " + text, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        TextView tv_title, tv_description, tv_date, tv_code;
        LinearLayout lay_promotion_code;
        Button bt_copy_code;


        public MyView(View itemView) {
            super(itemView);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_date = (TextView) itemView.findViewById(R.id.tv_date);
            lay_promotion_code = (LinearLayout) itemView.findViewById(R.id.lay_promotion_code);
            bt_copy_code = (Button) itemView.findViewById(R.id.bt_copy_code);
            tv_code = (TextView) itemView.findViewById(R.id.tv_code);

        }
    }
}
