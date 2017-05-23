package com.crest.goyo.ModelClasses;

/**
 * Created by mTech on 13-May-2017.
 */

import com.google.gson.annotations.SerializedName;

public class model_tripdata {

    @SerializedName("tripid")
    public String tripid;

    @SerializedName("loc")
    public String[] loc;

    @SerializedName("bearing")
    public String bearing;

    @SerializedName("sertm")
    public String sertm;

    @SerializedName("speed")
    public String speed;
}
