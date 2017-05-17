package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class ChargesModel {

    String min_charge,base_fare,upto_km,upto_km_charge,after_km_charge,ride_time_pick_charge,ride_time_wait_charge,service_tax;

    public ChargesModel(String min_charge, String base_fare, String upto_km, String upto_km_charge, String after_km_charge, String ride_time_pick_charge, String ride_time_wait_charge, String service_tax) {
        this.min_charge = min_charge;
        this.base_fare = base_fare;
        this.upto_km = upto_km;
        this.upto_km_charge = upto_km_charge;
        this.after_km_charge = after_km_charge;
        this.ride_time_pick_charge = ride_time_pick_charge;
        this.ride_time_wait_charge = ride_time_wait_charge;
        this.service_tax = service_tax;
    }

    public String getMin_charge() {
        return min_charge;
    }

    public void setMin_charge(String min_charge) {
        this.min_charge = min_charge;
    }

    public String getBase_fare() {
        return base_fare;
    }

    public void setBase_fare(String base_fare) {
        this.base_fare = base_fare;
    }

    public String getUpto_km() {
        return upto_km;
    }

    public void setUpto_km(String upto_km) {
        this.upto_km = upto_km;
    }

    public String getUpto_km_charge() {
        return upto_km_charge;
    }

    public void setUpto_km_charge(String upto_km_charge) {
        this.upto_km_charge = upto_km_charge;
    }

    public String getAfter_km_charge() {
        return after_km_charge;
    }

    public void setAfter_km_charge(String after_km_charge) {
        this.after_km_charge = after_km_charge;
    }

    public String getRide_time_pick_charge() {
        return ride_time_pick_charge;
    }

    public void setRide_time_pick_charge(String ride_time_pick_charge) {
        this.ride_time_pick_charge = ride_time_pick_charge;
    }

    public String getRide_time_wait_charge() {
        return ride_time_wait_charge;
    }

    public void setRide_time_wait_charge(String ride_time_wait_charge) {
        this.ride_time_wait_charge = ride_time_wait_charge;
    }

    public String getService_tax() {
        return service_tax;
    }

    public void setService_tax(String service_tax) {
        this.service_tax = service_tax;
    }
}
