package com.sopt.android.thunder.detail.model;

import java.io.Serializable;

/**
 * Created by Yujin on 2016-02-29.
 */
public class Content_notice implements Serializable{
    int groupid;
    int check; // 0,1 ; 0이면 안중요, 1이면 중요
    String title;
    String context;
    String date;

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

}
