
package com.crest.goyo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.crest.goyo.ModelClasses.PaymentModel;
import com.crest.goyo.Utils.Constant;
import com.crest.goyo.Utils.Preferences;
import com.crest.goyo.VolleyLibrary.RequestInterface;
import com.crest.goyo.VolleyLibrary.VolleyRequestClass;
import com.crest.goyo.VolleyLibrary.VolleyRequestClassNew;
import com.crest.goyo.VolleyLibrary.VolleyTAG;
import com.payu.india.CallBackHandler.OnetapCallback;
import com.payu.india.Extras.PayUChecksum;
import com.payu.india.Interfaces.OneClickPaymentListener;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.HttpUrl;

public class AddMoneyDetail extends AppCompatActivity implements View.OnClickListener, OneClickPaymentListener {

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
    private String merchantKey, userCredentials;
    private PaymentParams mPaymentParams;
    private PayuConfig payuConfig;
    private PayUChecksum checksum;
    String txnId;

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


        //TODO Must write this code if integrating One Tap payments
        OnetapCallback.setOneTapCallback(this);
        //TODO Must write below code in your activity to set up initial context for PayU
        Payu.setInstance(this);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                addMoneyApiCall(txnId);
            } else {
                Toast.makeText(this, getString(R.string.could_not_receive_data), Toast.LENGTH_LONG).show();
            }
        }
    }

    // This method prepares all the payments params to be sent to PayuBaseActivity.java
    public void navigateToBaseActivity(View view) {

        merchantKey = paymentModel.getKey();
        String amount = mAmount;
        String email = paymentModel.getEmail();
        int environment = PayuConstants.PRODUCTION_ENV;
        userCredentials = merchantKey + ":" + email;
        //TODO Below are mandatory params for hash genetation
        mPaymentParams = new PaymentParams();
        mPaymentParams.setKey(merchantKey);
        mPaymentParams.setAmount(amount);
        mPaymentParams.setProductInfo(paymentModel.getProductName());
        mPaymentParams.setFirstName(paymentModel.getFirstName());
        mPaymentParams.setEmail(paymentModel.getEmail());
        txnId = String.valueOf(System.currentTimeMillis());
        mPaymentParams.setTxnId(txnId);
        mPaymentParams.setSurl(paymentModel.getsUrl());
        mPaymentParams.setFurl(paymentModel.getfUrl());
        mPaymentParams.setUdf1("");
        mPaymentParams.setUdf2("");
        mPaymentParams.setUdf3("");
        mPaymentParams.setUdf4("");
        mPaymentParams.setUdf5("");

        mPaymentParams.setUserCredentials(userCredentials);

        payuConfig = new PayuConfig();
        payuConfig.setEnvironment(environment);

        generateParamsForHash();

    }

    private void generateParamsForHash() {
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if (null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
        Log.w("POST PARAMS", "generateParamsForHash: " + postParams);
        generateHashFromMyServer();
    }

    private void generateHashFromMyServer() {
        RequestQueue requestQueue = Volley.newRequestQueue(AddMoneyDetail.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.GET_PayUBiz_Hash, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getInt("status") == 1) {
                        Log.w("Response Payment", "onResult: " + response);
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        PayuHashes payuHashes = new PayuHashes();
                        payuHashes.setPaymentHash(dataObj.getString("payment_hash"));
                        payuHashes.setVasForMobileSdkHash(dataObj.getString("vas_for_mobile_sdk_hash"));
                        payuHashes.setPaymentRelatedDetailsForMobileSdkHash(dataObj.getString("payment_related_details_for_mobile_sdk_hash"));
                        payuHashes.setDeleteCardHash(dataObj.getString("delete_user_card_hash"));
                        payuHashes.setStoredCardsHash(dataObj.getString("get_user_cards_hash"));
                        payuHashes.setEditCardHash(dataObj.getString("edit_user_card_hash"));
                        payuHashes.setSaveCardHash(dataObj.getString("save_user_card_hash"));
                        launchSdkUI(payuHashes);
                    } else {
                        Log.e("getPaymentMethods", "onResult: Null Response");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> param = new HashMap<>();
                param.put("device", "ANDROID");
                param.put("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
                param.put("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
                param.put("txnid", txnId);
                param.put("amount", mAmount);
                param.put("productinfo", paymentModel.getProductName());
                param.put("firstname", paymentModel.getFirstName());
                param.put("email", paymentModel.getEmail());
                param.put("user_credentials", userCredentials);
                return param;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }


    public void launchSdkUI(PayuHashes payuHashes) {

        Intent intent = new Intent(this, PayUBaseActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        //fetchMerchantHashes(intent);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);

    }

    private void storeMerchantHash(String cardToken, String merchantHash) {

        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials + "&card_token=" + cardToken + "&merchant_hash=" + merchantHash;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    //TODO Deploy a file on your server for storing cardToken and merchantHash nad replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/store_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }

    private void fetchMerchantHashes(final Intent intent) {
        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        final Intent baseActivityIntent = intent;
        new AsyncTask<Void, Void, HashMap<String, String>>() {

            @Override
            protected HashMap<String, String> doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                    InputStream responseInputStream = conn.getInputStream();
                    StringBuffer responseStringBuffer = new StringBuffer();
                    byte[] byteContainer = new byte[1024];
                    for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                        responseStringBuffer.append(new String(byteContainer, 0, i));
                    }

                    JSONObject response = new JSONObject(responseStringBuffer.toString());

                    HashMap<String, String> cardTokens = new HashMap<String, String>();
                    JSONArray oneClickCardsArray = response.getJSONArray("data");
                    int arrayLength;
                    if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                        for (int i = 0; i < arrayLength; i++) {
                            cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                        }
                        return cardTokens;
                    }
                    // pass these to next activity

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(HashMap<String, String> oneClickTokens) {
                super.onPostExecute(oneClickTokens);

                baseActivityIntent.putExtra(PayuConstants.ONE_CLICK_CARD_TOKENS, oneClickTokens);
                startActivityForResult(baseActivityIntent, PayuConstants.PAYU_REQUEST_CODE);
            }
        }.execute();
    }

    private void deleteMerchantHash(String cardToken) {

        final String postParams = "card_token=" + cardToken;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    //TODO Replace below url with your server side file url.
                    URL url = new URL("https://payu.herokuapp.com/delete_merchant_hash");

                    byte[] postParamsByte = postParams.getBytes("UTF-8");

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                    conn.setDoOutput(true);
                    conn.getOutputStream().write(postParamsByte);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                this.cancel(true);
            }
        }.execute();
    }

    //TODO This method is used only if integrating One Tap Payments
    public HashMap<String, String> getAllOneClickHashHelper(String merchantKey, String userCredentials) {

        // now make the api call.
        final String postParams = "merchant_key=" + merchantKey + "&user_credentials=" + userCredentials;
        HashMap<String, String> cardTokens = new HashMap<String, String>();

        try {
            //TODO Replace below url with your server side file url.
            URL url = new URL("https://payu.herokuapp.com/get_merchant_hashes");

            byte[] postParamsByte = postParams.getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
            conn.setDoOutput(true);
            conn.getOutputStream().write(postParamsByte);

            InputStream responseInputStream = conn.getInputStream();
            StringBuffer responseStringBuffer = new StringBuffer();
            byte[] byteContainer = new byte[1024];
            for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                responseStringBuffer.append(new String(byteContainer, 0, i));
            }

            JSONObject response = new JSONObject(responseStringBuffer.toString());

            JSONArray oneClickCardsArray = response.getJSONArray("data");
            int arrayLength;
            if ((arrayLength = oneClickCardsArray.length()) >= 1) {
                for (int i = 0; i < arrayLength; i++) {
                    cardTokens.put(oneClickCardsArray.getJSONArray(i).getString(0), oneClickCardsArray.getJSONArray(i).getString(1));
                }

            }
            // pass these to next activity

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardTokens;
    }


    private void addMoneyApiCall(String paymentId) {
        Log.e("TAG", "addMoneyApiCall call");
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constant.URL_ADD_MONEY).newBuilder();
        urlBuilder.addQueryParameter("device", "ANDROID");
        urlBuilder.addQueryParameter("lang", "en");
        urlBuilder.addQueryParameter("login_id", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID));
        urlBuilder.addQueryParameter("v_token", Preferences.getValue_String(getApplicationContext(), Preferences.USER_AUTH_TOKEN));
        urlBuilder.addQueryParameter("f_amount", mAmount);
        urlBuilder.addQueryParameter("v_payment_type", paymentModel.getV_type());
        urlBuilder.addQueryParameter("transaction_id", paymentId);

        String url = urlBuilder.build().toString();
        String newurl = url.replaceAll(" ", "%20");
        VolleyRequestClass.allRequest(AddMoneyDetail.this, newurl, new RequestInterface() {
            @Override
            public void onResult(JSONObject response) {
                try {
                    int responce_status = response.getInt(VolleyTAG.status);
                    if (responce_status == VolleyTAG.response_status) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("addMoney", "sucessAddMoney");
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), AddMoney.class);
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, true);
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_money:
                navigateToBaseActivity(v);
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
                            paymentModel.setfUrl(ldata.getString("fUrl").replaceAll("\'",""));
                            paymentModel.setsUrl(ldata.getString("sUrl").replaceAll("\'",""));
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

    @Override
    public HashMap<String, String> getAllOneClickHash(String userCredentials) {
        return getAllOneClickHashHelper(merchantKey, userCredentials);
    }

    @Override
    public void getOneClickHash(String cardToken, String merchantKey, String userCredentials) {

    }

    @Override
    public void saveOneClickHash(String cardToken, String oneClickHash) {
        // 1. POST http request to your server
        // POST params - merchant_key, user_credentials,card_token,merchant_hash.
        // 2. In this POST method the oneclickhash is stored corresponding to card token in merchant server.
        // this is a sample code for storing one click hash on merchant server.

        storeMerchantHash(cardToken, oneClickHash);
    }

    @Override
    public void deleteOneClickHash(String cardToken, String userCredentials) {
        deleteMerchantHash(cardToken);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
        int row_index;

        @Override
        public CustomAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(AddMoneyDetail.this).inflate(R.layout.layout_addmoney_adpter, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CustomAdapter.MyViewHolder holder, final int position) {
            holder.txtAddMoneyType.setText(paymentMethodList.get(position).getV_name());
            Log.e("getV_name", "onBindViewHolder: " + paymentMethodList.get(position).getV_name());
            Glide.with(AddMoneyDetail.this).load(paymentMethodList.get(position).getV_image().replaceAll("\'", "")).into(holder.imgAddMoneyType);
            holder.layout_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.layout_payment.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    Log.e("Selected Payment Method", "onClick: " + paymentMethodList.get(position).getV_name());
                }
            });
            holder.layout_payment.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onClick(View v) {
                    row_index = position;
                    notifyDataSetChanged();
                }
            });
            if (row_index == position) {
                holder.layout_payment.setBackgroundResource(R.color.colorPrimary);
            } else {
                holder.layout_payment.setBackgroundResource(R.color.colorWhite);
            }
        }

        @Override
        public int getItemCount() {
            return paymentMethodList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView txtAddMoneyType;
            ImageView imgAddMoneyType;
            LinearLayout layout_payment;

            public MyViewHolder(View itemView) {
                super(itemView);
                txtAddMoneyType = (TextView) itemView.findViewById(R.id.txtAddMoneyType);
                imgAddMoneyType = (ImageView) itemView.findViewById(R.id.imgViewAddMoneyType);
                layout_payment = (LinearLayout) itemView.findViewById(R.id.layout_payments_methods);
            }
        }
    }
}
