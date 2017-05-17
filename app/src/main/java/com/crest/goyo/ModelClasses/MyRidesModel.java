package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class MyRidesModel {

    String id,user_v_name,e_status,vehicle_type,d_time,pickup_address,destination_address,final_amount,actual_distance,trip_time_in_min,d_start,d_end,days,hours,minute,seconds,v_ride_code;

    public MyRidesModel(String id, String user_v_name, String e_status, String vehicle_type, String d_time, String pickup_address, String destination_address, String final_amount, String actual_distance, String trip_time_in_min, String d_start, String d_end, String days, String hours, String minute, String seconds, String v_ride_code) {
        this.id = id;
        this.user_v_name = user_v_name;
        this.e_status = e_status;
        this.vehicle_type = vehicle_type;
        this.d_time = d_time;
        this.pickup_address = pickup_address;
        this.destination_address = destination_address;
        this.final_amount = final_amount;
        this.actual_distance = actual_distance;
        this.trip_time_in_min = trip_time_in_min;
        this.d_start = d_start;
        this.d_end = d_end;
        this.days = days;
        this.hours = hours;
        this.minute = minute;
        this.seconds = seconds;
        this.v_ride_code = v_ride_code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_v_name() {
        return user_v_name;
    }

    public void setUser_v_name(String user_v_name) {
        this.user_v_name = user_v_name;
    }

    public String getE_status() {
        return e_status;
    }

    public void setE_status(String e_status) {
        this.e_status = e_status;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getD_time() {
        return d_time;
    }

    public void setD_time(String d_time) {
        this.d_time = d_time;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getDestination_address() {
        return destination_address;
    }

    public void setDestination_address(String destination_address) {
        this.destination_address = destination_address;
    }

    public String getFinal_amount() {
        return final_amount;
    }

    public void setFinal_amount(String final_amount) {
        this.final_amount = final_amount;
    }

    public String getActual_distance() {
        return actual_distance;
    }

    public void setActual_distance(String actual_distance) {
        this.actual_distance = actual_distance;
    }

    public String getTrip_time_in_min() {
        return trip_time_in_min;
    }

    public void setTrip_time_in_min(String trip_time_in_min) {
        this.trip_time_in_min = trip_time_in_min;
    }

    public String getD_start() {
        return d_start;
    }

    public void setD_start(String d_start) {
        this.d_start = d_start;
    }

    public String getD_end() {
        return d_end;
    }

    public void setD_end(String d_end) {
        this.d_end = d_end;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

    public String getSeconds() {
        return seconds;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }

    public String getV_ride_code() {
        return v_ride_code;
    }

    public void setV_ride_code(String v_ride_code) {
        this.v_ride_code = v_ride_code;
    }
}
