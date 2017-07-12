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

import org.json.JSONObject;

/**
 * Created by annie on 20/4/17.
 */

public class VolleyRequestClassNew {

    public static void allRequest(final Context applicationContext, final String newurl, final RequestInterface requestInterface) {

        RequestQueue queue = Volley.newRequestQueue(applicationContext);
        new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, newurl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            requestInterface.onResult(response);
                            Log.d(applicationContext.getClass().getName(), "" + newurl);
                            Log.d(applicationContext.getClass().getName(), "" + response);
                        } else {
                            Log.e("ServiceHandler", "Couldn't get any data from the url");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("VolleyLog:", "" + error);
                    }
                }
        );
        postRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(postRequest);
    }
}
