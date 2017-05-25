package com.sopt.android.thunder.detail.model;

/**
 * Created by lmjin_000 on 2015-12-19.
 */
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by inbiz02 on 2016-01-11.
 */
public class Content implements Parcelable{
    public int thunderid;
    public String title;
    public String host;
    public String date;
    public String contents;
    public int type;
    public String address;
    public String latitude;
    public String url;
    public int groupid; //추가된 내용 2/21(일)

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getThunderid() {
        return thunderid;
    }

    public void setThunderid(int thunderid) {
        this.thunderid = thunderid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String longitude;


    public Content(){}

    protected Content(Parcel in) {
        thunderid = in.readInt();
        title = in.readString();
        host = in.readString();
        date = in.readString();
        contents = in.readString();
        type = in.readInt();
        address = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(thunderid);
        dest.writeString(title);
        dest.writeString(host);
        dest.writeString(date);
        dest.writeString(contents);
        dest.writeInt(type);
        dest.writeString(address);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Content> CREATOR = new Creator<Content>() {
        @Override
        public Content createFromParcel(Parcel in) {
            return new Content(in);
        }

        @Override
        public Content[] newArray(int size) {
            return new Content[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}