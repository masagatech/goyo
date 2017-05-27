package com.crest.goyo.ModelClasses;

/**
 * Created by mTech on 12-May-2017.
 */

import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.gson.annotations.SerializedName;

/**
 * Created by mTech on 02-May-2017.
 */
public class MyKidsTrips {

    @SerializedName("pid")
    public int id;

    @SerializedName("nm")
    public String nm;

    @SerializedName("date")
    public String date;

    @SerializedName("time")
    public String time;

    @SerializedName("btch")
    public String btch;

    @SerializedName("pd")
    public String pd;

    @SerializedName("sts")
    public String sts;

    @SerializedName("stsi")
    public String stsi;

    @SerializedName("stdsi")
    public String stdsi;

    @SerializedName("trpid")
    public String tripid;

    public int Type;

    public MyKidsTrips()
    {}
}
