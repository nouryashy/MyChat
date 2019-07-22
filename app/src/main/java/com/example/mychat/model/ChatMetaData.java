package com.example.mychat.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMetaData  implements Parcelable {
    private String mUserName;
    private String mUserEmail;
    private String mUserUID;

    public ChatMetaData() {

    }

    public ChatMetaData(String userName, String userEmail, String userUID) {
        mUserName = userName;
        mUserEmail = userEmail;
        mUserUID = userUID;
    }

    protected ChatMetaData(Parcel in) {
        mUserName = in.readString();
        mUserEmail = in.readString();
        mUserUID = in.readString();
    }

    public static final Creator<ChatMetaData> CREATOR = new Creator<ChatMetaData>() {
        @Override
        public ChatMetaData createFromParcel(Parcel in) {
            return new ChatMetaData(in);
        }

        @Override
        public ChatMetaData[] newArray(int size) {
            return new ChatMetaData[size];
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
        dest.writeString(mUserUID);
    }
}
