package com.crest.goyo.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by mTech on 13-May-2017.
 */

public class Global {
    //public static String DOMAIN_URL = "http://192.168.1.16:8081/goyoapi";
//    public final static String REST_URL = "http://192.168.43.10:8082/goyoapi";
//    public static final String SOCKET_URL = "http://192.168.43.10:8082/";

    //server
    public final static String REST_URL = "http://35.154.230.244:8082/goyoapi";
    public static final String SOCKET_URL = "http://35.154.230.244:8082/";

    public static File ExternalPath = Environment.getExternalStorageDirectory();
    public final static String Image_Path = "/goyo_images";


    public enum urls {
        getmykids("getmykids", REST_URL + "/cust/getmykids"),
        getlastknownloc("getlastknownloc", REST_URL + "/tripapi/getdelta"),
        activatekid("activatekid", REST_URL + "/cust/activatekid");

        public String key;
        public String value;

        private urls(String toKey, String toValue) {
            key = toKey;
            value = toValue;
        }

    }

    public final static String start = "1";
    public final static String done = "2";
    public final static String pause = "pause";
    public final static String cancel = "3";
    public final static String pending = "0";

    public final static String pickedupdrop = "1";
    public final static String absent = "2";
    //get Usrid
    public static String getUserID(Context c){
        return Preferences.getValue_String(c, Preferences.USER_ID);
    }

    public static ProgressDialog prgdialog;

    public static void showProgress(ProgressDialog prd) {
        prd.setCancelable(false);
        if (!prd.isShowing()) prd.show();
    }

    public static void hideProgress(ProgressDialog prd) {
        prd.dismiss();
    }


    public static Object cloneObject(Object obj){
        try{
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                if(field.get(obj) == null || Modifier.isFinal(field.getModifiers())){
                    continue;
                }
                if(field.getType().isPrimitive() || field.getType().equals(String.class)
                        || field.getType().getSuperclass().equals(Number.class)
                        || field.getType().equals(Boolean.class)){
                    field.set(clone, field.get(obj));
                }else{
                    Object childObj = field.get(obj);
                    if(childObj == obj){
                        field.set(clone, clone);
                    }else{
                        field.set(clone, cloneObject(field.get(obj)));
                    }
                }
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }
}
