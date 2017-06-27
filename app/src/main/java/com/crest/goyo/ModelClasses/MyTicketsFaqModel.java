package com.crest.goyo.ModelClasses;

/**
 * Created by crestsystem1 on 6/23/17.
 */

public class MyTicketsFaqModel {
    public String Id;
    public String Title;
    public String Detail;
    public String Itextbox;

    public MyTicketsFaqModel(String id, String title, String detail,String i_textbox1) {
        Id = id;
        Title = title;
        Detail = detail;
        Itextbox = i_textbox1;
    }

    public String getId() {
        return Id;
    }

    public String getItextbox() {
        return Itextbox;
    }

    public String getDetail() {
        return Detail;
    }

    public String getTitle() {
        return Title;
    }
}
