package com.daoyu.chat.base;

import android.os.Parcel;
import android.os.Parcelable;

public class BaseBean<T> implements Parcelable {
    public int code;
    public String msg;
    public boolean success;
    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeByte(this.success ? (byte) 1 : (byte) 0);
    }

    public BaseBean() {
    }

    protected BaseBean(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        this.success = in.readByte() != 0;
    }

    public static final Creator<BaseBean> CREATOR = new Creator<BaseBean>() {
        @Override
        public BaseBean createFromParcel(Parcel source) {
            return new BaseBean(source);
        }

        @Override
        public BaseBean[] newArray(int size) {
            return new BaseBean[size];
        }
    };
}
