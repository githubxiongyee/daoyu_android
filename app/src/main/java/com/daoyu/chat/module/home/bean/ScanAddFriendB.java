package com.daoyu.chat.module.home.bean;

import android.os.Parcel;

import com.daoyu.chat.base.BaseBean;

import java.util.ArrayList;

public class ScanAddFriendB extends BaseBean<ArrayList<ScanAddFriendB.ScanAddFrienData>> {
    public static class ScanAddFrienData implements android.os.Parcelable {
        public int id;
        public String friendMobile;
        public String friendNickName;
        public int userFriendId;
        public String isai;
        public String blacklist;
        public String fType;
        public String sex;
        public String headImg;
        public String agent_no;
        public String address_x;
        public String address_y;
        public String remarks;
        public String address_d;
        public String fremarks;
        public String ftype;


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.friendMobile);
            dest.writeString(this.friendNickName);
            dest.writeInt(this.userFriendId);
            dest.writeString(this.isai);
            dest.writeString(this.blacklist);
            dest.writeString(this.fType);
            dest.writeString(this.sex);
            dest.writeString(this.headImg);
            dest.writeString(this.agent_no);
            dest.writeString(this.address_x);
            dest.writeString(this.address_y);
            dest.writeString(this.remarks);
            dest.writeString(this.address_d);
            dest.writeString(this.fremarks);
            dest.writeString(this.ftype);
        }

        public ScanAddFrienData() {
        }

        protected ScanAddFrienData(Parcel in) {
            this.id = in.readInt();
            this.friendMobile = in.readString();
            this.friendNickName = in.readString();
            this.userFriendId = in.readInt();
            this.isai = in.readString();
            this.blacklist = in.readString();
            this.fType = in.readString();
            this.sex = in.readString();
            this.headImg = in.readString();
            this.agent_no = in.readString();
            this.address_x = in.readString();
            this.address_y = in.readString();
            this.remarks = in.readString();
            this.address_d = in.readString();
            this.fremarks = in.readString();
            this.ftype = in.readString();
        }

        public static final Creator<ScanAddFrienData> CREATOR = new Creator<ScanAddFrienData>() {
            @Override
            public ScanAddFrienData createFromParcel(Parcel source) {
                return new ScanAddFrienData(source);
            }

            @Override
            public ScanAddFrienData[] newArray(int size) {
                return new ScanAddFrienData[size];
            }
        };
    }
}
