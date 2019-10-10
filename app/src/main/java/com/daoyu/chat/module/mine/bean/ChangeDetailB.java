package com.daoyu.chat.module.mine.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ChangeDetailB extends BaseBean<ChangeDetailB.ChangeDetailData> {
    public static class ChangeDetailData implements android.os.Parcelable {
        public int id;
        public String orderNumber;
        public String orderStatus;
        public String expCompany;
        public String expCode;
        public String expNo;
        public String signName;
        public String signPhone;
        public String signAddress;
        public long orderTime;
        public String payWay;
        public long payTime;
        public String amountPayable;
        public double amountRealpay;
        public String remarks;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.orderNumber);
            dest.writeString(this.orderStatus);
            dest.writeString(this.expCompany);
            dest.writeString(this.expCode);
            dest.writeString(this.expNo);
            dest.writeString(this.signName);
            dest.writeString(this.signPhone);
            dest.writeString(this.signAddress);
            dest.writeLong(this.orderTime);
            dest.writeString(this.payWay);
            dest.writeLong(this.payTime);
            dest.writeString(this.amountPayable);
            dest.writeDouble(this.amountRealpay);
            dest.writeString(this.remarks);
        }

        public ChangeDetailData() {
        }

        protected ChangeDetailData(Parcel in) {
            this.id = in.readInt();
            this.orderNumber = in.readString();
            this.orderStatus = in.readString();
            this.expCompany = in.readString();
            this.expCode = in.readString();
            this.expNo = in.readString();
            this.signName = in.readString();
            this.signPhone = in.readString();
            this.signAddress = in.readString();
            this.orderTime = in.readLong();
            this.payWay = in.readString();
            this.payTime = in.readLong();
            this.amountPayable = in.readString();
            this.amountRealpay = in.readDouble();
            this.remarks = in.readString();
        }

        public static final Creator<ChangeDetailData> CREATOR = new Creator<ChangeDetailData>() {
            @Override
            public ChangeDetailData createFromParcel(Parcel source) {
                return new ChangeDetailData(source);
            }

            @Override
            public ChangeDetailData[] newArray(int size) {
                return new ChangeDetailData[size];
            }
        };
    }
}
