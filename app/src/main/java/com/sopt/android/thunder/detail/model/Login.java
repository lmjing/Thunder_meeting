package com.sopt.android.thunder.detail.model;

/**
 * Created by lmjin_000 on 2015-12-19.
 */
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by inbiz02 on 2016-01-11.
 */
public class Login implements Parcelable {

    public String id =null;
    public String session;
    public String registerid;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getRegisterid() {
        return registerid;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }



    public Login(){}

    protected Login(Parcel in) { //parcel 데이터로 전달된 것을 객체형으로 만드는 생성자
        id = in.readString();
        session = in.readString();
        registerid = in.readString();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Login> CREATOR = new Creator<Login>() { //parcel 데이터를 객체로 다시 만들어 주는 단계.
        @Override
        public Login createFromParcel(Parcel in) {
            return new Login(in);
        }

        @Override
        public Login[] newArray(int size) {
            return new Login[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(session);
        dest.writeString(registerid);
    }

}