package com.daoyu.chat.module.chat.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class LocalFriendData implements Parcelable {
    public String remarks;
    public int status;//0: 正常 1:拉黑 2:未添加

    public LocalFriendData(String remarks, int status) {
        this.remarks = remarks;
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.remarks);
        dest.writeInt(this.status);
    }

    public LocalFriendData() {
    }

    protected LocalFriendData(Parcel in) {
        this.remarks = in.readString();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<LocalFriendData> CREATOR = new Parcelable.Creator<LocalFriendData>() {
        @Override
        public LocalFriendData createFromParcel(Parcel source) {
            return new LocalFriendData(source);
        }

        @Override
        public LocalFriendData[] newArray(int size) {
            return new LocalFriendData[size];
        }
    };
}
