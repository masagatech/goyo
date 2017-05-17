package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class VehicleListModel {

    String id,i_driver_id,v_type;
    double l_latitude,l_longitude;
    int distance;

    public VehicleListModel(String id, String i_driver_id, String v_type, double l_latitude, double l_longitude, int distance) {
        this.id = id;
        this.i_driver_id = i_driver_id;
        this.v_type = v_type;
        this.l_latitude = l_latitude;
        this.l_longitude = l_longitude;
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getI_driver_id() {
        return i_driver_id;
    }

    public void setI_driver_id(String i_driver_id) {
        this.i_driver_id = i_driver_id;
    }

    public String getV_type() {
        return v_type;
    }

    public void setV_type(String v_type) {
        this.v_type = v_type;
    }

    public double getL_latitude() {
        return l_latitude;
    }

    public void setL_latitude(double l_latitude) {
        this.l_latitude = l_latitude;
    }

    public double getL_longitude() {
        return l_longitude;
    }

    public void setL_longitude(double l_longitude) {
        this.l_longitude = l_longitude;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
