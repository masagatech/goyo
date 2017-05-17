package com.crest.goyo.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by jasson on 27/6/16.
 */
public class Constant {

    //    public static String BASE_URL = "http://192.168.0.222:3000/api/";
//    http://35.154.123.76:8081/api/api-list
    public static String BASE_URL = "http://35.154.230.244:8081/api/";
    public static final String URL_SIGNUP = BASE_URL + "userSignUp";
    public static final String URL_LOGIN = BASE_URL + "userLogin";
    public static final String URL_CHANGE_PASSWORD = BASE_URL + "userPasswordUpdate";
    public static final String URL_GET_USER_PROFILE = BASE_URL + "userProfileGet";
    public static final String URL_UPDATE_USER_PROFILE = BASE_URL + "userProfileUpdate";
    public static final String URL_FORGOT_PASSWORD = BASE_URL + "userForgotPassword";
    public static final String URL_RESET_PASSWORD = BASE_URL + "userResetPassword";
    public static final String URL_GET_VEHICLE_TYPES = BASE_URL + "getVehicleTypes";
    public static final String URL_GIVE_FEEDBACK = BASE_URL + "userFeedback";
    public static final String URL_GET_VEHICLE_LIST = BASE_URL + "getVehiclesList";
    public static final String URL_GET_USER_RIDES = BASE_URL + "getUserRides";
    public static final String URL_SAVE_RIDE = BASE_URL + "saveRide";
    public static final String URL_LOGOUT = BASE_URL + "logout";
    public static final String URL_GET_VEHICLE_CHARGE = BASE_URL + "getVehicleTypeCharge";
    public static final String URL_GET_TERIFF_CARD = BASE_URL + "getTeriffCard";
    public static final String URL_GET_RIDE = BASE_URL + "getRide";
    public static final String URL_CITY_TYPE = BASE_URL + "getCities";
    public static final String URL_CONFIRM_RIDE = BASE_URL + "confirmRide";
    public static final String URL_REFERRAL_CODE = BASE_URL + "getReferralCode";
    public static final String URL_USER_RIDE_DETAIL = BASE_URL + "getUserRideDetails";
    public static final String URL_USER_WALLET = BASE_URL + "getUserWallet";
    public static final String URL_RIDE_RATE = BASE_URL + "rideRate";
    public static final String URL_GET_RIDE_CANCEL = BASE_URL + "getRideCancelReasons";
    public static final String URL_USER_RIDE_CANCEL = BASE_URL + "cancelRide";
    public static final String URL_GET_DRIVER_LOCATIOIN = BASE_URL + "getDriverLocation";
    public static final String URL_GET_PROMOTION_CODES = BASE_URL + "getPromotionCodes";
    public static final String URL_APPLY_PROMOCODE = BASE_URL + "promotionCodeExists";
    public static final String URL_ADD_MONEY = BASE_URL + "addMoney";
    public static final String URL_GET_NOTIF = BASE_URL + "getNotifications";
    public static final String URL_GET_NOTIF_INFO = BASE_URL + "getNotificationInfo";
    public static final String URL_RIDE_SOS = BASE_URL + "rideSOS";
    public static final String URL_RIDE_PAYMENT = BASE_URL + "ridePayment";
    public static final String URL_TERMS_COND = BASE_URL + "getCms";
    public static final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public static boolean CHECK_GPS = true;

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            Toast.makeText(c, "No internet connection.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static Bitmap setMarkerPin(Context context, int pin){
        BitmapDrawable bitmapdraw = (BitmapDrawable) context.getResources().getDrawable(pin);
        Bitmap bitmap = bitmapdraw.getBitmap();
        return Bitmap.createScaledBitmap(bitmap, 70, 70, false);
    }





}

