package com.crest.goyo.VolleyLibrary;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.crest.goyo.Utils.CustomDialog;

import org.json.JSONObject;

/**
 * Created by brittany on 4/4/17.
 */

public class VolleyRequestClass {

    public static void allRequest(final Context applicationContext, final String newurl, final RequestInterface requestInterface, final boolean b) {
        final CustomDialog customDialog = new CustomDialog(applicationContext);
        if (b) customDialog.show();
        RequestQueue queue = Volley.newRequestQueue(applicationContext);
        new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, newurl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            requestInterface.onResult(response);
                            Log.d(applicationContext.getClass().getName(), "" + newurl);
                            Log.d(applicationContext.getClass().getName(), "" + response);
                            if (b) customDialog.hide();
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                            if (b) customDialog.hide();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (b) customDialog.hide();
                        VolleyLog.d("VolleyLog:", "" + error);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        postRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(postRequest);
    }


}
