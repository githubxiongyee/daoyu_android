package com.daoyu.chat.module.home.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

public class AddNearbyFriendB extends BaseBean<AddNearbyFriendB.AddNearbyFriendData> {
    public AddNearbyFriendB() {
    }

    public static class AddNearbyFriendData implements android.os.Parcelable {
        /**
         * userId : 2
         * checkInCount : 1
         * lastCheckInDate : null
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

        public AddNearbyFriendData() {
        }

        protected AddNearbyFriendData(Parcel in) {
            this.userId = in.readString();
            this.checkInCount = in.readString();
            this.lastCheckInDate = in.readString();
            this.giftCount = in.readString();
        }

        public static final Creator<AddNearbyFriendData> CREATOR = new Creator<AddNearbyFriendData>() {
            @Override
            public AddNearbyFriendData createFromParcel(Parcel source) {
                return new AddNearbyFriendData(source);
            }

            @Override
            public AddNearbyFriendData[] newArray(int size) {
                return new AddNearbyFriendData[size];
            }
        };
    }
}
