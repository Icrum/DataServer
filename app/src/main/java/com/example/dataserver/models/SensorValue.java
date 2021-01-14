package com.example.dataserver.models;

import com.google.firebase.database.DataSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//
// Created by lourencogomes on 1/12/21.
//
public class SensorValue {

    String id;
    Float value;
    Date date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public SensorValue(String id, Float value, Date date) {
        this.id = id;
        this.value = value;
        this.date = date;
    }

    public SensorValue(Map.Entry<String, String> entry) {
        this.id = entry.getKey();
        this.value = Float.parseFloat(entry.getValue());
        this.date = new Date();
    }

    public SensorValue(DataSnapshot ds) {
        this.id = ds.getKey();
        this.value = Float.parseFloat((String)ds.child("Value").getValue());
        Iterator it = ds.getChildren().iterator();


        while(it.hasNext()){
            DataSnapshot d = (DataSnapshot) it.next();

            String key = d.getKey();
            if(key.compareTo( "Value")!=0) {
                Object  dateObj = d.getValue();
                this.date =  new Date((Long)dateObj);
            }

        }
    }




}
