package com.crest.goyo;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PaymentDetail extends AppCompatActivity implements View.OnClickListener {
    private TextView actionbar_title;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_payment_detail);

        initUI();
    }

    private void initUI() {

        actionbar_title = (TextView) findViewById(R.id.actionbar_title);

        actionbar_title.setText(R.string.actionbar_payment_detail);

        actionbar_title.setOnClickListener(this);

        builder = new AlertDialog.Builder(PaymentDetail.this, R.style.MyAlertDialogStyle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.actionbar_title:
                builder.setTitle("Ride Sucessfull");
                builder.setMessage("Your ride is sucessfuly done.");
                builder.setNegativeButton("RATE THIS RIDE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent rIntent = new Intent(getApplicationContext(), RateThisRide.class);
                        startActivity(rIntent);
                    }
                });
                builder.setCancelable(false);
                builder.setIcon(R.drawable.ic_correct);
                builder.show();
                break;

        }
    }
}
