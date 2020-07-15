package com.example.learningapp.data.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "users", indices = {@Index(value = "user_name", unique = true)})
public class User implements Parcelable {
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    public String userId;
    @ColumnInfo(name = "user_name")
    public String userName;
    @ColumnInfo(name = "user_sign")
    public String userSign;

    @Ignore
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
