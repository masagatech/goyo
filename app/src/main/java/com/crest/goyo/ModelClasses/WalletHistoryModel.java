package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class WalletHistoryModel {

    String message,from;

    public WalletHistoryModel(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
