package com.daoyu.chat.module.envelope.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class CreateOrderBean extends BaseBean<CreateOrderBean.CreateOrderData> {
    public static class CreateOrderData implements android.os.Parcelable {
        public int oId;
        public String bOrderNumber;
        public long bOrderTime;
        public String bOrderStatus;
        public double bOriginalPrice;
        public double bSolePrice;
        public double bCreditTotal;

        public CreateOrderData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.oId);
            dest.writeString(this.bOrderNumber);
            dest.writeLong(this.bOrderTime);
            dest.writeString(this.bOrderStatus);
            dest.writeDouble(this.bOriginalPrice);
            dest.writeDouble(this.bSolePrice);
            dest.writeDouble(this.bCreditTotal);
        }

        protected CreateOrderData(Parcel in) {
            this.oId = in.readInt();
            this.bOrderNumber = in.readString();
            this.bOrderTime = in.readLong();
            this.bOrderStatus = in.readString();
            this.bOriginalPrice = in.readDouble();
            this.bSolePrice = in.readDouble();
            this.bCreditTotal = in.readDouble();
        }

        public static final Creator<CreateOrderData> CREATOR = new Creator<CreateOrderData>() {
            @Override
            public CreateOrderData createFromParcel(Parcel source) {
                return new CreateOrderData(source);
            }

            @Override
            public CreateOrderData[] newArray(int size) {
                return new CreateOrderData[size];
            }
        };
    }
}
