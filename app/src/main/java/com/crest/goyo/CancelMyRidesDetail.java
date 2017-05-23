package com.crest.goyo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.TextView;

public class CancelMyRidesDetail extends AppCompatActivity {
    private TextView actionbar_title, tv_time, tv_pickup_from, tv_drop_loc, tv_reason, label_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_my_rides_detail);

        initUI();

        if(getIntent().getExtras()!=null){
            tv_pickup_from.setText(getIntent().getStringExtra("pickupAdd"));
            tv_drop_loc.setText(getIntent().getStringExtra("dropAdd"));
            String date =  DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("rideTime")), DateUtils.FORMAT_SHOW_DATE);
            String time = DateUtils.formatDateTime(getApplicationContext(), Long.parseLong(getIntent().getStringExtra("rideTime")), DateUtils.FORMAT_SHOW_TIME);
            tv_time.setText(date+ " AT "+time);
        }else {

        }
    }

    private void initUI() {
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_pickup_from = (TextView) findViewById(R.id.tv_pickup_from);
        tv_drop_loc = (TextView) findViewById(R.id.tv_drop_loc);
        tv_reason = (TextView) findViewById(R.id.tv_reason);
        label_reason = (TextView) findViewById(R.id.label_reason);
        actionbar_title.setText(R.string.nav_my_rides);
    }
}
