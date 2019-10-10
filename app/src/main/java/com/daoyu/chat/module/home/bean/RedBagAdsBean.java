package com.daoyu.chat.module.home.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class RedBagAdsBean extends BaseBean<ArrayList<RedBagAdsBean.RedBagData>> {

    public static class RedBagData implements android.os.Parcelable {
        public int pId;
        public String productNo;
        public String productName;
        public String productDesc;
        public double originalPrice;
        public double salePrice;
        public String thumbPicUrl;
        public int stockQuantity;
        public int soldQuantity;
        public double hongbaoPrice;
        public String hongbaoSta;

        public RedBagData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.pId);
            dest.writeString(this.productNo);
            dest.writeString(this.productName);
            dest.writeString(this.productDesc);
            dest.writeDouble(this.originalPrice);
            dest.writeDouble(this.salePrice);
            dest.writeString(this.thumbPicUrl);
            dest.writeInt(this.stockQuantity);
            dest.writeInt(this.soldQuantity);
            dest.writeDouble(this.hongbaoPrice);
            dest.writeString(this.hongbaoSta);
        }

        protected RedBagData(Parcel in) {
            this.pId = in.readInt();
            this.productNo = in.readString();
            this.productName = in.readString();
            this.productDesc = in.readString();
            this.originalPrice = in.readDouble();
            this.salePrice = in.readDouble();
            this.thumbPicUrl = in.readString();
            this.stockQuantity = in.readInt();
            this.soldQuantity = in.readInt();
            this.hongbaoPrice = in.readDouble();
            this.hongbaoSta = in.readString();
        }

        public static final Creator<RedBagData> CREATOR = new Creator<RedBagData>() {
            @Override
            public RedBagData createFromParcel(Parcel source) {
                return new RedBagData(source);
            }

            @Override
            public RedBagData[] newArray(int size) {
                return new RedBagData[size];
            }
        };
    }
}
