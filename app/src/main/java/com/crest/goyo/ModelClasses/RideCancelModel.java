package com.crest.goyo.ModelClasses;

/**
 * Created by annie on 15/4/17.
 */

public class RideCancelModel {

    String tv_req_can,rb_req_can,mRideId;

    public RideCancelModel(String tv_req_can, String mRideId) {
        this.tv_req_can = tv_req_can;
        this.mRideId = mRideId;
    }

    public String getTv_req_can() {
        return tv_req_can;
    }

    public void setTv_req_can(String tv_req_can) {
        this.tv_req_can = tv_req_can;
    }

    public String getmRideId() {
        return mRideId;
    }

    public void setmRideId(String mRideId) {
        this.mRideId = mRideId;
    }
}
