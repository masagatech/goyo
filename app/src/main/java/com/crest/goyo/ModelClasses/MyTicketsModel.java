package com.crest.goyo.ModelClasses;

/**
 * Created by crestsystem1 on 6/23/17.
 */

public class MyTicketsModel {
    public String Id;
    public String Status;
    public String Date;
    public String Detail;
    public String Title;

    public MyTicketsModel(String id, String status, String date, String title, String detail) {
        Id = id;
        Status = status;
        Date = date;
        Detail = detail;
        Title = title;
    }

    public String getId() {
        return Id;
    }

    public String getStatus() {
        return Status;
    }

    public String getDate() {
        return Date;
    }

    public String getDetail() {
        return Detail;
    }

    public String getTitle() {
        return Title;
    }
}
