package com.daoyu.chat.module.envelope.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

/**
 * 创建兑换B
 */
public class CreateChangeB extends BaseBean<CreateChangeB.CreateChangeData> {
    public static class CreateChangeData implements android.os.Parcelable {
        public int bId;
        public int oId;
        public String bOrderNumber;
        public long bOrderTime;
        public String bOrderStatus;
        public double bCreditTotal;
        public double bOriginalPrice;

        public int id;
        public String orderNumber;
        public double orderPrice;
        public double proPrice;
        public double salePrice;
        public String orderStatus;
        public String proName;
        public String proImg;
        public int adid;

        public CreateChangeData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.bId);
            dest.writeInt(this.oId);
            dest.writeString(this.bOrderNumber);
            dest.writeLong(this.bOrderTime);
            dest.writeString(this.bOrderStatus);
            dest.writeDouble(this.bCreditTotal);
            dest.writeDouble(this.bOriginalPrice);
            dest.writeInt(this.id);
            dest.writeString(this.orderNumber);
            dest.writeDouble(this.orderPrice);
            dest.writeDouble(this.proPrice);
            dest.writeDouble(this.salePrice);
            dest.writeString(this.orderStatus);
            dest.writeString(this.proName);
            dest.writeString(this.proImg);
            dest.writeInt(this.adid);
        }

        protected CreateChangeData(Parcel in) {
            this.bId = in.readInt();
            this.oId = in.readInt();
            this.bOrderNumber = in.readString();
            this.bOrderTime = in.readLong();
            this.bOrderStatus = in.readString();
            this.bCreditTotal = in.readDouble();
            this.bOriginalPrice = in.readDouble();
            this.id = in.readInt();
            this.orderNumber = in.readString();
            this.orderPrice = in.readDouble();
            this.proPrice = in.readDouble();
            this.salePrice = in.readDouble();
            this.orderStatus = in.readString();
            this.proName = in.readString();
            this.proImg = in.readString();
            this.adid = in.readInt();
        }

        public static final Creator<CreateChangeData> CREATOR = new Creator<CreateChangeData>() {
            @Override
            public CreateChangeData createFromParcel(Parcel source) {
                return new CreateChangeData(source);
            }

            @Override
            public CreateChangeData[] newArray(int size) {
                return new CreateChangeData[size];
            }
        };
    }
}
