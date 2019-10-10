package com.daoyu.chat.module.system.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class SignB extends BaseBean<SignB.SignData> {
    public static class SignData implements android.os.Parcelable {
        /**
         * userId : 19
         * checkInCount : 1
         * lastCheckInDate : 2019-09-06
         * giftCount : 1
         */

        public String userId;
        public String checkInCount;
        public String lastCheckInDate;
        public String giftCount;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.userId);
            dest.writeString(this.checkInCount);
            dest.writeString(this.lastCheckInDate);
            dest.writeString(this.giftCount);
        }

        public SignData() {
        }

        protected SignData(Parcel in) {
            this.userId = in.readString();
            this.checkInCount = in.readString();
            this.lastCheckInDate = in.readString();
            this.giftCount = in.readString();
        }

        public static final Creator<SignData> CREATOR = new Creator<SignData>() {
            @Override
            public SignData createFromParcel(Parcel source) {
                return new SignData(source);
            }

            @Override
            public SignData[] newArray(int size) {
                return new SignData[size];
            }
        };
    }
}
