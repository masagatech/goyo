package com.crest.goyo.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    public static final String FORGOT_PASSWORD_OTP = "FORGOT_PASSWORD_OTP";
    public static final String FORGOT_PASSWORD_EMAIL = "FORGOT_PASSWORD_EMAIL";
    public static final String USER_ID = "USER_ID";
    public static final String USER_AUTH_TOKEN = "USER_AUTH_TOKEN";
    public static final String RIDE_ID = "RIDE_ID";
    public static final String DRIVER_ID = "DRIVER_ID";
    public static final String ADD_MONEY = "ADD_MONEY";
    public static final String NO_VEHICLES = "NO_VEHICLES";
    public static final String VEHICLES_IMG = "VEHICLES_IMG";
    public static final String V_ID = "V_ID";
    public static final String CITY = "CITY";


    public static void setValue(Context context, String Key, String Value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Key, Value);
        editor.commit();
    }

    public static String getValue_String(Context context, String Key) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(Key, "");
    }
}