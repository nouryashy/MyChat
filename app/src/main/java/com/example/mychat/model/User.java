package com.example.mychat.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private String mUserName;
    private String mUserEmail;
    private long mUserCreateDate;
    private String mUserUID;

    public User() {
    }

    public User(String userName, String userEmail, long userCreateDate, String userUID) {
        mUserName = userName;
        mUserEmail = userEmail;
        mUserCreateDate = userCreateDate;
        mUserUID = userUID;
    }
    public User(String userName, String userEmail, String userUID) {
        mUserName = userName;
        mUserEmail = userEmail;
        mUserUID = userUID;
    }


      protected User(Parcel in) {
        mUserName = in.readString();
        mUserEmail = in.readString();
        mUserCreateDate = in.readLong();
        mUserUID = in.readString();
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

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getUserEmail() {
        return mUserEmail;
    }

    public void setUserEmail(String userEmail) {
        mUserEmail = userEmail;
    }

    public long getUserCreateDate() {
        return mUserCreateDate;
    }

    public void setUserCreateDate(long userCreateDate) {
        mUserCreateDate = userCreateDate;
    }

    public String getUserUID() {
        return mUserUID;
    }

    public void setUserUID(String userUID) {
        mUserUID = userUID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mUserName);
        dest.writeString(mUserEmail);
        dest.writeLong(mUserCreateDate);
        dest.writeString(mUserUID);
    }
}
