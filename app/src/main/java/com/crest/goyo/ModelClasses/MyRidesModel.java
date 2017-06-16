package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class MyRidesModel {

    String id,status,ride_time,vehicle_type,pickup_address,destination_address,driver_name,v_ride_code,driver_v_id;

  /*  public MyRidesModel(String id, String status, String ride_time, String vehicle_type, String pickup_address, String destination_address, String driver_name) {
        this.id = id;
        this.status = status;
        this.ride_time = ride_time;
        this.vehicle_type = vehicle_type;
        this.pickup_address = pickup_address;
        this.destination_address = destination_address;
        this.driver_name = driver_name;
    }*/

    public MyRidesModel(String id, String status, String ride_time, String vehicle_type, String pickup_address, String destination_address, String driver_name, String v_ride_code, String driver_v_id) {
        this.id = id;
        this.status = status;
        this.ride_time = ride_time;
        this.vehicle_type = vehicle_type;
        this.pickup_address = pickup_address;
        this.destination_address = destination_address;
        this.driver_name = driver_name;
        this.v_ride_code = v_ride_code;
        this.driver_v_id = driver_v_id;
    }

    public String getDriver_v_id() {
        return driver_v_id;
    }

    public void setDriver_v_id(String driver_v_id) {
        this.driver_v_id = driver_v_id;
    }

    public String getId() {
        return id;
    }

    public String getV_ride_code() {
        return v_ride_code;
    }

    public void setV_ride_code(String v_ride_code) {
        this.v_ride_code = v_ride_code;
    }

    /*public MyRidesModel(String id, String status, String ride_time, String vehicle_type, String pickup_address, String destination_address, String driver_name, String v_ride_code) {
        this.id = id;
        this.status = status;
        this.ride_time = ride_time;
        this.vehicle_type = vehicle_type;
        this.pickup_address = pickup_address;
        this.destination_address = destination_address;
        this.driver_name = driver_name;
        this.v_ride_code = v_ride_code;
    }*/

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRide_time() {
        return ride_time;
    }

    public void setRide_time(String ride_time) {
        this.ride_time = ride_time;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
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

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }
}
