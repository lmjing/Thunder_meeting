package com.sopt.android.thunder.detail.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class User implements Parcelable {
    // TODO: 6. Gson 객체 생성 + Message 모델 선언(생성자까지)
    // 댓글
    public static final Gson GSON = new GsonBuilder().create();

    public String name, id, password;
    public String birth;
    public String number;//휴대폰 번호
    public String registerid;
    public int groupid;
    public int authority;

    public int getAuthority() {
        return authority;
    }

    public void setAuthority(int authority) {
        this.authority = authority;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setGroupid(int groupid) {
        this.groupid = groupid;
    }

    public String getRegisterid() {
        return registerid;
    }

    public void setRegisterid(String registerid) {
        this.registerid = registerid;
    }

    public User(){ }

    protected User(Parcel in) { //parcel 데이터로 전달된 것을 객체형으로 만드는 생성자
        name = in.readString();
        id = in.readString();
        password = in.readString();
        birth = in.readString();
        number = in.readString();
        registerid = in.readString();
        authority = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() { //parcel 데이터를 객체로 다시 만들어 주는 단계.
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {   //객체를 parcel 데이터로 만드는 단계
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(password);
        dest.writeString(birth);
        dest.writeString(number);
        dest.writeString(registerid);
        dest.writeInt(authority);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}














