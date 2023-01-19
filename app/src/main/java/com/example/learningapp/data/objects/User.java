package com.example.learningapp.data.objects;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    public String userId;
    public String userName;
    public String userSign;

    private String ignore;
    // @Embedded  // 嵌套？

    public User(){

    }



    protected User(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userSign = in.readString();
        ignore = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userName);
        dest.writeString(userSign);
        dest.writeString(ignore);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public String getIgnore() {
        return ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

}
