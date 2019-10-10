package com.daoyu.chat.module.home.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class FriendInfoBean extends BaseBean<ArrayList<FriendInfoBean.FriendInfoData>> {

    public static class FriendInfoData implements android.os.Parcelable {
        public int id;
        public String realName;
        public String nickName;
        public String headImg;
        public String sex;
        public String mobile;
        public String credential;
        public String birthday;
        public String email;
        public String delStatus;
        public String remarks;
        public String agentNo;
        public String addressX;
        public String addressY;
        public String addressD;
        public String registerTime;
        public String updateTime;
        public String fremarks;


        public FriendInfoData() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.realName);
            dest.writeString(this.nickName);
            dest.writeString(this.headImg);
            dest.writeString(this.sex);
            dest.writeString(this.mobile);
            dest.writeString(this.credential);
            dest.writeString(this.birthday);
            dest.writeString(this.email);
            dest.writeString(this.delStatus);
            dest.writeString(this.remarks);
            dest.writeString(this.agentNo);
            dest.writeString(this.addressX);
            dest.writeString(this.addressY);
            dest.writeString(this.addressD);
            dest.writeString(this.registerTime);
            dest.writeString(this.updateTime);
            dest.writeString(this.fremarks);
        }

        protected FriendInfoData(Parcel in) {
            this.id = in.readInt();
            this.realName = in.readString();
            this.nickName = in.readString();
            this.headImg = in.readString();
            this.sex = in.readString();
            this.mobile = in.readString();
            this.credential = in.readString();
            this.birthday = in.readString();
            this.email = in.readString();
            this.delStatus = in.readString();
            this.remarks = in.readString();
            this.agentNo = in.readString();
            this.addressX = in.readString();
            this.addressY = in.readString();
            this.addressD = in.readString();
            this.registerTime = in.readString();
            this.updateTime = in.readString();
            this.fremarks = in.readString();
        }

        public static final Creator<FriendInfoData> CREATOR = new Creator<FriendInfoData>() {
            @Override
            public FriendInfoData createFromParcel(Parcel source) {
                return new FriendInfoData(source);
            }

            @Override
            public FriendInfoData[] newArray(int size) {
                return new FriendInfoData[size];
            }
        };
    }
}
