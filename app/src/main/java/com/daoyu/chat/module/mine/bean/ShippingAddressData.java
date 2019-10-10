package com.daoyu.chat.module.mine.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class ShippingAddressData implements Parcelable {
    public int id;
    public int createId;
    public long createTime;
    public int updateId;
    public long updateTime;
    public int uId;
    public String aNick;
    public String aPhone;
    public String aAddress;
    public String isDef;

    public ShippingAddressData(int id, String aNick, String aPhone, String aAddress) {
        this.id = id;
        this.aNick = aNick;
        this.aPhone = aPhone;
        this.aAddress = aAddress;
    }

    public ShippingAddressData(int id, String aNick, String aPhone, String aAddress,String isDef) {
        this.id = id;
        this.aNick = aNick;
        this.aPhone = aPhone;
        this.aAddress = aAddress;
        this.isDef = isDef;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.createId);
        dest.writeLong(this.createTime);
        dest.writeInt(this.updateId);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.uId);
        dest.writeString(this.aNick);
        dest.writeString(this.aPhone);
        dest.writeString(this.aAddress);
        dest.writeString(this.isDef);
    }

    public ShippingAddressData() {
    }

    protected ShippingAddressData(Parcel in) {
        this.id = in.readInt();
        this.createId = in.readInt();
        this.createTime = in.readLong();
        this.updateId = in.readInt();
        this.updateTime = in.readLong();
        this.uId = in.readInt();
        this.aNick = in.readString();
        this.aPhone = in.readString();
        this.aAddress = in.readString();
        this.isDef = in.readString();
    }

    public static final Parcelable.Creator<ShippingAddressData> CREATOR = new Parcelable.Creator<ShippingAddressData>() {
        @Override
        public ShippingAddressData createFromParcel(Parcel source) {
            return new ShippingAddressData(source);
        }

        @Override
        public ShippingAddressData[] newArray(int size) {
            return new ShippingAddressData[size];
        }
    };
}
