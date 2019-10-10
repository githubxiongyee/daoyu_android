package com.daoyu.chat.module.mine.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;
import com.google.gson.annotations.SerializedName;

public class WeChatBean extends BaseBean<WeChatBean.WeChatData> {

    public static class WeChatData implements android.os.Parcelable {
        public String appid;
        public String noncestr;
        @SerializedName("package")
        public String packageX;
        public String partnerid;
        public String prepayid;
        public String sign;
        public String timestamp;


        public WeChatData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.appid);
            dest.writeString(this.noncestr);
            dest.writeString(this.packageX);
            dest.writeString(this.partnerid);
            dest.writeString(this.prepayid);
            dest.writeString(this.sign);
            dest.writeString(this.timestamp);
        }

        protected WeChatData(Parcel in) {
            this.appid = in.readString();
            this.noncestr = in.readString();
            this.packageX = in.readString();
            this.partnerid = in.readString();
            this.prepayid = in.readString();
            this.sign = in.readString();
            this.timestamp = in.readString();
        }

        public static final Creator<WeChatData> CREATOR = new Creator<WeChatData>() {
            @Override
            public WeChatData createFromParcel(Parcel source) {
                return new WeChatData(source);
            }

            @Override
            public WeChatData[] newArray(int size) {
                return new WeChatData[size];
            }
        };
    }
}
