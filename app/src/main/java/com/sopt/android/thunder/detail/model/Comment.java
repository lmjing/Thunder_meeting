package com.sopt.android.thunder.detail.model;

/**
 * Created by lmjin_000 on 2016-02-08.
 */
public class Comment {
    String id;
    int thunderid;
    String contents;
    String write_time;
    String name;
    int comment_index;

    public int getComment_index() {
        return comment_index;
    }

    public void setComment_index(int comment_index) {
        this.comment_index = comment_index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getThunderid() {
        return thunderid;
    }

    public void setThunderid(int thunderid) {
        this.thunderid = thunderid;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getWrite_time() {
        return write_time;
    }

    public void setWrite_time(String write_time) {
        this.write_time = write_time;
    }
}
