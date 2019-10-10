package com.daoyu.chat.module.home.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class QrOrderBean extends BaseBean<QrOrderBean.QrOrderData> {

    public static class QrOrderData implements android.os.Parcelable {
        public int bId;
        public int oId;
        public String bName;
        public String bOrderNumber;
        public long bOrderTime;
        public String bOrderStatus;
        public double bSolePrice;
        public double bCreditTotal;

        public QrOrderData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.bId);
            dest.writeInt(this.oId);
            dest.writeString(this.bName);
            dest.writeString(this.bOrderNumber);
            dest.writeLong(this.bOrderTime);
            dest.writeString(this.bOrderStatus);
            dest.writeDouble(this.bSolePrice);
            dest.writeDouble(this.bCreditTotal);
        }

        protected QrOrderData(Parcel in) {
            this.bId = in.readInt();
            this.oId = in.readInt();
            this.bName = in.readString();
            this.bOrderNumber = in.readString();
            this.bOrderTime = in.readLong();
            this.bOrderStatus = in.readString();
            this.bSolePrice = in.readDouble();
            this.bCreditTotal = in.readDouble();
        }

        public static final Creator<QrOrderData> CREATOR = new Creator<QrOrderData>() {
            @Override
            public QrOrderData createFromParcel(Parcel source) {
                return new QrOrderData(source);
            }

            @Override
            public QrOrderData[] newArray(int size) {
                return new QrOrderData[size];
            }
        };
    }
}
