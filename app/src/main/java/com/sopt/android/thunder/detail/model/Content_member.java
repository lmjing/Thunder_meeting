package com.sopt.android.thunder.detail.model;

import java.io.Serializable;

/**
 * Created by Yujin on 2016-03-06.
 */
public class Content_member implements Serializable {
    int groupid;
    String id;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupid() {
        return groupid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }
}
