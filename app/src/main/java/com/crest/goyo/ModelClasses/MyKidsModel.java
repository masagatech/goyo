package com.crest.goyo.ModelClasses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by llc on 5/21/2017.
 */

public class MyKidsModel {

    @SerializedName("studid")
    public int StudId = 0;

    @SerializedName("nm")
    public String Name = "";

    @SerializedName("div")
    public String  Div = "";

    @SerializedName("gen")
    public String Gen = "";

    @SerializedName("sch")
    public String School = "";

    @SerializedName("activated")
    public Boolean Activated = false;

}
