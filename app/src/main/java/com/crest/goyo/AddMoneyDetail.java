package com.crest.goyo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.crest.goyo.ModelClasses.PaymentModel;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.payUMoney.sdk.PayUmoneySdkInitilizer;
import com.payUMoney.sdk.SdkConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

public class AddMoneyDetail extends AppCompatActivity implements View.OnClickListener {

    private RadioButton rb_debit_card, rb_wallet;
    private EditText et_card, et_exp_date, et_cvv;
    private LinearLayout lay_card_detail, lay_wallet_detail;
    private TextView actionbar_title;
    private Button bt_add_money;
    private String mAmount;
    public static final String TAG = "AddMoneyDetails";
    List<PaymentModel> paymentMethodList = new ArrayList<>();
    PaymentModel paymentModel;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.add_money_detail);

        initUI();
        if (getIntent().getExtras() != null) {
            mAmount = getIntent().getStringExtra("addMoneyAmount");
        }

        bt_add_money.setText("ADD " + "\u20B9" + mAmount);
        et_exp_date.addTextChangedListener(new TextWatcher() {
            int prevL = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevL = et_exp_date.getText().toString().length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int length = editable.length();
                if ((prevL < length) && (length == 2)) {
                    editable.append("/");
                }
            }
        });
        rb_debit_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_wallet.setChecked(false);
                rb_debit_card.setChecked(true);
                lay_card_detail.setVisibility(View.VISIBLE);
                lay_wallet_detail.setVisibility(View.GONE);

            }
        });

        rb_wallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_wallet.setChecked(true);
                rb_debit_card.setChecked(false);
                lay_card_detail.setVisibility(View.GONE);
                lay_wallet_detail.setVisibility(View.VISIBLE);

            }
        });
        getPaymentMethods();
    }

    /*private void addMoneyApiCall(String paymentId) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_ADD_MONEY).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("f_amount", mAmount);
        urlBuilder.addQueryParameter("v_payment_mode", "payu");
        urlBuilder.addQueryParameter("transaction_id", paymentId);


        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClass.allRequest(AddMoneyDetail.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    String message = response.getString(VolleyTAG.message);
                    if (responce_status == VolleyTAG.response_status) {
                        JSONObject jsonObject = response.getJSONObject("data");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("addMoney", "sucessAddMoney");
                        startActivity(intent);
                    } else {

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }*/

    private void initUI() {
        rb_debit_card = (RadioButton) findViewById(R.id.rb_debit_card);
        rb_wallet = (RadioButton) findViewById(R.id.rb_wallet);
        lay_card_detail = (LinearLayout) findViewById(R.id.lay_card_detail);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        lay_wallet_detail = (LinearLayout) findViewById(R.id.lay_wallet_detail);
        et_card = (EditText) findViewById(R.id.et_card);
        et_cvv = (EditText) findViewById(R.id.et_cvv);
        et_exp_date = (EditText) findViewById(R.id.et_exp_date);
        bt_add_money = (Button) findViewById(R.id.bt_add_money);
        bt_add_money.setOnClickListener(this);
        actionbar_title.setText("ADD MONEY");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerPaymentMethods);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    //onClickListener (Do not find for the Click Listener its has been already set From XML)
    public void makePayment(View view) {
        String phone = paymentModel.getPhone();
        String productName = paymentModel.getProductName();
        String firstName = paymentModel.getFirstName();
        String txnId = "0nf7" + System.currentTimeMillis();
        String email = paymentModel.getEmail();
        String sUrl = paymentModel.getsUrl();
        String fUrl = paymentModel.getfUrl();
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        boolean isDebug = true;
        String key = paymentModel.getKey(); //need to be registered in the PayuMoney.com from wherer you will get the key
        String merchantId = paymentModel.getMerchantId(); // as above from where you can get the MerchantID
        PayUmoneySdkInitilizer.PaymentParam.Builder builder = new PayUmoneySdkInitilizer.PaymentParam.Builder();
        builder.setAmount(getAmount())
                .setTnxId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(sUrl)
                .setfUrl(fUrl)
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setIsDebug(isDebug)
                .setKey(key)
                .setMerchantId(merchantId);
        PayUmoneySdkInitilizer.PaymentParam paymentParam = builder.build();
        calculateServerSideHashAndInitiatePayment(paymentParam);
    }

    private void calculateServerSideHashAndInitiatePayment(final PayUmoneySdkInitilizer.PaymentParam paymentParam) {

        // Replace your server side hash generator API URL
        String url = "https://test.payumoney.com/payment/op/calculateHashForTest";

        Toast.makeText(this, "Please wait... Generating hash from server ... ", Toast.LENGTH_LONG).show();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has(SdkConstants.STATUS)) {
                        String status = jsonObject.optString(SdkConstants.STATUS);
                        if (status != null || status.equals("1")) {

                            String hash = jsonObject.getString(SdkConstants.RESULT);
                            Log.i("app_activity", "Server calculated Hash :  " + hash);

                            paymentParam.setMerchantHash(hash);

                            PayUmoneySdkInitilizer.startPaymentActivityForResult(AddMoneyDetail.this, paymentParam);
                        } else {
                            Toast.makeText(AddMoneyDetail.this,
                                    jsonObject.getString(SdkConstants.RESULT),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                if (error instanceof NoConnectionError) {
                    Toast.makeText(AddMoneyDetail.this,
                            AddMoneyDetail.this.getString(R.string.connect_to_internet),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddMoneyDetail.this,
                            error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return paymentParam.getParams();
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private double getAmount() {
        Double amount = Double.valueOf(mAmount);
        if (isDouble(String.valueOf(amount))) {
            amount = Double.parseDouble(String.valueOf(amount));
            return amount;
        } else {
            Toast.makeText(getApplicationContext(), "Paying Default Amount ₹10", Toast.LENGTH_LONG).show();
            return amount;
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PayUmoneySdkInitilizer.PAYU_SDK_PAYMENT_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Success - Payment ID : " + data.getStringExtra(SdkConstants.PAYMENT_ID));
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                addMoneyApiCall(paymentId);
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "failure");
                showDialogMessage("cancelled");
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_FAILED) {
                Log.i("app_activity", "failure");
                String paymentId = data.getStringExtra(SdkConstants.PAYMENT_ID);
                if (data != null) {
                    if (data.getStringExtra(SdkConstants.RESULT).equals("cancel")) {
                    } else {
                        showDialogMessage("failure");
                    }
                }
                //Write your code if there's no result
            } else if (resultCode == PayUmoneySdkInitilizer.RESULT_BACK) {
                Log.i(TAG, "User returned without login");
                showDialogMessage("User returned without login");
            }
        }
    }

    private void showDialogMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(TAG);
        builder.setMessage(message);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_money:
                makePayment(v);
                break;
        }
    }

    private void getPaymentMethods() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.GET_PAYMENT_METHODS).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(this, Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(this, Preferences.USER_AUTH_TOKEN));
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        final okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        JSONArray dataArr = response.getJSONArray("data");
                        for (int i = 0; i < dataArr.length(); i++) {
                            paymentModel = new PaymentModel();
                            JSONObject data = dataArr.getJSONObject(i);
                            JSONObject ldata = data.getJSONObject("l_data");
                            paymentModel.setId(data.getString("id"));
                            paymentModel.setV_name(data.getString("v_name"));
                            paymentModel.setV_type(data.getString("v_type"));
                            paymentModel.setV_mode(data.getString("v_mode"));
                            paymentModel.setV_image(data.getString("v_image"));
                            paymentModel.setKey(ldata.getString("key"));
                            paymentModel.setfUrl(ldata.getString("fUrl"));
                            paymentModel.setsUrl(ldata.getString("sUrl"));
                            paymentModel.setEmail(ldata.getString("email"));
                            paymentModel.setPhone(ldata.getString("phone"));
                            paymentModel.setFirstName(ldata.getString("firstName"));
                            paymentModel.setMerchantId(ldata.getString("merchantId"));
                            paymentModel.setProductName(ldata.getString("productName"));
                            paymentMethodList.add(paymentModel);
                        }
                        CustomAdapter customAdapter = new CustomAdapter();
                        recyclerView.setAdapter(customAdapter);
                    } else {
                        Log.e("getPaymentMethods", "onResult: Null Response");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addMoneyApiCall(String paymentId) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.ADD_MONEY).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(this, Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(this, Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("f_amount", mAmount);
        urlBuilder.addQueryParameter("v_payment_type", paymentModel.getV_type());
        urlBuilder.addQueryParameter("transaction_id", paymentId);
        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        final okhttp3.Request request = new okhttp3.Request.Builder().url(newurl).build();
        VolleyRequestClassNew.allRequest(this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        Toast.makeText(AddMoneyDetail.this, "Money Added Successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "onResult: Null Response");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AddMoneyDetail.this).inflate(R.layout.layout_addmoney_adpter, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CustomAdapter.MyViewHolder holder, int position) {
            Log.e("Name", "onBindViewHolder: " + paymentMethodList.get(position).getV_name());
            holder.txtAddMoneyType.setText(paymentMethodList.get(position).getV_name());
            Glide.with(AddMoneyDetail.this).load(paymentMethodList.get(position).getV_image().replaceAll("\'", "")).into(holder.imgAddMoneyType);
        }

        @Override
        public int getItemCount() {
            return paymentMethodList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtAddMoneyType;
            ImageView imgAddMoneyType;

            public MyViewHolder(View itemView) {
                super(itemView);
                txtAddMoneyType = (TextView) itemView.findViewById(R.id.txtAddMoneyType);
                imgAddMoneyType = (ImageView) itemView.findViewById(R.id.imgAddMoneyType);
            }
        }
    }
}
