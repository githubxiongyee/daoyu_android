package com.daoyu.chat.module.system.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SignInfoB extends BaseBean<SignInfoB.SignInfoData> {
    public static class SignInfoData implements android.os.Parcelable {
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

        public SignInfoData() {
        }

        protected SignInfoData(Parcel in) {
            this.userId = in.readString();
            this.checkInCount = in.readString();
            this.lastCheckInDate = in.readString();
            this.giftCount = in.readString();
        }

        public static final Creator<SignInfoData> CREATOR = new Creator<SignInfoData>() {
            @Override
            public SignInfoData createFromParcel(Parcel source) {
                return new SignInfoData(source);
            }

            @Override
            public SignInfoData[] newArray(int size) {
                return new SignInfoData[size];
            }
        };
    }
}
