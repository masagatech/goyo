package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class PromotionCodeModel {

    String title,code,date,detail;

    public PromotionCodeModel(String title, String code, String date, String detail) {
        this.title = title;
        this.code = code;
        this.date = date;
        this.detail = detail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
