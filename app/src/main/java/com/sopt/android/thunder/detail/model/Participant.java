package com.sopt.android.thunder.detail.model;

/**
 * Created by inbiz02 on 2016-01-13.
 */
public class Participant {
    public String name;
    public String number;

    Participant(){
        
    }
    public Participant(String name){
        this.name = name;
        this.number = null;
    }
    Participant(String name, String number){
        this.name = name;
        this.number = number;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
