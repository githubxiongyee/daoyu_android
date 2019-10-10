package com.daoyu.chat.module.mine.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class ReMainBean extends BaseBean<ReMainBean.ReMainData> {
    public static class ReMainData implements android.os.Parcelable {
        public Integer uId;
        public String singleNo;
        public String nickName;
        public Integer nums;
        public Double amountRealpay;
        public Integer sumUser;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeValue(this.uId);
            dest.writeString(this.singleNo);
            dest.writeString(this.nickName);
            dest.writeValue(this.nums);
            dest.writeValue(this.amountRealpay);
            dest.writeValue(this.sumUser);
        }

        public ReMainData() {
        }

        protected ReMainData(Parcel in) {
            this.uId = (Integer) in.readValue(Integer.class.getClassLoader());
            this.singleNo = in.readString();
            this.nickName = in.readString();
            this.nums = (Integer) in.readValue(Integer.class.getClassLoader());
            this.amountRealpay = (Double) in.readValue(Double.class.getClassLoader());
            this.sumUser = (Integer) in.readValue(Integer.class.getClassLoader());
        }

        public static final Creator<ReMainData> CREATOR = new Creator<ReMainData>() {
            @Override
            public ReMainData createFromParcel(Parcel source) {
                return new ReMainData(source);
            }

            @Override
            public ReMainData[] newArray(int size) {
                return new ReMainData[size];
            }
        };
    }
}
