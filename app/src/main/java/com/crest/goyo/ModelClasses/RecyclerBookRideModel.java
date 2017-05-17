package com.crest.goyo.ModelClasses;

/**
 * Created by cresttwo on 4/1/2016.
 */
public class RecyclerBookRideModel {

    String id,name,type,list_icon,active_icon,plotting_icon;

    public RecyclerBookRideModel(String id, String name, String type, String list_icon, String active_icon, String plotting_icon) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.list_icon = list_icon;
        this.active_icon = active_icon;
        this.plotting_icon = plotting_icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getList_icon() {
        return list_icon;
    }

    public void setList_icon(String list_icon) {
        this.list_icon = list_icon;
    }

    public String getActive_icon() {
        return active_icon;
    }

    public void setActive_icon(String active_icon) {
        this.active_icon = active_icon;
    }

    public String getPlotting_icon() {
        return plotting_icon;
    }

    public void setPlotting_icon(String plotting_icon) {
        this.plotting_icon = plotting_icon;
    }
}
